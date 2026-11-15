package com.example.pettycash.features.records.impl.data

import com.example.pettycash.datasource.local.ActivityLocalDataSource
import com.example.pettycash.datasource.local.CompanyLocalDataSource
import com.example.pettycash.datasource.local.RequestLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

enum class ActivityKind { Spend, Refund, Request, TopUp }

data class ActivityItem(
    val id: String,
    val kind: ActivityKind,
    val kindId: String,
    val title: String,
    val subtitle: String,
    val amountKes: Long,
    val incoming: Boolean,
    val statusLabel: String,
    val status: String,
    val createdAt: Long,
    val project: String = "",
    val site: String = "",
    val paidTo: String = "",
    val reference: String = "",
    val voucherCode: String = "",
    val hasReceipt: Boolean = false,
    val method: String = "",
    val category: String = "",
    val note: String = "",
    val person: String = "",
)

interface RecordsRepository {
    val items: Flow<List<ActivityItem>>
    val companyName: Flow<String>
    fun find(kind: ActivityKind, id: String): Flow<ActivityItem?>
}

class LocalRecordsRepository(
    private val activityLocal: ActivityLocalDataSource,
    private val requestLocal: RequestLocalDataSource,
    private val companyLocal: CompanyLocalDataSource,
    private val json: Json,
) : RecordsRepository {

    override val items: Flow<List<ActivityItem>> = combine(
        activityLocal.spendsJson,
        activityLocal.refundsJson,
        activityLocal.topupsJson,
        requestLocal.requestsJson,
    ) { spendsRaw, refundsRaw, topupsRaw, requestsRaw ->
        buildList {
            spendsRaw?.let {
                runCatching {
                    json.decodeFromString(ListSerializer(SpendRow.serializer()), it)
                }.getOrDefault(emptyList())
            }?.forEach { row ->
                add(
                    ActivityItem(
                        id = "spend_${row.id}",
                        kind = ActivityKind.Spend,
                        kindId = row.id,
                        title = row.paidTo.ifBlank { row.items.firstOrNull()?.description ?: row.category },
                        subtitle = row.category,
                        amountKes = row.amountKes,
                        incoming = false,
                        statusLabel = if (row.hasReceipt) "Paid" else "No receipt",
                        status = row.status,
                        createdAt = row.createdAt,
                        project = row.project,
                        site = row.site,
                        paidTo = row.paidTo,
                        reference = row.mpesaCode,
                        hasReceipt = row.hasReceipt,
                        method = row.method,
                        category = row.category,
                        person = row.recordedBy,
                    ),
                )
            }
            refundsRaw?.let {
                runCatching {
                    json.decodeFromString(ListSerializer(RefundRow.serializer()), it)
                }.getOrDefault(emptyList())
            }?.forEach { row ->
                add(
                    ActivityItem(
                        id = "refund_${row.id}",
                        kind = ActivityKind.Refund,
                        kindId = row.id,
                        title = row.purpose,
                        subtitle = row.category,
                        amountKes = row.amountKes,
                        incoming = true,
                        statusLabel = row.status,
                        status = row.status,
                        createdAt = row.createdAt,
                        category = row.category,
                        note = row.note,
                        person = row.claimedBy,
                    ),
                )
            }
            topupsRaw?.let {
                runCatching {
                    json.decodeFromString(ListSerializer(TopUpRow.serializer()), it)
                }.getOrDefault(emptyList())
            }?.forEach { row ->
                add(
                    ActivityItem(
                        id = "topup_${row.id}",
                        kind = ActivityKind.TopUp,
                        kindId = row.id,
                        title = "Float top up",
                        subtitle = "M-Pesa",
                        amountKes = row.amountKes,
                        incoming = true,
                        statusLabel = "Paid",
                        status = "Paid",
                        createdAt = row.createdAt,
                        reference = row.reference,
                    ),
                )
            }
            requestsRaw?.let {
                runCatching {
                    json.decodeFromString(ListSerializer(RequestRow.serializer()), it)
                }.getOrDefault(emptyList())
            }?.forEach { row ->
                add(
                    ActivityItem(
                        id = "request_${row.id}",
                        kind = ActivityKind.Request,
                        kindId = row.id,
                        title = row.purpose,
                        subtitle = row.kind,
                        amountKes = row.amountKes,
                        incoming = true,
                        statusLabel = row.status,
                        status = row.status,
                        createdAt = row.createdAt,
                        project = row.project,
                        site = row.site,
                        voucherCode = row.voucherCode,
                        category = row.category,
                        note = row.note,
                        person = row.requesterName,
                    ),
                )
            }
        }.sortedByDescending { it.createdAt }
    }

    override val companyName: Flow<String> = companyLocal.profileJson.map { raw ->
        raw?.let {
            runCatching {
                json.decodeFromString(ProfileRow.serializer(), it)
            }.getOrNull()?.name.orEmpty()
        }.orEmpty()
    }

    override fun find(kind: ActivityKind, id: String): Flow<ActivityItem?> =
        items.map { list -> list.firstOrNull { it.kind == kind && it.kindId == id } }

    @Serializable
    private data class SpendItemRow(val description: String = "")

    @Serializable
    private data class SpendRow(
        val id: String = "",
        val amountKes: Long = 0,
        val items: List<SpendItemRow> = emptyList(),
        val category: String = "",
        val project: String = "",
        val site: String = "",
        val paidTo: String = "",
        val method: String = "",
        val mpesaCode: String = "",
        val hasReceipt: Boolean = false,
        val status: String = "Paid",
        val recordedBy: String = "",
        val createdAt: Long = 0,
    )

    @Serializable
    private data class RefundRow(
        val id: String = "",
        val amountKes: Long = 0,
        val purpose: String = "",
        val category: String = "",
        val note: String = "",
        val status: String = "Submitted",
        val claimedBy: String = "",
        val createdAt: Long = 0,
    )

    @Serializable
    private data class TopUpRow(
        val id: String = "",
        val amountKes: Long = 0,
        val reference: String = "",
        val createdAt: Long = 0,
    )

    @Serializable
    private data class RequestRow(
        val id: String = "",
        val amountKes: Long = 0,
        val purpose: String = "",
        val kind: String = "",
        val category: String = "",
        val project: String = "",
        val site: String = "",
        val note: String = "",
        val requesterName: String = "",
        val status: String = "",
        val voucherCode: String = "",
        val createdAt: Long = 0,
    )

    @Serializable
    private data class ProfileRow(val name: String = "")
}
