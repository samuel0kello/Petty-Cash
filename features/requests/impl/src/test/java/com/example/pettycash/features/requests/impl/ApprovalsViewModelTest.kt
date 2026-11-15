package com.example.pettycash.features.requests.impl

import com.example.pettycash.datasource.local.AuthLocalDataSource
import com.example.pettycash.features.requests.impl.data.CashRequest
import com.example.pettycash.features.requests.impl.data.RequestContext
import com.example.pettycash.features.requests.impl.data.RequestStatus
import com.example.pettycash.features.requests.impl.data.RequestsRepository
import com.example.pettycash.features.requests.impl.ui.approval.ApprovalDetailAction
import com.example.pettycash.features.requests.impl.ui.approval.ApprovalDetailEvent
import com.example.pettycash.features.requests.impl.ui.approval.ApprovalDetailState
import com.example.pettycash.features.requests.impl.ui.approval.ApprovalDetailViewModel
import com.example.pettycash.features.requests.impl.ui.approval.ApprovalSheet
import com.example.pettycash.features.requests.impl.ui.approvals.ApprovalTab
import com.example.pettycash.features.requests.impl.ui.approvals.ApprovalsAction
import com.example.pettycash.features.requests.impl.ui.approvals.ApprovalsEvent
import com.example.pettycash.features.requests.impl.ui.approvals.ApprovalsState
import com.example.pettycash.features.requests.impl.ui.approvals.ApprovalsViewModel
import com.example.pettycash.features.requests.impl.ui.approvals.requestsForTab
import com.example.pettycash.features.requests.impl.ui.approvals.waitingTotalKes
import io.mockk.coEvery
import io.mockk.mockk
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
class ApprovalsViewModelTest {

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
    fun approvalsGroupsRequestsByTab() = runTest(dispatcher) {
        val repo = FakeApprovalRepository(
            listOf(
                request("r1", RequestStatus.PENDING, 3_200),
                request("r2", RequestStatus.INFO_REQUESTED, 1_500),
                request("r3", RequestStatus.APPROVED, 900),
                request("r4", RequestStatus.PAID, 700),
                request("r5", RequestStatus.REJECTED, 400),
            ),
        )
        val viewModel = ApprovalsViewModel(repo)
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()
        val state = viewModel.state.first { it.loaded }

        assertEquals(2, requestsForTab(state, ApprovalTab.Waiting).size)
        assertEquals(2, requestsForTab(state, ApprovalTab.Approved).size)
        assertEquals(1, requestsForTab(state, ApprovalTab.Rejected).size)
        assertEquals(4_700L, waitingTotalKes(state))
    }

    @Test
    fun requestClickNavigatesToDetail() = runTest(dispatcher) {
        val viewModel = ApprovalsViewModel(FakeApprovalRepository(listOf(request("r1", RequestStatus.PENDING, 100))))
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        viewModel.onAction(ApprovalsAction.OnRequestClick("r1"))
        assertEquals(ApprovalsEvent.NavigateToDetail("r1"), viewModel.event.first())
    }

    @Test
    fun approveMovesRequestToApprovedAndOpensPayoutReview() = runTest(dispatcher) {
        val repo = FakeApprovalRepository(listOf(request("r1", RequestStatus.PENDING, 3_200)))
        val viewModel = ApprovalDetailViewModel(repo, fakeAuth(), "r1")
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        viewModel.onAction(ApprovalDetailAction.Approve)
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertEquals(ApprovalSheet.PayoutReview, state.sheet)
        assertEquals(RequestStatus.APPROVED, repo.statusOf("r1"))
    }

    @Test
    fun rejectMarksRejectedAndShowsSnackbarWithUndo() = runTest(dispatcher) {
        val repo = FakeApprovalRepository(listOf(request("r1", RequestStatus.PENDING, 3_200)))
        val viewModel = ApprovalDetailViewModel(repo, fakeAuth(), "r1")
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        viewModel.onAction(ApprovalDetailAction.Reject)
        viewModel.onAction(ApprovalDetailAction.RejectReasonChanged("Missing receipt"))
        viewModel.onAction(ApprovalDetailAction.RejectSend)
        advanceUntilIdle()

        var state = viewModel.state.first()
        assertEquals(RequestStatus.REJECTED, repo.statusOf("r1"))
        assertTrue(state.rejectedSnackbar)

        viewModel.onAction(ApprovalDetailAction.RejectUndo)
        advanceUntilIdle()
        state = viewModel.state.first()
        assertEquals(RequestStatus.PENDING, repo.statusOf("r1"))
        assertTrue(!state.rejectedSnackbar)
    }

