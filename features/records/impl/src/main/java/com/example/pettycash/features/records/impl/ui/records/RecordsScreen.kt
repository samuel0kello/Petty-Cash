package com.example.pettycash.features.records.impl.ui.records

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pettycash.features.records.impl.R
import com.example.pettycash.features.records.impl.data.ActivityItem
import com.example.pettycash.features.records.impl.data.ActivityKind
import com.example.pettycash.features.records.impl.data.RecordsRepository
import com.example.pettycash.mai.components.MaiCard
import com.example.pettycash.mai.components.MaiChip
import com.example.pettycash.mai.components.MaiListItem
import com.example.pettycash.mai.components.MaiRowDirection
import com.example.pettycash.mai.components.MaiStatus
import com.example.pettycash.mai.components.MaiStatusChip
import com.example.pettycash.mai.components.MaiTextField
import com.example.pettycash.mai.components.MaiTopAppBar
import com.example.pettycash.mai.components.MaiTransactionRow
import com.example.pettycash.mai.components.formatAmount
import com.example.pettycash.mai.theme.MaiTheme
import com.example.pettycash.presentation.CollectOneTimeEvent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ActivityRoute(
    onActivityEvent: (ActivityEvent) -> Unit,
    viewModel: ActivityViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = ActivityState())
    CollectOneTimeEvent(viewModel.event) { onActivityEvent(it) }
    ActivityScreen(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun ActivityScreen(
    state: ActivityState,
    onAction: (ActivityAction) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MaiTopAppBar(title = stringResource(R.string.records_activity_title), navigationIcon = null)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaiTheme.spacing.xxl),
        ) {
            MaiTextField(
                value = state.query,
                onValueChange = { onAction(ActivityAction.QueryChanged(it)) },
                placeholder = stringResource(R.string.records_search_hint),
                leadingIcon = Icons.Filled.Search,
                singleLine = true,
            )
            Spacer(Modifier.height(MaiTheme.spacing.md))
            Row(horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm)) {
                ActivityFilter.entries.forEach { filter ->
                    MaiChip(
                        label = stringResource(filter.labelRes()),
                        selected = state.filter == filter,
                        onClick = { onAction(ActivityAction.FilterSelected(filter)) },
                    )
                }
            }
        }
        Spacer(Modifier.height(MaiTheme.spacing.md))
        when {
            state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.filtered.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    stringResource(
                        if (state.items.isEmpty()) R.string.records_empty else R.string.records_no_results,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            else -> LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = MaiTheme.spacing.xxl),
            ) {
                items(state.filtered, key = { it.id }) { item ->
                    ActivityRow(item = item, onClick = { onAction(ActivityAction.OpenItem(item)) })
                    Spacer(Modifier.height(MaiTheme.spacing.sm))
                }
            }
        }
    }
}

@Composable
internal fun NotificationsRoute(
    onOpenDetail: (ActivityKind, String) -> Unit,
    repository: RecordsRepository,
) {
    val items by produceState<List<ActivityItem>?>(initialValue = null, repository) {
        repository.items.collect { value = it }
    }
    val list = items.orEmpty()
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    val (todayItems, earlierItems) = list.partition { it.createdAt >= today }
    Column(modifier = Modifier.fillMaxSize()) {
        MaiTopAppBar(title = stringResource(R.string.records_notifications_title))
        when {
            items == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            list.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    stringResource(R.string.records_empty_notifications),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            else -> LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = MaiTheme.spacing.xxl),
            ) {
                if (todayItems.isNotEmpty()) {
                    item { SectionHeading(stringResource(R.string.records_today)) }
                    items(todayItems, key = { it.id }) { NotificationRow(it, onOpenDetail) }
                }
                if (earlierItems.isNotEmpty()) {
                    item { SectionHeading(stringResource(R.string.records_earlier)) }
                    items(earlierItems, key = { it.id }) { NotificationRow(it, onOpenDetail) }
                }
            }
        }
    }
}

@Composable
private fun SectionHeading(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(vertical = MaiTheme.spacing.sm),
    )
}

@Composable
private fun NotificationRow(item: ActivityItem, onOpenDetail: (ActivityKind, String) -> Unit) {
    MaiListItem(
        title = "${item.kind.label()}: ${item.title}",
        subtitle = "${formatAmount(item.amountKes.toString())} · ${item.statusLabel}",
        leadingIcon = item.kind.icon(),
        onClick = { onOpenDetail(item.kind, item.kindId) },
    )
}

@Composable
internal fun ExpenseDetailRoute(
    kind: ActivityKind,
    id: String,
    onAudit: () -> Unit,
    onVoucher: () -> Unit,
    repository: RecordsRepository,
) {
    val item by produceState<ActivityItem?>(initialValue = null, kind, id) {
        repository.find(kind, id).collect { value = it }
    }
    ExpenseDetailScreen(item = item, onAudit = onAudit, onVoucher = onVoucher)
}

