package com.example.pettycash.features.settings.impl.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pettycash.features.settings.impl.R
import com.example.pettycash.features.settings.impl.data.SettingsProfile
import com.example.pettycash.features.settings.impl.data.SettingsSnapshot
import com.example.pettycash.features.settings.impl.data.TeamPerson
import com.example.pettycash.mai.components.MaiButton
import com.example.pettycash.mai.components.MaiListItem
import com.example.pettycash.mai.components.MaiPinPad
import com.example.pettycash.mai.components.MaiSegmentedButton
import com.example.pettycash.mai.components.MaiSwitchRow
import com.example.pettycash.mai.components.MaiTextField
import com.example.pettycash.mai.components.MaiTopAppBar
import com.example.pettycash.mai.theme.MaiTheme
import com.example.pettycash.presentation.CollectOneTimeEvent
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ProfileRoute(
    onSignedOut: () -> Unit,
    onAppearance: () -> Unit,
    onSecurity: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = ProfileState())
    CollectOneTimeEvent(viewModel.event) { if (it == ProfileEvent.SignedOut) onSignedOut() }
    ProfileScreen(
        state = state,
        onAction = viewModel::onAction,
        onAppearance = onAppearance,
        onSecurity = onSecurity,
    )
}

@Composable
internal fun ProfileScreen(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
    onAppearance: () -> Unit = {},
    onSecurity: () -> Unit = {},
) {
    val snapshot = state.snapshot
    Column(modifier = Modifier.fillMaxSize()) {
        MaiTopAppBar(title = stringResource(R.string.settings_profile_title), navigationIcon = null)
        if (snapshot == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = MaiTheme.spacing.xxl),
        ) {
            Spacer(Modifier.height(MaiTheme.spacing.sm))
            MaiListItem(
                title = snapshot.userName.ifBlank { "You" },
                subtitle = snapshot.userPhone,
                avatarInitials = initials(snapshot.userName),
            )
            Spacer(Modifier.height(MaiTheme.spacing.lg))
            Text(
                stringResource(R.string.settings_phone),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = MaiTheme.spacing.xs),
            )
            DetailText(snapshot.userPhone)
            Spacer(Modifier.height(MaiTheme.spacing.md))
            Text(
                stringResource(R.string.settings_refund_number),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = MaiTheme.spacing.xs),
            )
            DetailText(snapshot.memberPhone.ifBlank { "—" })
            Spacer(Modifier.height(MaiTheme.spacing.lg))
            SectionLabel(stringResource(R.string.settings_section_settings))
            MaiListItem(
                title = stringResource(R.string.settings_appearance),
                leadingIcon = Icons.Filled.Palette,
                onClick = onAppearance,
            )
            MaiListItem(
                title = stringResource(R.string.settings_security),
                leadingIcon = Icons.Filled.Lock,
                onClick = onSecurity,
            )
            Spacer(Modifier.height(MaiTheme.spacing.lg))
            MaiListItem(
                title = stringResource(R.string.settings_sign_out),
                leadingIcon = Icons.Filled.Person,
                onClick = { onAction(ProfileAction.SignOutClick) },
            )
            Spacer(Modifier.height(MaiTheme.spacing.xl))
        }
    }
    if (state.confirmSignOut) {
        AlertDialog(
            onDismissRequest = { onAction(ProfileAction.SignOutDismiss) },
            title = { Text(stringResource(R.string.settings_sign_out)) },
            text = { Text(stringResource(R.string.settings_sign_out_body)) },
            confirmButton = {
                TextButton(onClick = { onAction(ProfileAction.SignOutConfirm) }) {
                    Text(stringResource(R.string.settings_sign_out))
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(ProfileAction.SignOutDismiss) }) {
                    Text(stringResource(R.string.settings_cancel))
                }
            },
        )
    }
}

@Composable
internal fun MoreRoute(
    onCompany: () -> Unit,
    onTeam: () -> Unit,
    onLimits: () -> Unit,
    onProjects: () -> Unit,
    onVouchers: () -> Unit,
    onMpesa: () -> Unit,
    onAppearance: () -> Unit,
    onSecurity: () -> Unit,
    repository: com.example.pettycash.features.settings.impl.data.SettingsRepository,
) {
    val snapshot by produceSettingsSnapshot(repository)
    MoreScreen(
        snapshot = snapshot,
        onCompany = onCompany,
        onTeam = onTeam,
        onLimits = onLimits,
        onProjects = onProjects,
        onVouchers = onVouchers,
        onMpesa = onMpesa,
        onAppearance = onAppearance,
        onSecurity = onSecurity,
    )
}

@Composable
private fun produceSettingsSnapshot(
    repository: com.example.pettycash.features.settings.impl.data.SettingsRepository,
): androidx.compose.runtime.State<SettingsSnapshot?> {
    val state = androidx.compose.runtime.produceState<SettingsSnapshot?>(initialValue = null) {
        repository.snapshot.collect { value = it }
    }
    return state
}