    @Test
    fun askInfoMarksInfoRequested() = runTest(dispatcher) {
        val repo = FakeApprovalRepository(listOf(request("r1", RequestStatus.PENDING, 3_200)))
        val viewModel = ApprovalDetailViewModel(repo, fakeAuth(), "r1")
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        viewModel.onAction(ApprovalDetailAction.AskInfo)
        viewModel.onAction(ApprovalDetailAction.AskTextChanged("Need a receipt"))
        viewModel.onAction(ApprovalDetailAction.AskInfoSend)
        advanceUntilIdle()

        assertEquals(RequestStatus.INFO_REQUESTED, repo.statusOf("r1"))
        assertEquals(ApprovalSheet.None, viewModel.state.first().sheet)
    }

    @Test
    fun wrongPinShowsErrorAndRightPinPaysOut() = runTest(dispatcher) {
        val repo = FakeApprovalRepository(listOf(request("r1", RequestStatus.APPROVED, 3_200)))
        val viewModel = ApprovalDetailViewModel(repo, fakeAuth(), "r1")
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        viewModel.onAction(ApprovalDetailAction.PayoutConfirm)
        advanceUntilIdle()
        assertEquals(ApprovalSheet.PinEntry, viewModel.state.first().sheet)

        listOf(9, 9, 9, 9).forEach { viewModel.onAction(ApprovalDetailAction.PinDigit(it)) }
        advanceUntilIdle()
        assertTrue(viewModel.state.first().pinError)

        viewModel.onAction(ApprovalDetailAction.PayoutCancel)
        viewModel.onAction(ApprovalDetailAction.PayoutConfirm)
        advanceUntilIdle()
        listOf(1, 2, 3, 4).forEach { viewModel.onAction(ApprovalDetailAction.PinDigit(it)) }
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertEquals(ApprovalSheet.Paid, state.sheet)
        assertEquals(RequestStatus.PAID, repo.statusOf("r1"))
        assertTrue(state.payoutReference.isNotBlank())
    }

    private fun fakeAuth(): AuthLocalDataSource =
        mockk {
            coEvery { pinValue() } returns "1234"
        }

    private fun request(id: String, status: String, amount: Long) = CashRequest(
        id = id,
        amountKes = amount,
        purpose = "Fuel",
        category = "Fuel",
        project = "P",
        site = "S",
        neededBy = "Fri",
        requesterName = "Brian",
        reviewerName = "Grace",
        status = status,
        voucherCode = "PCV-$id",
        createdAt = 0L,
    )

    private class FakeApprovalRepository(
        initial: List<CashRequest>,
    ) : RequestsRepository {
        private val flow = MutableStateFlow(initial to context())
        private val statuses = initial.associate { it.id to it.status }.toMutableMap()

        override val snapshot: Flow<Pair<List<CashRequest>, RequestContext>> = flow

        fun statusOf(id: String): String? = statuses[id]

        override suspend fun requestById(id: String): CashRequest? =
            flow.value.first.firstOrNull { it.id == id }

        override suspend fun createRequest(
            amountKes: Long,
            purpose: String,
            category: String,
            project: String,
            site: String,
            neededBy: String,
            note: String,
        ): CashRequest = error("unused")

        override suspend fun setStatus(id: String, status: String, paidReference: String, reviewNote: String) {
            statuses[id] = status
            flow.value = flow.value.first.map {
                if (it.id == id) {
                    it.copy(
                        status = status,
                        paidReference = paidReference.ifBlank { it.paidReference },
                        reviewNote = reviewNote.ifBlank { it.reviewNote },
                    )
                } else it
            } to flow.value.second
        }

        private fun context() = RequestContext(5_000, emptyList(), emptyList(), emptyList(), "Grace", "Brian")
    }
}
