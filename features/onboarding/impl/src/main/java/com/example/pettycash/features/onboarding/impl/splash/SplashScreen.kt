package com.example.pettycash.features.onboarding.impl.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pettycash.features.onboarding.impl.R
import com.example.pettycash.mai.theme.MaiTheme
import com.example.pettycash.presentation.CollectOneTimeEvent
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SplashRoute(
    onNavigateToIntro: () -> Unit,
    viewModel: SplashViewModel = koinViewModel(),
) {
    viewModel.state.collectAsStateWithLifecycle(initialValue = Unit)

    CollectOneTimeEvent(viewModel.event) { event ->
        when (event) {
            SplashEvent.NavigateToIntro -> onNavigateToIntro()
        }
    }
    SplashScreen()
}

@Composable
internal fun SplashScreen(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MaiTheme.spacing.xxl, vertical = MaiTheme.spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Payments,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(48.dp),
                )
            }
            Spacer(Modifier.height(MaiTheme.spacing.xl))
            Text(
                stringResource(R.string.onboarding_brand_name),
                style = MaiTheme.customTypography.brandDisplay,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(MaiTheme.spacing.sm))
            Text(
                stringResource(R.string.onboarding_splash_tagline),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    MaiTheme { SplashScreen() }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenDarkPreview() {
    MaiTheme(darkTheme = true) { SplashScreen() }
}
