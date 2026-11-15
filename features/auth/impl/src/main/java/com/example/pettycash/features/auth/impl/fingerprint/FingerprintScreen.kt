package com.example.pettycash.features.auth.impl.fingerprint

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pettycash.features.auth.impl.R
import com.example.pettycash.mai.components.MaiButton
import com.example.pettycash.mai.components.MaiButtonType
import com.example.pettycash.mai.theme.MaiTheme

@Composable
internal fun FingerprintRoute(
    onBack: () -> Unit,
    onTurnOn: () -> Unit,
    onNotNow: () -> Unit,
) {
    FingerprintScreen(onTurnOn = onTurnOn, onNotNow = onNotNow, onBack = onBack)
}

@Composable
internal fun FingerprintScreen(
    onTurnOn: () -> Unit,
    onNotNow: () -> Unit,
    onBack: () -> Unit,
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.lg),
        ) {
            Icon(
                Icons.Filled.Fingerprint,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(120.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    .padding(28.dp),
            )
            Text(
                stringResource(R.string.auth_fingerprint_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            Text(
                stringResource(R.string.auth_fingerprint_body),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.weight(1f))
            MaiButton(
                text = stringResource(R.string.auth_fingerprint_turn_on),
                onClick = onTurnOn,
                modifier = Modifier.fillMaxWidth(),
            )
            MaiButton(
                text = stringResource(R.string.auth_fingerprint_not_now),
                onClick = onNotNow,
                type = MaiButtonType.Text,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FingerprintScreenPreview() {
    MaiTheme {
        FingerprintScreen(onTurnOn = {}, onNotNow = {}, onBack = {})
    }
}
