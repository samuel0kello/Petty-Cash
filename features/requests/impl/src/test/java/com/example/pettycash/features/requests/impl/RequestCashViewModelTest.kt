package com.example.pettycash.features.requests.impl

import com.example.pettycash.features.requests.impl.data.CashRequest
import com.example.pettycash.features.requests.impl.data.RequestContext
import com.example.pettycash.features.requests.impl.data.RequestStatus
import com.example.pettycash.features.requests.impl.data.RequestsRepository
import com.example.pettycash.features.requests.impl.ui.request.RequestCashAction
import com.example.pettycash.features.requests.impl.ui.request.RequestCashEvent
import com.example.pettycash.features.requests.impl.ui.request.RequestCashState
import com.example.pettycash.features.requests.impl.ui.request.RequestCashViewModel
import com.example.pettycash.features.requests.impl.ui.request.RequestStep
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
class RequestCashViewModelTest {

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
    fun amountEntryAdvancesToDetails() = runTest(dispatcher) {
        val viewModel = RequestCashViewModel(FakeRequestsRepository())
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        listOf("3", "2", "0", "0").forEach { viewModel.onAction(RequestCashAction.AmountKey(it)) }

        var state = viewModel.state.first()
        assertEquals("3200", state.amountDigits)

        viewModel.onAction(RequestCashAction.Next)
        state = viewModel.state.first()
        assertEquals(RequestStep.Details, state.step)
    }

    @Test
    fun detailsGatingRequiresAllFields() = runTest(dispatcher) {
        val viewModel = RequestCashViewModel(FakeRequestsRepository())
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        viewModel.onAction(RequestCashAction.AmountKey("1"))
        viewModel.onAction(RequestCashAction.Next)
        advanceUntilIdle()

        viewModel.onAction(RequestCashAction.Next)
        assertEquals(RequestStep.Details, viewModel.state.first().step)

        viewModel.onAction(RequestCashAction.PurposeChanged("Fuel"))
        viewModel.onAction(RequestCashAction.CategoryChanged("Fuel"))
        viewModel.onAction(RequestCashAction.ProjectChanged("Garissa solar install"))
        viewModel.onAction(RequestCashAction.SiteChanged("Garissa"))
        viewModel.onAction(RequestCashAction.NeededByChanged("Fri 30 Oct"))
        viewModel.onAction(RequestCashAction.Next)
        advanceUntilIdle()

        assertEquals(RequestStep.Review, viewModel.state.first().step)
    }

    @Test
    fun sendCreatesRequestAndShowsSent() = runTest(dispatcher) {
        val repo = FakeRequestsRepository()
        val viewModel = RequestCashViewModel(repo)
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        viewModel.onAction(RequestCashAction.AmountKey("3"))
        viewModel.onAction(RequestCashAction.AmountKey("2"))
        viewModel.onAction(RequestCashAction.AmountKey("0"))
        viewModel.onAction(RequestCashAction.AmountKey("0"))
        viewModel.onAction(RequestCashAction.Next)
        viewModel.onAction(RequestCashAction.PurposeChanged("Fuel for Garissa"))
        viewModel.onAction(RequestCashAction.CategoryChanged("Fuel"))
        viewModel.onAction(RequestCashAction.ProjectChanged("Garissa solar install"))
        viewModel.onAction(RequestCashAction.SiteChanged("Garissa"))
        viewModel.onAction(RequestCashAction.NeededByChanged("Fri 30 Oct"))
        viewModel.onAction(RequestCashAction.Next)
        viewModel.onAction(RequestCashAction.Send)
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertEquals(RequestStep.Sent, state.step)
        assertEquals("PCV-1043", state.sentRequest?.voucherCode)
        assertEquals(3200L, state.sentRequest?.amountKes)
        assertEquals(1, repo.created.size)

        viewModel.onAction(RequestCashAction.Done)
        assertEquals(RequestCashEvent.NavigateHome, viewModel.event.first())
    }

    @Test
    fun backWalksStepsInReverse() = runTest(dispatcher) {
        val viewModel = RequestCashViewModel(FakeRequestsRepository())
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        viewModel.onAction(RequestCashAction.AmountKey("5"))
        viewModel.onAction(RequestCashAction.Next)
        viewModel.onAction(RequestCashAction.Back)
        assertEquals(RequestStep.Amount, viewModel.state.first().step)

        viewModel.onAction(RequestCashAction.Back)
        assertEquals(RequestCashEvent.NavigateHome, viewModel.event.first())
    }

    private class FakeRequestsRepository : RequestsRepository {
        val created = mutableListOf<CashRequest>()
        private val flow = MutableStateFlow(
            emptyList<CashRequest>() to RequestContext(
                perRequestLimitKes = 5_000L,
                projects = listOf("Garissa solar install"),
                sites = listOf("Garissa"),
                categories = listOf("Fuel", "Transport"),
                reviewerName = "Grace Wanjiku",
                memberName = "Brian Otieno",
            ),
        )

        override val snapshot: Flow<Pair<List<CashRequest>, RequestContext>> = flow

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
        ): CashRequest {
            val request = CashRequest(
                id = "req-1",
                amountKes = amountKes,
                purpose = purpose,
                category = category,
                project = project,
                site = site,
                neededBy = neededBy,
                note = note,
                requesterName = "Brian Otieno",
                reviewerName = "Grace Wanjiku",
                status = RequestStatus.PENDING,
                voucherCode = "PCV-1043",
                createdAt = 0L,
            )
            created.add(request)
            flow.value = flow.value.first + request to flow.value.second
            return request
        }

        override suspend fun setStatus(id: String, status: String, paidReference: String, reviewNote: String) = Unit
    }
}
