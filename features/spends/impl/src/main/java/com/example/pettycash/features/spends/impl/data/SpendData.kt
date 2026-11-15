package com.example.pettycash.features.spends.impl.data

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
data class SpendItem(
    val description: String = "",
    val amountKes: Long = 0,
)

@Serializable
data class SpendRecord(
    val id: String,
    val amountKes: Long,
    val items: List<SpendItem> = emptyList(),
    val category: String,
    val project: String,
    val site: String,
    val paidTo: String,
    val method: String,
    val mpesaCode: String = "",
    val hasReceipt: Boolean = false,
    val recordedBy: String,
    val createdAt: Long,
)

data class SpendContext(
    val categories: List<String>,
    val projects: List<String>,
    val sites: List<String>,
    val memberName: String,
    val floatKes: Long,
)

interface SpendsRepository {
    val snapshot: Flow<Pair<List<SpendRecord>, SpendContext>>
    suspend fun saveSpend(
        amountKes: Long,
        items: List<SpendItem>,
        category: String,
        project: String,
        site: String,
        paidTo: String,
        method: String,
        mpesaCode: String,
        hasReceipt: Boolean,
    )
}

class LocalSpendsRepository(
    private val local: ActivityLocalDataSource,
    private val companyLocal: CompanyLocalDataSource,
    private val inviteLocal: InviteLocalDataSource,
    private val json: Json,
) : SpendsRepository {

    override val snapshot: Flow<Pair<List<SpendRecord>, SpendContext>> =
        combine(
            local.spendsJson,
            companyLocal.profileJson,
            inviteLocal.membersJson,
            inviteLocal.memberName,
            local.topupsJson,
        ) { spendsRaw, profileRaw, membersRaw, memberName, topupsRaw ->
            val spends = spendsRaw?.let { decodeSpends(it) } ?: emptyList()
            val profile = profileRaw?.let { decodeProfile(it) }
            val starting = profile?.floatStartingAmount?.filter { it.isDigit() }?.toLongOrNull()
                ?: DEFAULT_FLOAT_KES
            val topups = topupsRaw?.let {
                runCatching {
                    json.decodeFromString(ListSerializer(TopUpDto.serializer()), it)
                }.getOrDefault(emptyList())
            } ?: emptyList()
            spends to SpendContext(
                categories = profile?.categories?.takeIf { it.isNotEmpty() } ?: DEFAULT_CATEGORIES,
                projects = profile?.projects ?: emptyList(),
                sites = profile?.sites ?: emptyList(),
                memberName = memberName?.takeIf { it.isNotBlank() } ?: "Custodian",
                floatKes = (starting + topups.sumOf { it.amountKes } - spends.sumOf { it.amountKes }).coerceAtLeast(0),
            )
        }

    override suspend fun saveSpend(
        amountKes: Long,
        items: List<SpendItem>,
        category: String,
        project: String,
        site: String,
        paidTo: String,
        method: String,
        mpesaCode: String,
        hasReceipt: Boolean,
    ) {
        val (_, context) = snapshot.first()
        val spends = currentSpends()
        val record = SpendRecord(
            id = UUID.randomUUID().toString(),
            amountKes = amountKes,
            items = items,
            category = category,
            project = project,
            site = site,
            paidTo = paidTo,
            method = method,
            mpesaCode = mpesaCode,
            hasReceipt = hasReceipt,
            recordedBy = context.memberName,
            createdAt = System.currentTimeMillis(),
        )
        local.saveSpendsJson(json.encodeToString(ListSerializer(SpendRecord.serializer()), spends + record))
    }

    private suspend fun currentSpends(): List<SpendRecord> {
        val raw = local.spendsJson.first() ?: return emptyList()
        return decodeSpends(raw)
    }

    private fun decodeSpends(raw: String): List<SpendRecord> =
        json.decodeFromString(ListSerializer(SpendRecord.serializer()), raw)

    @Serializable
    private data class ProfileDto(
        val floatStartingAmount: String = "",
        val projects: List<String> = emptyList(),
        val sites: List<String> = emptyList(),
        val categories: List<String> = emptyList(),
    )

    @Serializable
    private data class TopUpDto(val amountKes: Long = 0)

    @Serializable
    private data class MemberDto(val name: String = "", val isSelf: Boolean = false)

    private fun decodeProfile(raw: String): ProfileDto? =
        runCatching { json.decodeFromString(ProfileDto.serializer(), raw) }.getOrNull()

    private companion object {
        const val DEFAULT_FLOAT_KES = 18_450L
        val DEFAULT_CATEGORIES = listOf("Fuel", "Transport", "Supplies", "Meals", "Other")
    }
}
