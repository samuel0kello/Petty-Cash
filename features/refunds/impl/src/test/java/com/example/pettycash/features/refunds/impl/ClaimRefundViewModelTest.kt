package com.example.pettycash.features.refunds.impl

import com.example.pettycash.features.refunds.impl.data.RefundClaim
import com.example.pettycash.features.refunds.impl.data.RefundContext
import com.example.pettycash.features.refunds.impl.data.RefundsRepository
import com.example.pettycash.features.refunds.impl.ui.claim.ClaimRefundAction
import com.example.pettycash.features.refunds.impl.ui.claim.ClaimRefundEvent
import com.example.pettycash.features.refunds.impl.ui.claim.ClaimRefundState
import com.example.pettycash.features.refunds.impl.ui.claim.ClaimRefundViewModel
import com.example.pettycash.features.refunds.impl.ui.claim.ProofChoice
import com.example.pettycash.features.refunds.impl.ui.claim.RefundStep
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
class ClaimRefundViewModelTest {

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
    fun proofRequiredBeforeRefundTo() = runTest(dispatcher) {
        val viewModel = ClaimRefundViewModel(FakeRefundsRepository())
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        listOf("8", "5", "0").forEach { viewModel.onAction(ClaimRefundAction.AmountKey(it)) }
        viewModel.onAction(ClaimRefundAction.Next)
        viewModel.onAction(ClaimRefundAction.PurposeChanged("Matatu fare"))
        viewModel.onAction(ClaimRefundAction.Next)
        advanceUntilIdle()
        assertEquals(RefundStep.Proof, viewModel.state.first().step)

        viewModel.onAction(ClaimRefundAction.Next)
        advanceUntilIdle()
        assertEquals(RefundStep.Proof, viewModel.state.first().step)

        viewModel.onAction(ClaimRefundAction.NoReceiptProof)
        viewModel.onAction(ClaimRefundAction.Next)
        advanceUntilIdle()
        assertEquals(RefundStep.RefundTo, viewModel.state.first().step)
    }

    @Test
    fun submitSavesClaimAndNavigatesHome() = runTest(dispatcher) {
        val repo = FakeRefundsRepository()
        val viewModel = ClaimRefundViewModel(repo)
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        listOf("8", "5", "0").forEach { viewModel.onAction(ClaimRefundAction.AmountKey(it)) }
        viewModel.onAction(ClaimRefundAction.Next)
        viewModel.onAction(ClaimRefundAction.PurposeChanged("Matatu fare to Garissa"))
        viewModel.onAction(ClaimRefundAction.CategoryChanged("Transport"))
        viewModel.onAction(ClaimRefundAction.FromChanged("Westlands"))
        viewModel.onAction(ClaimRefundAction.ToChanged("Garissa site office"))
        viewModel.onAction(ClaimRefundAction.Next)
        viewModel.onAction(ClaimRefundAction.TakePhoto)
        viewModel.onAction(ClaimRefundAction.Shutter)
        viewModel.onAction(ClaimRefundAction.UsePhoto)
        advanceUntilIdle()
        assertEquals(RefundStep.Proof, viewModel.state.first().step)

        viewModel.onAction(ClaimRefundAction.Next)
        advanceUntilIdle()
        assertEquals(RefundStep.RefundTo, viewModel.state.first().step)
        assertEquals("254712345678", viewModel.state.first().refundTo)

        viewModel.onAction(ClaimRefundAction.Next)
        viewModel.onAction(ClaimRefundAction.Submit)
        advanceUntilIdle()

        assertEquals(1, repo.saved.size)
        assertEquals(850L, repo.saved[0].amountKes)
        assertEquals("Photo", repo.saved[0].proof)
        assertEquals(ClaimRefundEvent.NavigateHome, viewModel.event.first())
    }

    private class FakeRefundsRepository : RefundsRepository {
        val saved = mutableListOf<RefundClaim>()

        private val flow = MutableStateFlow(
            emptyList<RefundClaim>() to RefundContext(
                categories = listOf("Transport", "Fuel"),
                memberName = "Brian Otieno",
                memberPhone = "254712345678",
            ),
        )

        override val snapshot: Flow<Pair<List<RefundClaim>, RefundContext>> = flow

        override suspend fun saveClaim(
            amountKes: Long,
            purpose: String,
            category: String,
            date: String,
            from: String,
            to: String,
            note: String,
            proof: String,
            refundTo: String,
        ) {
            saved += RefundClaim(
                id = "claim-1",
                amountKes = amountKes,
                purpose = purpose,
                category = category,
                date = date,
                from = from,
                to = to,
                note = note,
                proof = proof,
                refundTo = refundTo,
                status = "Submitted",
                claimedBy = "Brian Otieno",
                createdAt = 0L,
            )
        }
    }
}
