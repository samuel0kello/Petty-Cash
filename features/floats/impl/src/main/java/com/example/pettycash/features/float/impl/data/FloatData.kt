package com.example.pettycash.features.floats.impl.data

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
data class TopUpRecord(
    val id: String,
    val amountKes: Long,
    val reference: String,
    val createdAt: Long,
)

@Serializable
data class SpendRow(
    val id: String,
    val amountKes: Long,
    val paidTo: String,
    val category: String,
    val hasReceipt: Boolean,
    val createdAt: Long,
)

data class FloatState(
    val floatName: String,
    val custodianName: String,
    val balanceKes: Long,
    val issuedKes: Long,
    val spentKes: Long,
)

data class FloatSnapshot(
    val state: FloatState,
    val spends: List<SpendRow>,
)

interface FloatRepository {
    val snapshot: Flow<FloatSnapshot>
    suspend fun addTopUp(amountKes: Long)
}

class LocalFloatRepository(
    private val local: ActivityLocalDataSource,
    private val companyLocal: CompanyLocalDataSource,
    private val inviteLocal: InviteLocalDataSource,
    private val json: Json,
) : FloatRepository {

    override val snapshot: Flow<FloatSnapshot> =
        combine(
            companyLocal.profileJson,
            local.spendsJson,
            local.topupsJson,
            inviteLocal.memberName,
        ) { profileRaw, spendsRaw, topupsRaw, memberName ->
            val profile = profileRaw?.let {
                runCatching { json.decodeFromString(ProfileDto.serializer(), it) }.getOrNull()
            }
            val spends = spendsRaw?.let {
                runCatching {
                    json.decodeFromString(ListSerializer(SpendRow.serializer()), it)
                }.getOrDefault(emptyList())
            } ?: emptyList()
            val topups = topupsRaw?.let {
                runCatching {
                    json.decodeFromString(ListSerializer(TopUpRecord.serializer()), it)
                }.getOrDefault(emptyList())
            } ?: emptyList()
            val starting = profile?.floatStartingAmount?.filter { it.isDigit() }?.toLongOrNull()
                ?: DEFAULT_FLOAT_KES
            val spent = spends.sumOf { it.amountKes }
            FloatSnapshot(
                state = FloatState(
                    floatName = profile?.floatName?.ifBlank { "Main float" } ?: "Main float",
                    custodianName = profile?.floatCustodian?.ifBlank { memberName ?: "Custodian" }
                        ?: (memberName ?: "Custodian"),
                    balanceKes = (starting + topups.sumOf { it.amountKes } - spent).coerceAtLeast(0),
                    issuedKes = starting + topups.sumOf { it.amountKes },
                    spentKes = spent,
                ),
                spends = spends.sortedByDescending { it.createdAt },
            )
        }

    override suspend fun addTopUp(amountKes: Long) {
        val topups = currentTopups()
        val record = TopUpRecord(
            id = UUID.randomUUID().toString(),
            amountKes = amountKes,
            reference = "TOP${(1000..9999).random()}",
            createdAt = System.currentTimeMillis(),
        )
        local.saveTopupsJson(json.encodeToString(ListSerializer(TopUpRecord.serializer()), topups + record))
    }

    private suspend fun currentTopups(): List<TopUpRecord> {
        val raw = local.topupsJson.first() ?: return emptyList()
        return runCatching {
            json.decodeFromString(ListSerializer(TopUpRecord.serializer()), raw)
        }.getOrDefault(emptyList())
    }

    @Serializable
    private data class ProfileDto(
        val floatName: String = "",
        val floatStartingAmount: String = "",
        val floatCustodian: String = "",
    )

    private companion object {
        const val DEFAULT_FLOAT_KES = 18_450L
    }
}
