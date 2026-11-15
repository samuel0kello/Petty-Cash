package com.example.pettycash.features.floats.impl.ui.floats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pettycash.features.floats.impl.R
import com.example.pettycash.features.floats.impl.data.FloatState
import com.example.pettycash.features.floats.impl.data.SpendRow
import com.example.pettycash.mai.components.MaiAmountInput
import com.example.pettycash.mai.components.MaiBalanceCard
import com.example.pettycash.mai.components.MaiButton
import com.example.pettycash.mai.components.MaiButtonType
import com.example.pettycash.mai.components.MaiFloatState
import com.example.pettycash.mai.components.MaiListItem
import com.example.pettycash.mai.components.MaiRowDirection
import com.example.pettycash.mai.components.MaiStatus
import com.example.pettycash.mai.components.MaiTopAppBar
import com.example.pettycash.mai.components.MaiTransactionRow
import com.example.pettycash.mai.components.formatAmount
import com.example.pettycash.mai.theme.MaiTheme
import com.example.pettycash.presentation.CollectOneTimeEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun FloatsRoute(
    onFloatsEvent: (FloatsEvent) -> Unit,
    viewModel: FloatsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = FloatsState())
    CollectOneTimeEvent(viewModel.event) { onFloatsEvent(it) }
    FloatsScreen(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun FloatsScreen(
    state: FloatsState,
    onAction: (FloatsAction) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MaiTopAppBar(title = stringResource(R.string.floats_title), navigationIcon = null)
        val snapshot = state.snapshot
        when {
            state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            snapshot == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.floats_empty))
            }
            else -> LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    start = MaiTheme.spacing.xxl,
                    end = MaiTheme.spacing.xxl,
                    top = MaiTheme.spacing.sm,
                    bottom = MaiTheme.spacing.lg,
                ),
                verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
            ) {
                item {
                    val s = snapshot.state
                    MaiBalanceCard(
                        floatName = s.floatName,
                        balanceKes = s.balanceKes,
                        issuedKes = s.issuedKes,
                        spentKes = s.spentKes,
                        state = floatStateOf(s),
                        onClick = { onAction(FloatsAction.OpenFloat(s)) },
                    )
                }
                item {
                    MaiListItem(
                        title = snapshot.state.custodianName,
                        subtitle = stringResource(R.string.floats_custodian),
                        avatarInitials = initials(snapshot.state.custodianName),
                    )
                }
            }
        }
    }
}

@Composable
internal fun FloatDetailRoute(
    onBack: () -> Unit,
    onFloatDetailEvent: (FloatDetailEvent) -> Unit,
    viewModel: FloatDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = FloatDetailState())
    CollectOneTimeEvent(viewModel.event) { onFloatDetailEvent(it) }
    FloatDetailScreen(state = state, onAction = viewModel::onAction, onBack = onBack)
}

@Composable
internal fun FloatDetailScreen(
    state: FloatDetailState,
    onAction: (FloatDetailAction) -> Unit,
    onBack: () -> Unit = {},
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MaiTopAppBar(
            title = stringResource(R.string.float_detail_title),
            onNavigationClick = onBack,
        )
        val snapshot = state.snapshot
        if (snapshot == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }
        val s = snapshot.state
        Column(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    start = MaiTheme.spacing.xxl,
                    end = MaiTheme.spacing.xxl,
                    top = MaiTheme.spacing.sm,
                    bottom = MaiTheme.spacing.lg,
                ),
                verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
            ) {
                item {
                    MaiBalanceCard(
                        floatName = s.floatName,
                        balanceKes = s.balanceKes,
                        issuedKes = s.issuedKes,
                        spentKes = s.spentKes,
                        state = floatStateOf(s),
                    )
                }
                item {
                    MaiListItem(
                        title = s.custodianName,
                        subtitle = stringResource(R.string.floats_custodian),
                        avatarInitials = initials(s.custodianName),
                    )
                }
                item {
                    Text(
                        "${stringResource(R.string.float_month_spending)} · ${state.monthLabel}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = MaiTheme.spacing.sm),
                    )
                }
                if (snapshot.spends.isEmpty()) {
                    item {
                        Text(
                            stringResource(R.string.float_no_spends),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    items(snapshot.spends, key = { it.id }) { spend ->
                        SpendTransactionRow(spend)
                    }
                }
            }
        }
        Column(
            modifier = Modifier.padding(
                horizontal = MaiTheme.spacing.xxl,
                vertical = MaiTheme.spacing.lg,
            ),
            verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
        ) {
            MaiButton(
                text = stringResource(R.string.float_top_up),
                onClick = { onAction(FloatDetailAction.TopUp) },
                modifier = Modifier.fillMaxWidth(),
            )
            MaiButton(
                text = stringResource(R.string.float_reconcile),
                onClick = { onAction(FloatDetailAction.StartReconciliation) },
                type = MaiButtonType.Outlined,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
internal fun TopUpRoute(
    onBack: () -> Unit,
    onTopUpEvent: (TopUpEvent) -> Unit,
    viewModel: TopUpViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = TopUpState())
    CollectOneTimeEvent(viewModel.event) { onTopUpEvent(it) }
    TopUpScreen(state = state, onAction = viewModel::onAction, onBack = onBack)
}

@Composable
internal fun TopUpScreen(
    state: TopUpState,
    onAction: (TopUpAction) -> Unit,
    onBack: () -> Unit = {},
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MaiTopAppBar(
            title = stringResource(R.string.topup_title),
            onNavigationClick = onBack,
        )
        Column(modifier = Modifier.weight(1f)) {
            MaiAmountInput(
                amountDigits = state.amountDigits,
                onKey = { onAction(TopUpAction.Digit(it)) },
                onBackspace = { onAction(TopUpAction.Backspace) },
                label = stringResource(R.string.topup_amount_label),
                supportingText = stringResource(R.string.topup_amount_hint),
                isWarning = state.error != null,
                modifier = Modifier.weight(1f),
            )
        }
        if (state.doneAmountKes != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaiTheme.spacing.xxl),
                shape = MaiTheme.shapes.lg,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
            ) {
                Column(
                    modifier = Modifier.padding(MaiTheme.spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        stringResource(R.string.topup_success_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        stringResource(R.string.topup_success_message, formatAmount(state.doneAmountKes.toString()), "the float"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(MaiTheme.spacing.md))
                    MaiButton(
                        text = stringResource(R.string.topup_done),
                        onClick = { onAction(TopUpAction.Done) },
                    )
                }
            }
        } else {
            MaiButton(
                text = stringResource(R.string.topup_next),
                onClick = { onAction(TopUpAction.Next) },
                enabled = state.canContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
            )
        }
    }
}

@Composable
private fun SpendTransactionRow(spend: SpendRow) {
    val date = SimpleDateFormat("d MMM", Locale.US).format(Date(spend.createdAt))
    MaiTransactionRow(
        title = spend.paidTo.ifBlank { spend.category.ifBlank { "Spend" } },
        meta = "$date · ${spend.category}",
        amountKes = spend.amountKes,
        direction = MaiRowDirection.Out,
        status = if (spend.hasReceipt) MaiStatus.Paid else MaiStatus.NoReceipt,
    )
}

private fun floatStateOf(s: FloatState): MaiFloatState = when {
    s.balanceKes <= 0 -> MaiFloatState.Empty
    s.balanceKes < 5_000 -> MaiFloatState.Low
    else -> MaiFloatState.Healthy
}

private fun initials(name: String): String =
    name.split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }
        .ifBlank { "?" }
