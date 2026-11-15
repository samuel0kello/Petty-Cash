package com.example.pettycash.features.company.impl

import com.example.pettycash.features.company.impl.data.CompanyProfile
import com.example.pettycash.features.company.impl.data.CompanyRepository
import com.example.pettycash.features.company.impl.ui.ApproverRule
import com.example.pettycash.features.company.impl.ui.CompanyOnboardingAction
import com.example.pettycash.features.company.impl.ui.CompanyOnboardingEvent
import com.example.pettycash.features.company.impl.ui.CompanyOnboardingViewModel
import com.example.pettycash.features.company.impl.ui.MpesaType
import com.example.pettycash.features.company.impl.ui.OnboardingStep
import com.example.pettycash.features.company.impl.ui.VoucherMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CompanyOnboardingViewModelTest {

    @Test
    fun `next is blocked until company name is set`() = runTest {
        val viewModel = CompanyOnboardingViewModel(FakeCompanyRepository())
        viewModel.onAction(CompanyOnboardingAction.OnNext)
        advanceUntilIdle()
        assertEquals(OnboardingStep.CompanyDetails, viewModel.state.first().step)

        viewModel.onAction(CompanyOnboardingAction.OnCompanyNameChange("Savanna Solar Ltd"))
        viewModel.onAction(CompanyOnboardingAction.OnNext)
        advanceUntilIdle()
        assertEquals(OnboardingStep.Mpesa, viewModel.state.first().step)
    }

    @Test
    fun `mpesa later skips to first float`() = runTest {
        val viewModel = CompanyOnboardingViewModel(FakeCompanyRepository())
        viewModel.onAction(CompanyOnboardingAction.OnCompanyNameChange("Acme"))
        viewModel.onAction(CompanyOnboardingAction.OnNext)
        advanceUntilIdle()
        viewModel.onAction(CompanyOnboardingAction.OnMpesaLater)
        advanceUntilIdle()
        assertEquals(OnboardingStep.FirstFloat, viewModel.state.first().step)
    }

    @Test
    fun `connect mpesa shows connected step then continues to float`() = runTest {
        val viewModel = CompanyOnboardingViewModel(FakeCompanyRepository())
        viewModel.onAction(CompanyOnboardingAction.OnCompanyNameChange("Acme"))
        viewModel.onAction(CompanyOnboardingAction.OnNext)
        advanceUntilIdle()
        viewModel.onAction(CompanyOnboardingAction.OnMpesaTypeSelect(MpesaType.Paybill))
        viewModel.onAction(CompanyOnboardingAction.OnMpesaNumberChange("123456"))
        viewModel.onAction(CompanyOnboardingAction.OnMpesaAccountChange("Acme Ltd"))
        viewModel.onAction(CompanyOnboardingAction.OnConnectMpesa)
        advanceUntilIdle()
        assertEquals(OnboardingStep.MpesaConnected, viewModel.state.first().step)
        assertTrue(viewModel.state.first().mpesaConnected)

        viewModel.onAction(CompanyOnboardingAction.OnNext)
        advanceUntilIdle()
        assertEquals(OnboardingStep.FirstFloat, viewModel.state.first().step)
    }

    @Test
    fun `full flow saves profile and navigates home`() = runTest {
        val repo = FakeCompanyRepository()
        val viewModel = CompanyOnboardingViewModel(repo)

        viewModel.onAction(CompanyOnboardingAction.OnCompanyNameChange("Savanna Solar Ltd"))
        viewModel.onAction(CompanyOnboardingAction.OnIndustrySelect("Energy and solar"))
        viewModel.onAction(CompanyOnboardingAction.OnTeamSizeSelect("6–20"))
        viewModel.onAction(CompanyOnboardingAction.OnNext)
        advanceUntilIdle()

        viewModel.onAction(CompanyOnboardingAction.OnMpesaLater)
        advanceUntilIdle()

        viewModel.onAction(CompanyOnboardingAction.OnFloatNameChange("Westlands office float"))
        viewModel.onAction(CompanyOnboardingAction.OnFloatAmountChange("25000"))
        viewModel.onAction(CompanyOnboardingAction.OnFloatSpendLimitChange("5000"))
        viewModel.onAction(CompanyOnboardingAction.OnNext)
        advanceUntilIdle()
        assertEquals(OnboardingStep.ApprovalRule, viewModel.state.first().step)

        viewModel.onAction(CompanyOnboardingAction.OnApproverRuleSelect(ApproverRule.SelectedApprovers))
        viewModel.onAction(CompanyOnboardingAction.OnCategoryToggle("Fuel"))
        viewModel.onAction(CompanyOnboardingAction.OnNext)
        advanceUntilIdle()
        assertEquals(OnboardingStep.ProjectsSites, viewModel.state.first().step)

        viewModel.onAction(CompanyOnboardingAction.OnProjectAdd("Garissa solar install"))
        viewModel.onAction(CompanyOnboardingAction.OnSiteAdd("Westlands HQ"))
        viewModel.onAction(CompanyOnboardingAction.OnVoucherModeSelect(VoucherMode.ContinuePaper))
        viewModel.onAction(CompanyOnboardingAction.OnLastVoucherChange("pcv-0214"))
        viewModel.onAction(CompanyOnboardingAction.OnNext)
        advanceUntilIdle()
        assertEquals(OnboardingStep.InviteTeam, viewModel.state.first().step)

        viewModel.onAction(CompanyOnboardingAction.OnInvitePhoneChange("722111222"))
        viewModel.onAction(CompanyOnboardingAction.OnInviteApproverToggle(true))
        viewModel.onAction(CompanyOnboardingAction.OnSkipInvites)
        advanceUntilIdle()

        assertEquals(CompanyOnboardingEvent.NavigateHome, viewModel.event.first())

        val saved = repo.savedProfile
        assertEquals("Savanna Solar Ltd", saved?.companyName)
        assertEquals("Energy and solar", saved?.industry)
        assertEquals("Westlands office float", saved?.floatName)
        assertEquals("25000", saved?.floatStartingAmount)
        assertEquals(listOf("Garissa solar install"), saved?.projects)
        assertEquals(listOf("Westlands HQ"), saved?.sites)
        assertEquals(false, saved?.voucherStartFresh)
        assertEquals("PCV-0214", saved?.lastVoucherNumber)
        assertEquals(ApproverRule.SelectedApprovers.name, saved?.approverRule)
        assertTrue(saved?.categories?.contains("Fuel") == true)
    }

    private class FakeCompanyRepository : CompanyRepository {
        var savedProfile: CompanyProfile? = null
        override suspend fun completeOnboarding(profile: CompanyProfile) {
            savedProfile = profile
        }
        override suspend fun isSetupComplete() = true
    }
}