@Composable
internal fun ExpenseDetailScreen(
    item: ActivityItem?,
    onAudit: () -> Unit,
    onVoucher: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MaiTopAppBar(title = stringResource(R.string.records_detail_title))
        if (item == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.xs)) {
                Text(
                    "KES ${formatAmount(item.amountKes.toString())}",
                    style = MaiTheme.customTypography.amountLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                MaiStatusChip(statusFor(item.status, item.statusLabel))
            }
            MaiCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(MaiTheme.spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm),
                ) {
                    if (item.project.isNotBlank()) DetailRow(stringResource(R.string.records_row_project), item.project)
                    if (item.site.isNotBlank()) DetailRow(stringResource(R.string.records_row_site), item.site)
                    if (item.paidTo.isNotBlank()) DetailRow(stringResource(R.string.records_row_paid_to), item.paidTo)
                    if (item.reference.isNotBlank()) DetailRow(stringResource(R.string.records_row_reference), item.reference)
                    if (item.voucherCode.isNotBlank()) DetailRow(stringResource(R.string.records_row_voucher), item.voucherCode)
                }
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = MaiTheme.shapes.md,
                color = MaiTheme.colors.surfaceContainerLow,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = MaiTheme.spacing.lg),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ReceiptLong,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp),
                    )
                    Text(
                        stringResource(
                            if (item.hasReceipt || item.kind == ActivityKind.Request) {
                                R.string.records_receipt_attached
                            } else {
                                R.string.records_no_receipt
                            },
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm)) {
                MaiListItem(
                    title = stringResource(R.string.records_audit_trail),
                    leadingIcon = Icons.Filled.History,
                    onClick = onAudit,
                )
                MaiListItem(
                    title = stringResource(R.string.records_view_voucher),
                    leadingIcon = Icons.Filled.ReceiptLong,
                    onClick = onVoucher,
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
internal fun AuditTrailRoute(
    kind: ActivityKind,
    id: String,
    repository: RecordsRepository,
) {
    val item by produceState<ActivityItem?>(initialValue = null, kind, id) {
        repository.find(kind, id).collect { value = it }
    }
    AuditTrailScreen(item = item)
}

@Composable
internal fun AuditTrailScreen(item: ActivityItem?) {
    Column(modifier = Modifier.fillMaxSize()) {
        MaiTopAppBar(title = stringResource(R.string.records_audit_title))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
        ) {
            item?.let { AuditEntry(stringResource(R.string.records_event_created), it.person, it.createdAt) }
            AuditEntry(stringResource(R.string.records_event_submitted), item?.person ?: "", item?.createdAt ?: 0)
            if (item?.status == "Paid" || item?.status == "Approved") {
                AuditEntry(stringResource(R.string.records_event_approved), "", item.createdAt + 1)
            }
            if (item?.status == "Paid") {
                AuditEntry(stringResource(R.string.records_event_paid), "", item.createdAt + 2)
            }
        }
    }
}

@Composable
private fun AuditEntry(title: String, person: String, at: Long) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
    ) {
        Icon(
            Icons.Filled.Verified,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
        )
        Column {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            Text(
                buildString {
                    append(SimpleDateFormat("d MMM yyyy, HH:mm", Locale.US).format(Date(at)))
                    if (person.isNotBlank()) append(" · $person")
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
internal fun VoucherRoute(
    kind: ActivityKind,
    id: String,
    repository: RecordsRepository,
) {
    val item by produceState<ActivityItem?>(initialValue = null, kind, id) {
        repository.find(kind, id).collect { value = it }
    }
    val company by produceState("") {
        repository.companyName.collect { value = it }
    }
    VoucherScreen(item = item, companyName = company)
}

@Composable
internal fun VoucherScreen(item: ActivityItem?, companyName: String) {
    Column(modifier = Modifier.fillMaxSize()) {
        MaiTopAppBar(title = stringResource(R.string.records_voucher_title))
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(MaiTheme.spacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MaiCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(MaiTheme.spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm),
                ) {
                    Text(
                        companyName.ifBlank { "PettyCash" },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        item?.voucherCode?.ifBlank { "PCV-${item?.kindId?.take(4)?.uppercase() ?: "0000"}" }
                            ?: "PCV-0000",
                        style = MaiTheme.customTypography.codeMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    HorizontalDivider()
                    Text(
                        "KES ${formatAmount((item?.amountKes ?: 0).toString())}",
                        style = MaiTheme.customTypography.amountLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        item?.title.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        SimpleDateFormat("d MMMM yyyy", Locale.US).format(Date(item?.createdAt ?: 0)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivityRow(item: ActivityItem, onClick: () -> Unit) {
    MaiTransactionRow(
        title = item.title,
        meta = "${item.subtitle} · ${item.statusLabel}",
        amountKes = item.amountKes,
        direction = if (item.incoming) MaiRowDirection.In else MaiRowDirection.Out,
        status = statusFor(item.status, item.statusLabel),
        onClick = onClick,
    )
}

private fun statusFor(status: String, label: String): MaiStatus = when (status) {
    "Paid", "Approved", "Closed", "Submitted" -> when (label) {
        "No receipt" -> MaiStatus.NoReceipt
        else -> if (status == "Paid") MaiStatus.Paid else MaiStatus.Approved
    }
    "Pending" -> MaiStatus.Pending
    "Rejected" -> MaiStatus.Rejected
    else -> if (label == "No receipt") MaiStatus.NoReceipt else MaiStatus.Paid
}

private fun ActivityKind.label(): String = when (this) {
    ActivityKind.Spend -> "Spend"
    ActivityKind.Refund -> "Refund"
    ActivityKind.Request -> "Request"
    ActivityKind.TopUp -> "Top up"
}

private fun ActivityKind.icon(): ImageVector = when (this) {
    ActivityKind.Spend -> Icons.Filled.ReceiptLong
    ActivityKind.Refund -> Icons.AutoMirrored.Filled.ReceiptLong
    ActivityKind.Request -> Icons.Filled.RequestQuote
    ActivityKind.TopUp -> Icons.Filled.AccountBalanceWallet
}

private fun ActivityFilter.labelRes(): Int = when (this) {
    ActivityFilter.All -> R.string.records_filter_all
    ActivityFilter.MoneyIn -> R.string.records_filter_in
    ActivityFilter.MoneyOut -> R.string.records_filter_out
    ActivityFilter.Requests -> R.string.records_filter_requests
    ActivityFilter.Spends -> R.string.records_filter_spends
    ActivityFilter.Refunds -> R.string.records_filter_refunds
}
