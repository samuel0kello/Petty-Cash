package com.example.pettycash.features.onboarding.impl.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pettycash.features.onboarding.impl.R
import com.example.pettycash.mai.components.MaiButton
import com.example.pettycash.mai.components.MaiButtonSize
import com.example.pettycash.mai.components.MaiButtonType
import com.example.pettycash.mai.theme.MaiTheme
import kotlinx.coroutines.launch

private data class IntroPage(
    val icon: ImageVector,
    val titleRes: Int,
    val bodyRes: Int,
)

private val introPages = listOf(
    IntroPage(Icons.Filled.ReceiptLong, R.string.onboarding_intro_1_title, R.string.onboarding_intro_1_body),
    IntroPage(Icons.Filled.Payments, R.string.onboarding_intro_2_title, R.string.onboarding_intro_2_body),
    IntroPage(Icons.Filled.Savings, R.string.onboarding_intro_3_title, R.string.onboarding_intro_3_body),
)

@Composable
internal fun IntroScreen(
    onFinish: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { introPages.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == introPages.lastIndex

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MaiTheme.spacing.xxl)
                .padding(top = MaiTheme.spacing.lg, bottom = MaiTheme.spacing.xxl),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (!isLastPage) {
                    MaiButton(
                        text = stringResource(R.string.onboarding_intro_skip),
                        onClick = onSkip,
                        type = MaiButtonType.Text,
                        size = MaiButtonSize.Medium,
                    )
                }
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) { page ->
                val introPage = introPages[page]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = MaiTheme.spacing.xxl),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        introPage.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(96.dp),
                    )
                    Spacer(Modifier.height(MaiTheme.spacing.xxxl))
                    Text(
                        stringResource(introPage.titleRes),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(MaiTheme.spacing.md))
                    Text(
                        stringResource(introPage.bodyRes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            IntroPageDots(
                pageCount = introPages.size,
                currentPage = pagerState.currentPage,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = MaiTheme.spacing.xl),
            )
            MaiButton(
                text = stringResource(
                    if (isLastPage) R.string.onboarding_intro_get_started else R.string.onboarding_intro_next
                ),
                onClick = {
                    if (isLastPage) {
                        onFinish()
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun IntroPageDots(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    val pageDescription = stringResource(R.string.onboarding_intro_page_of, currentPage + 1, pageCount)
    Row(
        modifier = modifier
            .semantics { contentDescription = pageDescription }
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(pageCount) { index ->
            val selected = index == currentPage
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(width = if (selected) 24.dp else 8.dp, height = 8.dp)
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant,
                        CircleShape,
                    ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun IntroScreenPreview() {
    MaiTheme { IntroScreen(onFinish = {}, onSkip = {}) }
}

@Preview(showBackground = true)
@Composable
private fun IntroScreenDarkPreview() {
    MaiTheme(darkTheme = true) { IntroScreen(onFinish = {}, onSkip = {}) }
}
