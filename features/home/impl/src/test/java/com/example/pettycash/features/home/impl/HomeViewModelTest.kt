package com.example.pettycash.features.home.impl

import com.example.pettycash.features.home.impl.data.HomeDashboard
import com.example.pettycash.features.home.impl.data.HomeRepository
import com.example.pettycash.features.home.impl.data.HomeRole
import com.example.pettycash.features.home.impl.data.HomeSetupStep
import com.example.pettycash.features.home.impl.data.HomeTab
import com.example.pettycash.features.home.impl.ui.HomeAction
import com.example.pettycash.features.home.impl.ui.HomeEvent
import com.example.pettycash.features.home.impl.ui.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `loads dashboard for stored role`() = runTest(dispatcher) {
        val repo = FakeHomeRepository(HomeRole.Custodian)
        val viewModel = HomeViewModel(repo)

        val state = viewModel.state.first { it.dashboard != null }

        assertEquals(HomeRole.Custodian, state.dashboard?.role)
        assertEquals("Wanjiru", state.dashboard?.userName)
    }

    @Test
    fun `greeting opens role switcher only when multiple roles`() = runTest(dispatcher) {
        val viewModel = HomeViewModel(FakeHomeRepository(HomeRole.Admin))
        viewModel.state.first { it.dashboard != null }

        viewModel.onAction(HomeAction.OnGreetingClick)
        advanceUntilIdle()
        assertTrue(viewModel.state.first().roleSwitcherOpen)

        viewModel.onAction(HomeAction.OnDismissRoleSwitcher)
        advanceUntilIdle()
        assertFalse(viewModel.state.first().roleSwitcherOpen)
    }

    @Test
    fun `member has no switchable roles so greeting does nothing`() = runTest(dispatcher) {
        val viewModel = HomeViewModel(FakeHomeRepository(HomeRole.Member))
        viewModel.state.first { it.dashboard != null }

        viewModel.onAction(HomeAction.OnGreetingClick)
        advanceUntilIdle()

        assertFalse(viewModel.state.first().roleSwitcherOpen)
    }

    @Test
    fun `selecting a role persists and reloads dashboard`() = runTest(dispatcher) {
        val repo = FakeHomeRepository(HomeRole.Admin)
        val viewModel = HomeViewModel(repo)
        viewModel.state.first { it.dashboard != null }

        viewModel.onAction(HomeAction.OnRoleSelect(1))
        advanceUntilIdle()

        assertEquals(HomeRole.Custodian, repo.selectedRole)
        assertEquals(HomeRole.Custodian, viewModel.state.first().dashboard?.role)
        assertFalse(viewModel.state.first().roleSwitcherOpen)
    }

    @Test
    fun `tab select emits navigation event`() = runTest(dispatcher) {
        val viewModel = HomeViewModel(FakeHomeRepository(HomeRole.Admin))
        viewModel.state.first { it.dashboard != null }

        viewModel.onAction(HomeAction.OnTabSelect(HomeTab.Approvals))

        assertEquals(HomeEvent.NavigateToTab(HomeTab.Approvals), viewModel.event.first())
    }

    @Test
    fun `fab emits role specific destination`() = runTest(dispatcher) {
        val memberVm = HomeViewModel(FakeHomeRepository(HomeRole.Member))
        memberVm.state.first { it.dashboard != null }
        memberVm.onAction(HomeAction.OnFabClick)
        assertEquals(HomeEvent.NavigateToNewRequest, memberVm.event.first())

        val custodianVm = HomeViewModel(FakeHomeRepository(HomeRole.Custodian))
        custodianVm.state.first { it.dashboard != null }
        custodianVm.onAction(HomeAction.OnFabClick)
        assertEquals(HomeEvent.NavigateToRecordSpend, custodianVm.event.first())
    }

    @Test
    fun `tapping pending setup step dismisses first visit and opens onboarding`() = runTest(dispatcher) {
        val repo = FakeHomeRepository(HomeRole.Admin, firstVisit = true)
        val viewModel = HomeViewModel(repo)
        viewModel.state.first { it.dashboard != null }

        viewModel.onAction(HomeAction.OnSetupStepClick(3))
        advanceUntilIdle()

        assertTrue(repo.dismissedFirstVisit)
        assertEquals(HomeEvent.NavigateToCompanyOnboarding, viewModel.event.first())
    }

    @Test
    fun `tapping completed setup step does nothing`() = runTest(dispatcher) {
        val repo = FakeHomeRepository(HomeRole.Admin, firstVisit = true)
        val viewModel = HomeViewModel(repo)
        viewModel.state.first { it.dashboard != null }

        viewModel.onAction(HomeAction.OnSetupStepClick(0))
        advanceUntilIdle()

        assertFalse(repo.dismissedFirstVisit)
    }

    private class FakeHomeRepository(
        initialRole: HomeRole,
        private val firstVisit: Boolean = false,
    ) : HomeRepository {
        private val roleFlow = MutableStateFlow(initialRole)
        var selectedRole: HomeRole? = null
        var dismissedFirstVisit = false

        override val role = roleFlow

        override suspend fun dashboard(role: HomeRole): HomeDashboard = HomeDashboard(
            userName = when (role) {
                HomeRole.Admin -> "Grace"
                HomeRole.Custodian -> "Wanjiru"
                HomeRole.Member -> "Brian"
            },
            companyName = "Savanna Solar Ltd",
            role = role,
            switchableRoles = when (role) {
                HomeRole.Member -> listOf(HomeRole.Member)
                else -> listOf(HomeRole.Admin, HomeRole.Custodian)
            },
            setupTitle = if (firstVisit && role == HomeRole.Admin) "Finish setting up" else null,
            setupDone = if (firstVisit && role == HomeRole.Admin) 3 else 0,
            setupTotal = if (firstVisit && role == HomeRole.Admin) 5 else 0,
            setupSteps = if (firstVisit && role == HomeRole.Admin) {
                listOf(
                    HomeSetupStep("Company details", done = true),
                    HomeSetupStep("Connect M-Pesa", done = true),
                    HomeSetupStep("First float", done = true),
                    HomeSetupStep("Approval rule", done = false, actionable = true),
                    HomeSetupStep("Invite your team", done = false),
                )
            } else {
                emptyList()
            },
            tabs = listOf(HomeTab.Home, HomeTab.Approvals),
        )

        override suspend fun selectRole(role: HomeRole) {
            selectedRole = role
            roleFlow.value = role
        }

        override suspend fun dismissFirstVisit() {
            dismissedFirstVisit = true
        }
    }
}
