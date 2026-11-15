package com.example.pettycash.features.reconciliation.impl

import com.example.pettycash.features.reconciliation.impl.data.ReconciliationContext
import com.example.pettycash.features.reconciliation.impl.data.ReconciliationRecord
import com.example.pettycash.features.reconciliation.impl.data.ReconciliationRepository
import com.example.pettycash.features.reconciliation.impl.data.ReconciliationSnapshot
import com.example.pettycash.features.reconciliation.impl.data.UnreceiptedSpend
import com.example.pettycash.features.reconciliation.impl.ui.reconciliation.ReconStep
import com.example.pettycash.features.reconciliation.impl.ui.reconciliation.ReconciliationAction
import com.example.pettycash.features.reconciliation.impl.ui.reconciliation.ReconciliationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReconciliationViewModelTest {

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
    fun balancedCountSubmitsImmediately() = runTest(dispatcher) {
        val repo = FakeRepository()
        val viewModel = ReconciliationViewModel(repo)
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        listOf("1", "8", "4", "5", "0").forEach { viewModel.onAction(ReconciliationAction.Digit(it)) }
        viewModel.onAction(ReconciliationAction.Next)
        advanceUntilIdle()
        assertEquals(ReconStep.Result, viewModel.state.first().step)

        viewModel.onAction(ReconciliationAction.Submit)
        advanceUntilIdle()

        assertEquals(1, repo.submissions.size)
        assertEquals(0L, repo.submissions[0].varianceHint)
        assertEquals(ReconStep.Submitted, viewModel.state.first().step)
    }

    @Test
    fun shortCountGoesThroughExplainStep() = runTest(dispatcher) {
        val repo = FakeRepository()
        val viewModel = ReconciliationViewModel(repo)
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        listOf("1", "8", "4", "0", "0").forEach { viewModel.onAction(ReconciliationAction.Digit(it)) }
        viewModel.onAction(ReconciliationAction.Next)
        advanceUntilIdle()
        assertEquals(ReconStep.Result, viewModel.state.first().step)

        viewModel.onAction(ReconciliationAction.Explain)
        advanceUntilIdle()
        assertEquals(ReconStep.Explain, viewModel.state.first().step)

        viewModel.onAction(ReconciliationAction.ExplanationChanged("Coin roll missing"))
        viewModel.onAction(ReconciliationAction.Submit)
        advanceUntilIdle()

        assertEquals(1, repo.submissions.size)
        assertEquals("Coin roll missing", repo.submissions[0].explanation)
        assertEquals(ReconStep.Submitted, viewModel.state.first().step)
    }

    @Test
    fun adminSeesReviewWhenCountSubmitted() = runTest(dispatcher) {
        val repo = FakeRepository(
            initial = ReconciliationSnapshot(
                record = ReconciliationRecord(
                    id = "r1",
                    month = "2025-11",
                    countedKes = 18_000,
                    expectedKes = 18_450,
                    varianceKes = -450,
                    explanation = "",
                    status = "Submitted",
                    createdAt = 1L,
                    unreceiptedCount = 0,
                ),
                context = context(),
                isAdmin = true,
            ),
        )
        val viewModel = ReconciliationViewModel(repo)
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        assertEquals(ReconStep.AdminReview, viewModel.state.first().step)

        viewModel.onAction(ReconciliationAction.CloseMonth)
        advanceUntilIdle()
        assertEquals(1, repo.closes)
        assertEquals(ReconStep.MonthClosed, viewModel.state.first().step)
    }

    private fun context() = ReconciliationContext(
        expectedKes = 18_450,
        floatName = "Main float",
        unreceiptedSpends = listOf(UnreceiptedSpend("s1", "Transport", 500)),
    )

    private class FakeRepository(
        initial: ReconciliationSnapshot? = null,
    ) : ReconciliationRepository {
        data class Submission(val countedKes: Long, val explanation: String, val varianceHint: Long)
        val submissions = mutableListOf<Submission>()
        var closes = 0
        private val flow = MutableStateFlow(
            initial ?: ReconciliationSnapshot(record = null, context = context()),
        )

        override val snapshot: Flow<ReconciliationSnapshot> = flow

        override suspend fun submit(countedKes: Long, explanation: String, unreceiptedCount: Int) {
            submissions += Submission(countedKes, explanation, countedKes - 18_450)
            flow.value = flow.value.copy(
                record = ReconciliationRecord(
                    id = "r",
                    month = "2025-11",
                    countedKes = countedKes,
                    expectedKes = 18_450,
                    varianceKes = countedKes - 18_450,
                    explanation = explanation,
                    status = "Submitted",
                    createdAt = 1L,
                    unreceiptedCount = unreceiptedCount,
                ),
            )
        }

        override suspend fun closeMonth() {
            closes++
            flow.value = flow.value.copy(
                record = flow.value.record?.copy(status = "Closed"),
            )
        }

        private fun context() = ReconciliationContext(
            expectedKes = 18_450,
            floatName = "Main float",
            unreceiptedSpends = listOf(UnreceiptedSpend("s1", "Transport", 500)),
        )
    }
}
