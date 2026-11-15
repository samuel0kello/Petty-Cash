package com.example.pettycash.features.invite.impl

import com.example.pettycash.features.invite.impl.data.InviteDraft
import com.example.pettycash.features.invite.impl.data.InviteRepository
import com.example.pettycash.features.invite.impl.data.PendingInvite
import com.example.pettycash.features.invite.impl.data.TeamMember
import com.example.pettycash.features.invite.impl.data.TeamSnapshot
import com.example.pettycash.features.invite.impl.ui.invite.InviteFormAction
import com.example.pettycash.features.invite.impl.ui.invite.InviteFormEvent
import com.example.pettycash.features.invite.impl.ui.invite.InviteFormViewModel
import com.example.pettycash.features.invite.impl.ui.team.TeamAction
import com.example.pettycash.features.invite.impl.ui.team.TeamEvent
import com.example.pettycash.features.invite.impl.ui.team.TeamViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InviteViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun teamLoadsMembersAndInvites() = runTest(dispatcher) {
        val repo = FakeInviteRepository()
        val viewModel = TeamViewModel(repo)
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        val state = viewModel.state.first { it.loaded }
        assertEquals("Acme Traders", state.companyName)
        assertEquals(3, state.members.size)
        assertEquals(1, state.invites.size)
        assertTrue(state.loaded)
    }

    @Test
    fun inviteClickNavigatesToPreview() = runTest(dispatcher) {
        val viewModel = TeamViewModel(FakeInviteRepository())
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        viewModel.onAction(TeamAction.OnInviteClick("inv-1"))

        assertEquals(TeamEvent.NavigateToInvitePreview("inv-1"), viewModel.event.first())
    }

    @Test
    fun sendRequiresNameAndPhone() = runTest(dispatcher) {
        val repo = FakeInviteRepository()
        val viewModel = InviteFormViewModel(repo)
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        viewModel.onAction(InviteFormAction.OnSend)
        advanceUntilIdle()
        assertTrue(repo.sentInvites.isEmpty())

        viewModel.onAction(InviteFormAction.OnNameChange("Kevin"))
        viewModel.onAction(InviteFormAction.OnPhoneChange("722111222"))
        viewModel.onAction(InviteFormAction.OnSend)

        assertEquals(InviteFormEvent.NavigateToSent, viewModel.event.first())
        assertEquals(1, repo.sentInvites.size)
        assertEquals("Kevin", repo.sentInvites.first().name)
    }

    private class FakeInviteRepository : InviteRepository {
        val sentInvites = mutableListOf<PendingInvite>()
        var joined = false
        private val teamFlow = MutableStateFlow(
            TeamSnapshot(
                companyName = "Acme Traders",
                members = listOf(
                    TeamMember("m1", "Amara Okello", "Admin", isSelf = true),
                    TeamMember("m2", "Grace Wanjiku", "Approver"),
                    TeamMember("m3", "Brian Otieno", "Member"),
                ),
                invites = listOf(
                    PendingInvite("inv-1", "Kevin Mwangi", "+254 722 111 222", "Member"),
                ),
            ),
        )
        private val draftFlow = MutableStateFlow(InviteDraft(companyName = "Acme Traders"))

        override val team: Flow<TeamSnapshot> = teamFlow
        override val draft: Flow<InviteDraft> = draftFlow

        override suspend fun sendInvite(name: String, phone: String, approver: Boolean, custodian: Boolean) {
            sentInvites.add(PendingInvite("inv-x", name, phone, "Member"))
        }

        override suspend fun inviteById(id: String): PendingInvite? =
            teamFlow.value.invites.firstOrNull { it.id == id }

        override suspend fun beginJoin(inviteId: String) = Unit

        override suspend fun updateDraft(name: String, phone: String) = Unit

        override suspend fun markJoined() {
            joined = true
        }
    }
}