@Composable
internal fun MoreScreen(
    snapshot: SettingsSnapshot?,
    onCompany: () -> Unit = {},
    onTeam: () -> Unit = {},
    onLimits: () -> Unit = {},
    onProjects: () -> Unit = {},
    onVouchers: () -> Unit = {},
    onMpesa: () -> Unit = {},
    onAppearance: () -> Unit = {},
    onSecurity: () -> Unit = {},
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MaiTopAppBar(title = stringResource(R.string.settings_more_title), navigationIcon = null)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = MaiTheme.spacing.xxl),
        ) {
            Spacer(Modifier.height(MaiTheme.spacing.sm))
            if (snapshot?.role == "Admin") {
                SectionLabel(stringResource(R.string.settings_section_company))
                MaiListItem(
                    title = stringResource(R.string.settings_company_profile),
                    subtitle = snapshot.profile?.companyName,
                    leadingIcon = Icons.Filled.Business,
                    onClick = onCompany,
                )
                MaiListItem(
                    title = stringResource(R.string.settings_team),
                    leadingIcon = Icons.Filled.Group,
                    onClick = onTeam,
                )
                MaiListItem(
                    title = stringResource(R.string.settings_limits),
                    leadingIcon = Icons.Filled.ReceiptLong,
                    onClick = onLimits,
                )
                MaiListItem(
                    title = stringResource(R.string.settings_projects_title),
                    leadingIcon = Icons.Filled.Category,
                    onClick = onProjects,
                )
                MaiListItem(
                    title = stringResource(R.string.settings_vouchers),
                    leadingIcon = Icons.Filled.Description,
                    onClick = onVouchers,
                )
                MaiListItem(
                    title = stringResource(R.string.settings_mpesa),
                    subtitle = snapshot.profile?.let {
                        "${it.mpesaType} ${it.mpesaNumber}".trim()
                    },
                    leadingIcon = Icons.Filled.AccountBalanceWallet,
                    onClick = onMpesa,
                )
                Spacer(Modifier.height(MaiTheme.spacing.lg))
            }
            SectionLabel(stringResource(R.string.settings_section_you))
            MaiListItem(
                title = snapshot?.userName ?: stringResource(R.string.settings_profile_title),
                subtitle = snapshot?.userPhone,
                avatarInitials = initials(snapshot?.userName ?: ""),
            )
            MaiListItem(
                title = stringResource(R.string.settings_appearance),
                leadingIcon = Icons.Filled.Palette,
                onClick = onAppearance,
            )
            MaiListItem(
                title = stringResource(R.string.settings_security),
                leadingIcon = Icons.Filled.Lock,
                onClick = onSecurity,
            )
            Spacer(Modifier.height(MaiTheme.spacing.xl))
        }
    }
}

@Composable
internal fun AppearanceRoute(viewModel: AppearanceViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = AppearanceState())
    AppearanceScreen(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun AppearanceScreen(state: AppearanceState, onAction: (AppearanceAction) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        MaiTopAppBar(title = stringResource(R.string.settings_appearance_title))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
        ) {
            Text(
                stringResource(R.string.settings_theme),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            MaiSegmentedButton(
                options = listOf("system", "light", "dark"),
                selected = state.themeMode,
                onSelect = { onAction(AppearanceAction.ThemeSelected(it)) },
                label = {
                    stringResource(
                        when (it) {
                            "light" -> R.string.settings_theme_light
                            "dark" -> R.string.settings_theme_dark
                            else -> R.string.settings_theme_system
                        },
                    )
                },
            )
            MaiSwitchRow(
                title = stringResource(R.string.settings_high_contrast),
                checked = state.highContrast,
                onCheckedChange = { onAction(AppearanceAction.HighContrastChanged(it)) },
            )
        }
    }
}

@Composable
internal fun SecurityRoute(viewModel: SecurityViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = SecurityState())
    SecurityScreen(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun SecurityScreen(state: SecurityState, onAction: (SecurityAction) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        MaiTopAppBar(
            title = stringResource(
                if (state.pinStep != PinStep.Hidden) R.string.settings_change_pin_title
                else R.string.settings_security_title,
            ),
            onNavigationClick = {
                if (state.pinStep != PinStep.Hidden) onAction(SecurityAction.PinBack) else Unit
            },
        )
        when {
            state.pinStep == PinStep.Hidden -> SecurityMain(state, onAction)
            state.pinStep == PinStep.Done -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(MaiTheme.spacing.xxl),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        stringResource(R.string.settings_pin_saved),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            else -> PinStepContent(state, onAction)
        }
    }
}

