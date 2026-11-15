package com.example.pettycash.features.requests.impl.data

import com.example.pettycash.datasource.local.CompanyLocalDataSource
import com.example.pettycash.datasource.local.InviteLocalDataSource
import com.example.pettycash.datasource.local.RequestLocalDataSource
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

@Serializable
data class CashRequest(
    val id: String,
    val amountKes: Long,
    val purpose: String,
    val category: String,
    val project: String,
    val site: String,
    val neededBy: String,
    val note: String = "",
    val requesterName: String,
    val reviewerName: String,
    val status: String,
    val voucherCode: String,
    val createdAt: Long,
    val paidReference: String = "",
    val reviewNote: String = "",
)

object RequestStatus {
    const val PENDING = "Pending"
    const val INFO_REQUESTED = "Info requested"
    const val APPROVED = "Approved"
    const val REJECTED = "Rejected"
    const val PAID = "Paid"
}

data class RequestContext(
    val perRequestLimitKes: Long,
    val projects: List<String>,
    val sites: List<String>,
    val categories: List<String>,
    val reviewerName: String,
    val memberName: String,
)

interface RequestsRepository {
    val snapshot: Flow<Pair<List<CashRequest>, RequestContext>>
    suspend fun requestById(id: String): CashRequest?
    suspend fun createRequest(
        amountKes: Long,
        purpose: String,
        category: String,
        project: String,
        site: String,
        neededBy: String,
        note: String,
    ): CashRequest

    suspend fun setStatus(id: String, status: String, paidReference: String = "", reviewNote: String = "")
}

class LocalRequestsRepository(
    private val local: RequestLocalDataSource,
    private val companyLocal: CompanyLocalDataSource,
    private val inviteLocal: InviteLocalDataSource,
    private val json: Json,
) : RequestsRepository {

    override val snapshot: Flow<Pair<List<CashRequest>, RequestContext>> =
        combine(
            local.requestsJson,
            companyLocal.profileJson,
            inviteLocal.membersJson,
            inviteLocal.memberName,
        ) { requestsRaw, profileRaw, membersRaw, memberName ->
            val requests = requestsRaw?.let { decodeRequests(it) } ?: emptyList()
            val profile = profileRaw?.let { decodeProfile(it) }
            val reviewer = membersRaw?.let { decodeReviewer(it) } ?: "your admin"
            requests to RequestContext(
                perRequestLimitKes = profile?.limitKes ?: DEFAULT_LIMIT_KES,
                projects = profile?.projects ?: emptyList(),
                sites = profile?.sites ?: emptyList(),
                categories = profile?.categories?.takeIf { it.isNotEmpty() } ?: DEFAULT_CATEGORIES,
                reviewerName = reviewer,
                memberName = memberName?.takeIf { it.isNotBlank() } ?: "You",
            )
        }

    override suspend fun requestById(id: String): CashRequest? {
        val (requests, _) = snapshot.first()
        return requests.firstOrNull { it.id == id }
    }

    override suspend fun createRequest(
        amountKes: Long,
        purpose: String,
        category: String,
        project: String,
        site: String,
        neededBy: String,
        note: String,
    ): CashRequest {
        val (_, context) = snapshot.first()
        val requests = currentRequests()
        val nextNumber = (requests.maxOfOrNull { it.voucherCode.removePrefix("PCV-").toIntOrNull() ?: 0 } ?: 1042) + 1
        val request = CashRequest(
            id = UUID.randomUUID().toString(),
            amountKes = amountKes,
            purpose = purpose,
            category = category,
            project = project,
            site = site,
            neededBy = neededBy,
            note = note,
            requesterName = context.memberName,
            reviewerName = context.reviewerName,
            status = RequestStatus.PENDING,
            voucherCode = "PCV-$nextNumber",
            createdAt = System.currentTimeMillis(),
        )
        local.saveRequestsJson(json.encodeToString(ListSerializer(CashRequest.serializer()), requests + request))
        return request
    }

    override suspend fun setStatus(id: String, status: String, paidReference: String, reviewNote: String) {
        val requests = currentRequests()
        local.saveRequestsJson(
            json.encodeToString(
                ListSerializer(CashRequest.serializer()),
                requests.map {
                    if (it.id == id) {
                        it.copy(
                            status = status,
                            paidReference = paidReference.ifBlank { it.paidReference },
                            reviewNote = reviewNote.ifBlank { it.reviewNote },
                        )
                    } else it
                },
            ),
        )
    }

    private suspend fun currentRequests(): List<CashRequest> {
        val raw = local.requestsJson.first() ?: return emptyList()
        return decodeRequests(raw)
    }

    private fun decodeRequests(raw: String): List<CashRequest> =
        json.decodeFromString(ListSerializer(CashRequest.serializer()), raw)

    @Serializable
    private data class ProfileDto(
        val floatSingleSpendLimit: String = "",
        val projects: List<String> = emptyList(),
        val sites: List<String> = emptyList(),
        val categories: List<String> = emptyList(),
    ) {
        val limitKes: Long get() = floatSingleSpendLimit.filter { it.isDigit() }.toLongOrNull() ?: DEFAULT_LIMIT_KES
    }

    @Serializable
    private data class MemberDto(val name: String = "", val isSelf: Boolean = false, val role: String = "")

    private fun decodeProfile(raw: String): ProfileDto? =
        runCatching { json.decodeFromString(ProfileDto.serializer(), raw) }.getOrNull()

    private fun decodeReviewer(raw: String): String? =
        runCatching {
            json.decodeFromString(ListSerializer(MemberDto.serializer()), raw)
                .firstOrNull {
                    !it.isSelf && (it.role.contains("approver", true) || it.role.equals("Admin", true))
                }
                ?.name
        }.getOrNull()

    private companion object {
        const val DEFAULT_LIMIT_KES = 5_000L
        val DEFAULT_CATEGORIES = listOf("Fuel", "Transport", "Supplies", "Meals", "Other")
    }
}
