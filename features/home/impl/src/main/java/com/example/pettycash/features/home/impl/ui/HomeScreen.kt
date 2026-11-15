package com.example.pettycash.features.home.impl.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pettycash.features.home.impl.R
import com.example.pettycash.features.home.impl.data.HomeDashboard
import com.example.pettycash.features.home.impl.data.HomeInfoCard
import com.example.pettycash.features.home.impl.data.HomeRole
import com.example.pettycash.features.home.impl.data.HomeSetupStep
import com.example.pettycash.features.home.impl.data.HomeTab
import com.example.pettycash.mai.components.MaiBalanceCard
import com.example.pettycash.mai.components.MaiBanner
import com.example.pettycash.mai.components.MaiCoachMark
import com.example.pettycash.mai.components.MaiBannerTone
import com.example.pettycash.mai.components.MaiExtendedFab
import com.example.pettycash.mai.components.MaiNavigationBar
import com.example.pettycash.mai.components.MaiNavItem
import com.example.pettycash.mai.components.MaiRoleOption
import com.example.pettycash.mai.components.MaiRoleSwitcher
import com.example.pettycash.mai.components.MaiRowDirection
import com.example.pettycash.mai.components.MaiSetupChecklist
import com.example.pettycash.mai.components.MaiSetupStep
import com.example.pettycash.mai.components.MaiTopAppBar
import com.example.pettycash.mai.components.MaiTransactionRow
import com.example.pettycash.mai.theme.MaiTheme
import com.example.pettycash.presentation.CollectOneTimeEvent
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun HomeRoute(
    onHomeEvent: (HomeEvent) -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = HomeState())

    CollectOneTimeEvent(viewModel.event) { event -> onHomeEvent(event) }

    HomeScreen(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun HomeScreen(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dashboard = state.dashboard
    Box(modifier = modifier.fillMaxSize()) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
            if (dashboard == null) return@Surface
            Column(Modifier.fillMaxSize()) {
                MaiTopAppBar(
                    title = stringResource(R.string.home_greeting, dashboard.userName),
                    onNavigationClick = {},
                    onTitleClick = { onAction(HomeAction.OnGreetingClick) },
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(top = MaiTheme.spacing.sm, bottom = MaiTheme.spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm),
                ) {
                    HomeSections(dashboard = dashboard, onAction = onAction)
                    Spacer(Modifier.height(1.dp))
                }
                HomeBottomBar(dashboard = dashboard, onAction = onAction)
            }
        }
        if (state.roleSwitcherOpen && dashboard != null) {
            MaiRoleSwitcher(
                roles = dashboard.switchableRoles.map { it.toRoleOption(dashboard) },
                onSelect = { onAction(HomeAction.OnRoleSelect(it)) },
                onDismiss = { onAction(HomeAction.OnDismissRoleSwitcher) },
            )
        }
        if (state.showCoachMark && dashboard?.role == HomeRole.Member) {
            MaiCoachMark(
                title = stringResource(R.string.home_coach_mark_title),
                message = stringResource(R.string.home_coach_mark_message),
                actionLabel = stringResource(R.string.home_coach_mark_action),
                onAction = { onAction(HomeAction.OnCoachMarkDismiss) },
                bottomPadding = 144.dp,
            )
        }
    }
}

@Composable
private fun HomeSections(
    dashboard: HomeDashboard,
    onAction: (HomeAction) -> Unit,
) {
    dashboard.banner?.let { banner ->
        SectionMargin {
            MaiBanner(
                message = banner,
                tone = MaiBannerTone.Warning,
                icon = Icons.Filled.Warning,
            )
        }
    }
    if (dashboard.setupTitle != null) {
        SectionMargin {
            MaiSetupChecklist(
                title = dashboard.setupTitle,
                doneCount = dashboard.setupDone,
                totalCount = dashboard.setupTotal,
                steps = dashboard.setupSteps.map { it.toSetupStep() },
                onStepClick = { onAction(HomeAction.OnSetupStepClick(it)) },
            )
        }
    }
    dashboard.float?.let { float ->
        SectionMargin {
            MaiBalanceCard(
                floatName = float.name,
                balanceKes = float.balanceKes,
                issuedKes = float.issuedKes,
                spentKes = float.spentKes,
                state = float.state,
                onClick = { onAction(HomeAction.OnBalanceClick) },
                onTopUp = if (dashboard.showTopUp) ({ onAction(HomeAction.OnTopUpClick) }) else null,
            )
        }
    }
    dashboard.approvalCard?.let { card ->
        SectionMargin {
            HomeInfoCardView(card = card, filled = true, onClick = { onAction(HomeAction.OnApprovalCardClick) })
        }
    }
    dashboard.unaccountedCard?.let { card ->
        SectionMargin {
            HomeInfoCardView(card = card, filled = false, onClick = { onAction(HomeAction.OnUnaccountedCardClick) })
        }
    }
    dashboard.receiptsCard?.let { card ->
        SectionMargin {
            HomeInfoCardView(card = card, filled = true, onClick = { onAction(HomeAction.OnReceiptsCardClick) })
        }
    }
    dashboard.sectionTitle?.let { title ->
        SectionHeading(text = title)
    }
    dashboard.rows.forEach { row ->
        MaiTransactionRow(
            title = row.title,
            meta = row.meta,
            amountKes = row.amountKes,
            direction = if (row.incoming) MaiRowDirection.In else MaiRowDirection.Out,
            status = row.status,
            showAmountSign = row.showSign,
            onClick = { onAction(HomeAction.OnRowClick) },
        )
    }
}