@Composable
private fun ColumnScope.SecurityMain(state: SecurityState, onAction: (SecurityAction) -> Unit) {
    Column(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MaiTheme.spacing.xxl),
    ) {
        Spacer(Modifier.height(MaiTheme.spacing.sm))
        MaiListItem(
            title = stringResource(R.string.settings_change_pin),
            leadingIcon = Icons.Filled.Lock,
            onClick = { onAction(SecurityAction.ChangePin) },
        )
        MaiSwitchRow(
            title = stringResource(R.string.settings_fingerprint),
            checked = state.fingerprintEnabled,
            onCheckedChange = { onAction(SecurityAction.FingerprintChanged(it)) },
            modifier = Modifier.padding(horizontal = MaiTheme.spacing.sm),
        )
        Spacer(Modifier.height(MaiTheme.spacing.lg))
        Text(
            stringResource(R.string.settings_your_phones),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = MaiTheme.spacing.sm),
        )
        MaiListItem(
            title = stringResource(R.string.settings_this_phone),
            subtitle = stringResource(R.string.settings_approved),
            leadingIcon = Icons.Filled.Fingerprint,
        )
        Spacer(Modifier.height(MaiTheme.spacing.xl))
    }
}

@Composable
private fun PinStepContent(state: SecurityState, onAction: (SecurityAction) -> Unit) {
    val title = when (state.pinStep) {
        PinStep.OldPin -> stringResource(R.string.settings_enter_old_pin)
        PinStep.NewPin -> stringResource(R.string.settings_enter_new_pin)
        else -> stringResource(R.string.settings_confirm_new_pin)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (state.triesLeft < 5 && state.pinStep == PinStep.OldPin) {
                Text(
                    stringResource(R.string.settings_pin_wrong, state.triesLeft),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Spacer(Modifier.height(MaiTheme.spacing.lg))
            MaiPinPad(
                entered = state.pinEntry.length,
                onDigit = { onAction(SecurityAction.PinDigit(it.toString())) },
                onDelete = { onAction(SecurityAction.PinBackspace) },
            )
        }
    }
}

@Composable
internal fun PersonRoute(
    name: String,
    repository: com.example.pettycash.features.settings.impl.data.SettingsRepository,
    onRemoved: () -> Unit,
) {
    val snapshot by produceSettingsSnapshot(repository)
    val person = snapshot?.people?.firstOrNull { it.name == name }
    var confirm by remember { mutableStateOf(false) }
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize()) {
        MaiTopAppBar(title = stringResource(R.string.settings_person_title))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
        ) {
            if (person != null) {
                MaiListItem(
                    title = person.name,
                    subtitle = person.role,
                    avatarInitials = initials(person.name),
                )
                if (!person.isSelf) {
                    Spacer(Modifier.weight(1f))
                    MaiButton(
                        text = stringResource(R.string.settings_remove_person),
                        onClick = { confirm = true },
                        type = com.example.pettycash.mai.components.MaiButtonType.Outlined,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
    if (confirm && person != null) {
        AlertDialog(
            onDismissRequest = { confirm = false },
            title = { Text(stringResource(R.string.settings_remove_person_title, person.name)) },
            text = {
                Text(stringResource(R.string.settings_remove_person_body, snapshot?.profile?.companyName ?: ""))
            },
            confirmButton = {
                TextButton(onClick = {
                    confirm = false
                    scope.launch { repository.removePerson(person.id); onRemoved() }
                }) { Text(stringResource(R.string.settings_remove)) }
            },
            dismissButton = {
                TextButton(onClick = { confirm = false }) { Text(stringResource(R.string.settings_cancel)) }
            },
        )
    }
}

@Composable
internal fun CompanyFormRoute(
    titleRes: Int,
    viewModel: CompanyFormViewModel = koinViewModel(),
    content: @Composable (SettingsProfile, (SettingsProfile) -> Unit) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = CompanyFormState())
    CollectOneTimeEvent(viewModel.event) { }
    Column(modifier = Modifier.fillMaxSize()) {
        MaiTopAppBar(title = stringResource(titleRes))
        when {
            state.loading || state.profile == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            else -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
                ) {
                    content(state.profile!!, { viewModel.onAction(CompanyFormAction.ProfileChanged(it)) })
                }
                Column(
                    modifier = Modifier.padding(
                        horizontal = MaiTheme.spacing.xxl,
                        vertical = MaiTheme.spacing.lg,
                    ),
                ) {
                    if (state.saved) {
                        Text(
                            stringResource(R.string.settings_saved),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Spacer(Modifier.height(MaiTheme.spacing.sm))
                    MaiButton(
                        text = stringResource(R.string.settings_save),
                        onClick = { viewModel.onAction(CompanyFormAction.Save) },
                        enabled = !state.saving,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
internal fun DetailText(value: String) {
    Text(
        value,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(vertical = MaiTheme.spacing.sm),
    )
}

internal fun initials(name: String): String =
    name.split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }
        .ifBlank { "?" }
