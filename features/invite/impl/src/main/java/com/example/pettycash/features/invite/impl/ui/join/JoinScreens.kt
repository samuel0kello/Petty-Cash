package com.example.pettycash.features.invite.impl.ui.join

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pettycash.features.invite.impl.R
import com.example.pettycash.mai.components.MaiButton
import com.example.pettycash.mai.components.MaiButtonType
import com.example.pettycash.mai.components.MaiCheckboxRow
import com.example.pettycash.mai.components.MaiPageDots
import com.example.pettycash.mai.components.MaiTextField
import com.example.pettycash.mai.components.MaiTopAppBar
import com.example.pettycash.mai.theme.MaiTheme
import com.example.pettycash.presentation.CollectOneTimeEvent
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun JoinDetailsRoute(
    onJoinDetailsEvent: (JoinDetailsEvent) -> Unit,
    viewModel: JoinDetailsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = JoinDetailsState())
    CollectOneTimeEvent(viewModel.event) { event -> onJoinDetailsEvent(event) }
    JoinDetailsScreen(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun JoinDetailsScreen(
    state: JoinDetailsState,
    onAction: (JoinDetailsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(Modifier.fillMaxSize()) {
            MaiTopAppBar(
                title = stringResource(R.string.join_details_title),
                onNavigationClick = { onAction(JoinDetailsAction.OnBack) },
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = MaiTheme.spacing.xxl)
                    .padding(top = MaiTheme.spacing.lg, bottom = MaiTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
            ) {
                MaiTextField(
                    value = state.name,
                    onValueChange = { onAction(JoinDetailsAction.OnNameChange(it)) },
                    label = stringResource(R.string.join_field_full_name),
                )
                MaiTextField(
                    value = state.phone,
                    onValueChange = { onAction(JoinDetailsAction.OnPhoneChange(it)) },
                    label = stringResource(R.string.join_field_mpesa),
                    keyboardType = KeyboardType.Phone,
                )
            }
            MaiButton(
                text = stringResource(R.string.join_continue),
                onClick = { onAction(JoinDetailsAction.OnContinue) },
                enabled = state.name.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
            )
        }
    }
}

@Composable
internal fun JoinDataRoute(
    onJoinDataEvent: (JoinDataEvent) -> Unit,
    viewModel: JoinDataViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = JoinDataState())
    CollectOneTimeEvent(viewModel.event) { event -> onJoinDataEvent(event) }
    JoinDataScreen(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun JoinDataScreen(
    state: JoinDataState,
    onAction: (JoinDataAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(Modifier.fillMaxSize()) {
            MaiTopAppBar(
                title = stringResource(R.string.join_data_title),
                onNavigationClick = { onAction(JoinDataAction.OnBack) },
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = MaiTheme.spacing.xxl)
                    .padding(top = MaiTheme.spacing.lg, bottom = MaiTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm),
            ) {
                JoinDataRow(
                    icon = Icons.Filled.Info,
                    title = stringResource(R.string.join_data_collected_title),
                    body = stringResource(R.string.join_data_collected_body),
                )
                JoinDataRow(
                    icon = Icons.Filled.Policy,
                    title = stringResource(R.string.join_data_why_title),
                    body = stringResource(R.string.join_data_why_body, state.companyName),
                )
                JoinDataRow(
                    icon = Icons.Filled.Group,
                    title = stringResource(R.string.join_data_who_title),
                    body = stringResource(R.string.join_data_who_body, state.companyName),
                )
                MaiCheckboxRow(
                    label = stringResource(R.string.join_data_consent, state.companyName),
                    checked = state.consent,
                    onCheckedChange = { onAction(JoinDataAction.OnConsentChange(it)) },
                    modifier = Modifier.padding(top = MaiTheme.spacing.md),
                )
            }
            MaiButton(
                text = stringResource(R.string.join_continue),
                onClick = { onAction(JoinDataAction.OnContinue) },
                enabled = state.consent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
            )
        }
    }
}

@Composable
private fun JoinDataRow(icon: ImageVector, title: String, body: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaiTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = MaiTheme.spacing.xxs)
                .size(24.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.xxs)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private data class HowItWorksPage(
    val icon: ImageVector,
    val titleRes: Int,
    val bodyRes: Int,
)

@Composable
internal fun HowItWorksRoute(
    onHowItWorksEvent: (HowItWorksEvent) -> Unit,
    viewModel: HowItWorksViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = HowItWorksState())
    CollectOneTimeEvent(viewModel.event) { event -> onHowItWorksEvent(event) }
    HowItWorksScreen(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun HowItWorksScreen(
    state: HowItWorksState,
    onAction: (HowItWorksAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pages = listOf(
        HowItWorksPage(Icons.Filled.Payments, R.string.how_it_works_title_1, R.string.how_it_works_body_1),
        HowItWorksPage(Icons.Filled.ReceiptLong, R.string.how_it_works_title_2, R.string.how_it_works_body_2),
        HowItWorksPage(Icons.Filled.Savings, R.string.how_it_works_title_3, R.string.how_it_works_body_3),
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val pageDescription = stringResource(
        R.string.how_it_works_page_of,
        pagerState.currentPage + 1,
        pages.size,
    )

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MaiTheme.spacing.xxl)
                .padding(top = MaiTheme.spacing.lg, bottom = MaiTheme.spacing.xxl),
            verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
        ) {
            if (pagerState.currentPage < pages.lastIndex) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(
                        onClick = { onAction(HowItWorksAction.OnFinish) },
                        modifier = Modifier.height(48.dp),
                    ) {
                        Text(
                            stringResource(R.string.how_it_works_skip),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.height(48.dp))
            }
            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                val item = pages[page]
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(28.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            item.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(96.dp),
                        )
                    }
                    Text(
                        stringResource(item.titleRes),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        stringResource(item.bodyRes),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            MaiPageDots(
                pageCount = pages.size,
                currentPage = pagerState.currentPage,
                contentDescription = pageDescription,
                modifier = Modifier.fillMaxWidth(),
            )
            val isLast = pagerState.currentPage == pages.lastIndex
            MaiButton(
                text = stringResource(
                    if (isLast) R.string.how_it_works_pin else R.string.how_it_works_next,
                ),
                onClick = {
                    if (isLast) {
                        onAction(HowItWorksAction.OnFinish)
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
                isLoading = state.finishing,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
