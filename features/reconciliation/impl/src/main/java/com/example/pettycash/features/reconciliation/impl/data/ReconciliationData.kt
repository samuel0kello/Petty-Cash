package com.example.pettycash.features.reconciliation.impl.data

import com.example.pettycash.datasource.local.ActivityLocalDataSource
import com.example.pettycash.datasource.local.CompanyLocalDataSource
import com.example.pettycash.datasource.local.HomeLocalDataSource
import com.example.pettycash.datasource.local.InviteLocalDataSource
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

enum class ReconciliationStatus { Submitted, Reviewed, Closed }

@Serializable
data class ReconciliationRecord(
    val id: String,
    val month: String,
    val countedKes: Long,
    val expectedKes: Long,
    val varianceKes: Long,
    val explanation: String,
    val status: String,
    val createdAt: Long,
    val unreceiptedCount: Int,
)

data class ReconciliationContext(
    val expectedKes: Long,
    val floatName: String,
    val unreceiptedSpends: List<UnreceiptedSpend>,
)

data class UnreceiptedSpend(
    val id: String,
    val description: String,
    val amountKes: Long,
)

data class ReconciliationSnapshot(
    val record: ReconciliationRecord?,
    val context: ReconciliationContext?,
    val isAdmin: Boolean = false,
)

interface ReconciliationRepository {
    val snapshot: Flow<ReconciliationSnapshot>
    suspend fun submit(countedKes: Long, explanation: String, unreceiptedCount: Int)
    suspend fun closeMonth()
}

class LocalReconciliationRepository(
    private val local: ActivityLocalDataSource,
    private val companyLocal: CompanyLocalDataSource,
    private val inviteLocal: InviteLocalDataSource,
    private val homeLocal: HomeLocalDataSource,
    private val json: Json,
) : ReconciliationRepository {

    override val snapshot: Flow<ReconciliationSnapshot> = combine(
        local.reconciliationJson,
        companyLocal.profileJson,
        local.spendsJson,
        local.topupsJson,
        homeLocal.selectedRole,
    ) { reconRaw, profileRaw, spendsRaw, topupsRaw, role ->
        val record = reconRaw?.let {
            runCatching { json.decodeFromString(ReconciliationRecord.serializer(), it) }.getOrNull()
        }
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
                json.decodeFromString(ListSerializer(TopUpRow.serializer()), it)
            }.getOrDefault(emptyList())
        } ?: emptyList()
        val starting = profile?.floatStartingAmount?.filter { it.isDigit() }?.toLongOrNull()
            ?: DEFAULT_FLOAT_KES
        val expected = (starting + topups.sumOf { it.amountKes } - spends.sumOf { it.amountKes })
            .coerceAtLeast(0)
        ReconciliationSnapshot(
            record = record,
            isAdmin = role == "Admin",
            context = ReconciliationContext(
                expectedKes = expected,
                floatName = profile?.floatName?.ifBlank { "Main float" } ?: "Main float",
                unreceiptedSpends = spends.filter { !it.hasReceipt }.map {
                    UnreceiptedSpend(
                        id = it.id,
                        description = it.paidTo.ifBlank { it.category.ifBlank { "Spend" } },
                        amountKes = it.amountKes,
                    )
                },
            ),
        )
    }

    override suspend fun submit(countedKes: Long, explanation: String, unreceiptedCount: Int) {
        val ctx = snapshot.first().context ?: return
        val month = java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.US).format(java.util.Date())
        val record = ReconciliationRecord(
            id = UUID.randomUUID().toString(),
            month = month,
            countedKes = countedKes,
            expectedKes = ctx.expectedKes,
            varianceKes = countedKes - ctx.expectedKes,
            explanation = explanation,
            status = ReconciliationStatus.Submitted.name,
            createdAt = System.currentTimeMillis(),
            unreceiptedCount = unreceiptedCount,
        )
        local.saveReconciliationJson(json.encodeToString(ReconciliationRecord.serializer(), record))
    }

    override suspend fun closeMonth() {
        val current = local.reconciliationJson.first()?.let {
            runCatching { json.decodeFromString(ReconciliationRecord.serializer(), it) }.getOrNull()
        } ?: return
        local.saveReconciliationJson(
            json.encodeToString(
                ReconciliationRecord.serializer(),
                current.copy(status = ReconciliationStatus.Closed.name),
            ),
        )
    }

    @Serializable
    private data class ProfileDto(
        val floatName: String = "",
        val floatStartingAmount: String = "",
    )

    @Serializable
    private data class SpendRow(
        val id: String = "",
        val amountKes: Long = 0,
        val paidTo: String = "",
        val category: String = "",
        val hasReceipt: Boolean = false,
    )

    @Serializable
    private data class TopUpRow(val amountKes: Long = 0)

    private companion object {
        const val DEFAULT_FLOAT_KES = 18_450L
    }
}
