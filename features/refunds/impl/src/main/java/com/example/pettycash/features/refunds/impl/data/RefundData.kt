package com.example.pettycash.features.refunds.impl.data

import com.example.pettycash.datasource.local.ActivityLocalDataSource
import com.example.pettycash.datasource.local.CompanyLocalDataSource
import com.example.pettycash.datasource.local.InviteLocalDataSource
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

@Serializable
data class RefundClaim(
    val id: String,
    val amountKes: Long,
    val purpose: String,
    val category: String,
    val date: String,
    val from: String = "",
    val to: String = "",
    val note: String = "",
    val proof: String,
    val refundTo: String,
    val status: String,
    val claimedBy: String,
    val createdAt: Long,
)

object RefundStatus {
    const val SUBMITTED = "Submitted"
}

data class RefundContext(
    val categories: List<String>,
    val memberName: String,
    val memberPhone: String,
)

interface RefundsRepository {
    val snapshot: Flow<Pair<List<RefundClaim>, RefundContext>>
    suspend fun saveClaim(
        amountKes: Long,
        purpose: String,
        category: String,
        date: String,
        from: String,
        to: String,
        note: String,
        proof: String,
        refundTo: String,
    )
}

class LocalRefundsRepository(
    private val local: ActivityLocalDataSource,
    private val companyLocal: CompanyLocalDataSource,
    private val inviteLocal: InviteLocalDataSource,
    private val json: Json,
) : RefundsRepository {

    override val snapshot: Flow<Pair<List<RefundClaim>, RefundContext>> =
        combine(
            local.refundsJson,
            companyLocal.profileJson,
            inviteLocal.memberName,
            inviteLocal.memberPhone,
        ) { refundsRaw, profileRaw, memberName, memberPhone ->
            val refunds = refundsRaw?.let {
                runCatching {
                    json.decodeFromString(ListSerializer(RefundClaim.serializer()), it)
                }.getOrDefault(emptyList())
            } ?: emptyList()
            val profile = profileRaw?.let {
                runCatching { json.decodeFromString(ProfileDto.serializer(), it) }.getOrNull()
            }
            refunds to RefundContext(
                categories = profile?.categories?.takeIf { it.isNotEmpty() } ?: DEFAULT_CATEGORIES,
                memberName = memberName?.takeIf { it.isNotBlank() } ?: "You",
                memberPhone = memberPhone ?: "",
            )
        }

    override suspend fun saveClaim(
        amountKes: Long,
        purpose: String,
        category: String,
        date: String,
        from: String,
        to: String,
        note: String,
        proof: String,
        refundTo: String,
    ) {
        val (_, context) = snapshot.first()
        val claims = currentClaims()
        val claim = RefundClaim(
            id = UUID.randomUUID().toString(),
            amountKes = amountKes,
            purpose = purpose,
            category = category,
            date = date,
            from = from,
            to = to,
            note = note,
            proof = proof,
            refundTo = refundTo.ifBlank { context.memberPhone },
            status = RefundStatus.SUBMITTED,
            claimedBy = context.memberName,
            createdAt = System.currentTimeMillis(),
        )
        local.saveRefundsJson(json.encodeToString(ListSerializer(RefundClaim.serializer()), claims + claim))
    }

    private suspend fun currentClaims(): List<RefundClaim> {
        val raw = local.refundsJson.first() ?: return emptyList()
        return runCatching {
            json.decodeFromString(ListSerializer(RefundClaim.serializer()), raw)
        }.getOrDefault(emptyList())
    }

    @Serializable
    private data class ProfileDto(val categories: List<String> = emptyList())

    private companion object {
        val DEFAULT_CATEGORIES = listOf("Fuel", "Transport", "Supplies", "Meals", "Other")
    }
}
