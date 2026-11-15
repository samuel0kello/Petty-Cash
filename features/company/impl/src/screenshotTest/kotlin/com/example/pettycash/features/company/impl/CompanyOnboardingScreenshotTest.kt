package com.example.pettycash.features.company.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import com.example.pettycash.features.company.impl.ui.ApproverRule
import com.example.pettycash.features.company.impl.ui.CompanyOnboardingAction
import com.example.pettycash.features.company.impl.ui.CompanyOnboardingScreen
import com.example.pettycash.features.company.impl.ui.CompanyOnboardingState
import com.example.pettycash.features.company.impl.ui.MpesaType
import com.example.pettycash.features.company.impl.ui.OnboardingStep
import com.example.pettycash.features.company.impl.ui.VoucherMode
import com.example.pettycash.mai.theme.MaiTheme

private fun state(step: OnboardingStep) = CompanyOnboardingState(
    step = step,
    companyName = "Savanna Solar Ltd",
    industry = "Energy and solar",
    teamSize = "6–20",
    mpesaType = MpesaType.Paybill,
    mpesaNumber = "123456",
    mpesaAccountName = "Savanna Solar Ltd",
    mpesaConnected = step == OnboardingStep.MpesaConnected,
    floatName = "Westlands office float",
    floatStartingAmount = "25,000",
    floatCustodian = "",
    floatSingleSpendLimit = "5,000",
    floatReceiptDeadline = "24 hours",
    floatNoReceiptLimit = "2,000",
    approverRule = ApproverRule.Admins,
    categories = setOf("Transport", "Office supplies", "Fuel", "Meals", "Repairs", "Other"),
    projects = listOf("Garissa solar install", "Office"),
    sites = listOf("Westlands HQ", "Garissa"),
    voucherMode = VoucherMode.StartFresh,
    lastVoucherNumber = "PCV-0214",
    invitePhone = "722 111 222",
)


@Composable
private fun PreviewDevice(content: @Composable () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(24.dp))
        Box(Modifier.weight(1f)) { content() }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Company details")
@Composable
fun CompanyDetailsScreenshot() {
    MaiTheme {
        PreviewDevice {
        CompanyOnboardingScreen(state = state(OnboardingStep.CompanyDetails), onAction = {})
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Connect M-Pesa")
@Composable
fun MpesaScreenshot() {
    MaiTheme {
        PreviewDevice {
        CompanyOnboardingScreen(state = state(OnboardingStep.Mpesa), onAction = {})
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "M-Pesa connected")
@Composable
fun MpesaConnectedScreenshot() {
    MaiTheme {
        PreviewDevice {
        CompanyOnboardingScreen(state = state(OnboardingStep.MpesaConnected), onAction = {})
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "First float")
@Composable
fun FirstFloatScreenshot() {
    MaiTheme {
        PreviewDevice {
        CompanyOnboardingScreen(
                state = state(OnboardingStep.FirstFloat).copy(floatCustodian = ""),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Approval rule")
@Composable
fun ApprovalRuleScreenshot() {
    MaiTheme {
        PreviewDevice {
        CompanyOnboardingScreen(state = state(OnboardingStep.ApprovalRule), onAction = {})
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Projects sites vouchers")
@Composable
fun ProjectsSitesScreenshot() {
    MaiTheme {
        PreviewDevice {
        CompanyOnboardingScreen(state = state(OnboardingStep.ProjectsSites), onAction = {})
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Invite team")
@Composable
fun InviteTeamScreenshot() {
    MaiTheme {
        PreviewDevice {
        CompanyOnboardingScreen(state = state(OnboardingStep.InviteTeam), onAction = {})
        }
    }
}