@Composable
private fun SectionMargin(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxWidth().padding(horizontal = MaiTheme.spacing.xxl)) {
        content()
    }
}

@Composable
private fun SectionHeading(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaiTheme.spacing.xxl)
            .padding(top = MaiTheme.spacing.sm),
    )
}

@Composable
private fun HomeInfoCardView(
    card: HomeInfoCard,
    filled: Boolean,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaiTheme.shapes.md,
        color = if (filled) scheme.surfaceContainerHighest else scheme.surface,
        border = if (filled) null else BorderStroke(1.dp, scheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(MaiTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm),
        ) {
            Text(
                card.title,
                style = MaterialTheme.typography.titleMedium,
                color = scheme.onSurface,
            )
            Text(
                card.body,
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun HomeBottomBar(
    dashboard: HomeDashboard,
    onAction: (HomeAction) -> Unit,
) {
    Column {
        dashboard.fabLabel?.let { label ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = MaiTheme.spacing.lg, bottom = MaiTheme.spacing.lg),
                contentAlignment = Alignment.CenterEnd,
            ) {
                MaiExtendedFab(
                    label = label,
                    icon = Icons.Filled.Add,
                    onClick = { onAction(HomeAction.OnFabClick) },
                )
            }
        }
        MaiNavigationBar(
            items = dashboard.tabs.map { it.toNavItem() },
            selectedIndex = 0,
            onSelect = { onAction(HomeAction.OnTabSelect(dashboard.tabs[it])) },
        )
    }
}

@Composable
private fun HomeRole.toRoleOption(dashboard: HomeDashboard): MaiRoleOption {
    val (label, subtitle, icon) = when (this) {
        HomeRole.Admin -> Triple(
            stringResource(R.string.home_role_admin),
            dashboard.companyName,
            Icons.Filled.AdminPanelSettings,
        )
        HomeRole.Custodian -> Triple(
            stringResource(R.string.home_role_custodian),
            dashboard.float?.name ?: dashboard.companyName,
            Icons.Filled.AccountBalanceWallet,
        )
        HomeRole.Member -> Triple(
            stringResource(R.string.home_role_member),
            dashboard.companyName,
            Icons.Filled.Person,
        )
    }
    return MaiRoleOption(
        label = label,
        subtitle = subtitle,
        icon = icon,
        selected = this == dashboard.role,
    )
}

@Composable
private fun HomeTab.toNavItem(): MaiNavItem = when (this) {
    HomeTab.Home -> MaiNavItem(
        label = stringResource(R.string.home_tab_home),
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home,
    )
    HomeTab.Approvals -> MaiNavItem(stringResource(R.string.home_tab_approvals), Icons.Filled.TaskAlt)
    HomeTab.Float -> MaiNavItem(stringResource(R.string.home_tab_float), Icons.Filled.AccountBalanceWallet)
    HomeTab.People -> MaiNavItem(stringResource(R.string.home_tab_people), Icons.Filled.Group)
    HomeTab.More -> MaiNavItem(stringResource(R.string.home_tab_more), Icons.Filled.MoreHoriz)
    HomeTab.Requests -> MaiNavItem(stringResource(R.string.home_tab_requests), Icons.Filled.RequestQuote)
    HomeTab.History -> MaiNavItem(stringResource(R.string.home_tab_history), Icons.Filled.History)
    HomeTab.Profile -> MaiNavItem(stringResource(R.string.home_tab_profile), Icons.Filled.Person)
}

private fun HomeSetupStep.toSetupStep() = MaiSetupStep(label = label, done = done, actionable = actionable)
