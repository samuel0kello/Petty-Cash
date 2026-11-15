package com.example.pettycash.features.onboarding.impl.welcome

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.pettycash.features.onboarding.impl.R
import com.example.pettycash.mai.components.MaiButton
import com.example.pettycash.mai.components.MaiButtonSize
import com.example.pettycash.mai.components.MaiButtonType
import com.example.pettycash.mai.theme.MaiTheme

@Composable
internal fun WelcomeScreen(
    onSetUpCompany: () -> Unit,
    onInvited: () -> Unit,
    onExistingAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MaiTheme.spacing.xxl)
                .padding(top = MaiTheme.spacing.lg, bottom = MaiTheme.spacing.xxl),
            verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
        ) {
            Text(
                stringResource(R.string.onboarding_welcome_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            WelcomeOptionCard(
                title = stringResource(R.string.onboarding_welcome_company),
                subtitle = stringResource(R.string.onboarding_welcome_company_subtitle),
                onClick = onSetUpCompany,
            )
            WelcomeOptionCard(
                title = stringResource(R.string.onboarding_welcome_invited),
                subtitle = stringResource(R.string.onboarding_welcome_invited_subtitle),
                onClick = onInvited,
            )
            Spacer(Modifier.weight(1f))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                MaiButton(
                    text = stringResource(R.string.onboarding_welcome_existing_account),
                    onClick = onExistingAccount,
                    type = MaiButtonType.Text,
                    size = MaiButtonSize.Medium,
                )
            }
        }
    }
}

@Composable
private fun WelcomeOptionCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaiTheme.shapes.md)
            .clickable(onClick = onClick)
            .padding(MaiTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.sm),
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WelcomeScreenPreview() {
    MaiTheme { WelcomeScreen(onSetUpCompany = {}, onInvited = {}, onExistingAccount = {}) }
}

@Preview(showBackground = true)
@Composable
private fun WelcomeScreenDarkPreview() {
    MaiTheme(darkTheme = true) {
        WelcomeScreen(onSetUpCompany = {}, onInvited = {}, onExistingAccount = {})
    }
}
