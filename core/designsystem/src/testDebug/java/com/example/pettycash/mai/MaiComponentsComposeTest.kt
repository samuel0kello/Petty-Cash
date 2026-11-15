package com.example.pettycash.mai

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.pettycash.mai.components.MaiStatus
import com.example.pettycash.mai.components.MaiStatusChip
import com.example.pettycash.mai.theme.MaiTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class MaiComponentsComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun statusChipShowsLabel() {
        composeRule.setContent {
            MaiTheme {
                MaiStatusChip(MaiStatus.NeedsInfo)
            }
        }
        composeRule.onNodeWithText("Needs info").assertIsDisplayed()
    }
}
