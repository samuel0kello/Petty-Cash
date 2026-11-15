package com.example.pettycash.features.spends.impl

import com.example.pettycash.features.spends.impl.data.SpendContext
import com.example.pettycash.features.spends.impl.data.SpendItem
import com.example.pettycash.features.spends.impl.data.SpendRecord
import com.example.pettycash.features.spends.impl.data.SpendsRepository
import com.example.pettycash.features.spends.impl.ui.record.PayMethod
import com.example.pettycash.features.spends.impl.ui.record.ReceiptStage
import com.example.pettycash.features.spends.impl.ui.record.RecordSpendAction
import com.example.pettycash.features.spends.impl.ui.record.RecordSpendEvent
import com.example.pettycash.features.spends.impl.ui.record.RecordSpendState
import com.example.pettycash.features.spends.impl.ui.record.RecordSpendViewModel
import com.example.pettycash.features.spends.impl.ui.record.SpendStep
import com.example.pettycash.features.spends.impl.ui.record.itemsTotal
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecordSpendViewModelTest {

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
    fun amountOverFloatIsBlocked() = runTest(dispatcher) {
        val viewModel = RecordSpendViewModel(FakeSpendsRepository())
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        listOf("9", "9", "9", "9", "9").forEach { viewModel.onAction(RecordSpendAction.AmountKey(it)) }
        viewModel.onAction(RecordSpendAction.Next)
        advanceUntilIdle()

        assertEquals(SpendStep.Amount, viewModel.state.first().step)
    }

    @Test
    fun fullFlowSavesSpendAndNavigatesHome() = runTest(dispatcher) {
        val repo = FakeSpendsRepository()
        val viewModel = RecordSpendViewModel(repo)
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        listOf("1", "5", "0", "0").forEach { viewModel.onAction(RecordSpendAction.AmountKey(it)) }
        viewModel.onAction(RecordSpendAction.Next)
        viewModel.onAction(RecordSpendAction.ItemDescription(0, "Printer paper"))
        viewModel.onAction(RecordSpendAction.ItemAmount(0, "900"))
        viewModel.onAction(RecordSpendAction.AddItem)
        viewModel.onAction(RecordSpendAction.ItemDescription(1, "Pens"))
        viewModel.onAction(RecordSpendAction.ItemAmount(1, "600"))
        viewModel.onAction(RecordSpendAction.CategoryChanged("Supplies"))
        viewModel.onAction(RecordSpendAction.ProjectChanged("Office"))
        viewModel.onAction(RecordSpendAction.SiteChanged("Westlands HQ"))
        viewModel.onAction(RecordSpendAction.PaidToChanged("Naivas Sarit"))
        viewModel.onAction(RecordSpendAction.Next)
        advanceUntilIdle()
        assertEquals(SpendStep.PaidWith, viewModel.state.first().step)

        viewModel.onAction(RecordSpendAction.MpesaCodeChanged("SJK4H7XP2Q"))
        viewModel.onAction(RecordSpendAction.Next)
        advanceUntilIdle()
        assertEquals(SpendStep.Receipt, viewModel.state.first().step)

        viewModel.onAction(RecordSpendAction.Shutter)
        viewModel.onAction(RecordSpendAction.UsePhoto)
        advanceUntilIdle()
        assertEquals(SpendStep.Review, viewModel.state.first().step)

        viewModel.onAction(RecordSpendAction.Save)
        advanceUntilIdle()

        assertEquals(1, repo.saved.size)
        assertEquals(1500L, repo.saved[0].amountKes)
        assertEquals("M-Pesa", repo.saved[0].method)
        assertTrue(repo.saved[0].hasReceipt)
        assertEquals(RecordSpendEvent.NavigateHome, viewModel.event.first())
    }

    @Test
    fun cashSkipsCodeAndNoReceiptPath() = runTest(dispatcher) {
        val repo = FakeSpendsRepository()
        val viewModel = RecordSpendViewModel(repo)
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        listOf("8", "5", "0").forEach { viewModel.onAction(RecordSpendAction.AmountKey(it)) }
        viewModel.onAction(RecordSpendAction.Next)
        viewModel.onAction(RecordSpendAction.ItemDescription(0, "Fare"))
        viewModel.onAction(RecordSpendAction.ItemAmount(0, "850"))
        viewModel.onAction(RecordSpendAction.PaidToChanged("Driver")
        )
        viewModel.onAction(RecordSpendAction.Next)
        viewModel.onAction(RecordSpendAction.MethodSelected(PayMethod.Cash))
        viewModel.onAction(RecordSpendAction.Next)
        advanceUntilIdle()
        assertEquals(SpendStep.Receipt, viewModel.state.first().step)

        viewModel.onAction(RecordSpendAction.NoReceipt)
        viewModel.onAction(RecordSpendAction.ContinueWithoutReceipt)
        advanceUntilIdle()
        assertEquals(SpendStep.Review, viewModel.state.first().step)

        viewModel.onAction(RecordSpendAction.Save)
        advanceUntilIdle()
        assertEquals("Cash", repo.saved[0].method)
        assertEquals(false, repo.saved[0].hasReceipt)
        assertEquals(RecordSpendEvent.NavigateHome, viewModel.event.first())
    }

    private class FakeSpendsRepository : SpendsRepository {
        data class Saved(
            val amountKes: Long,
            val items: List<SpendItem>,
            val category: String,
            val method: String,
            val hasReceipt: Boolean,
        )
        val saved = mutableListOf<Saved>()

        private val flow = MutableStateFlow(
            emptyList<SpendRecord>() to SpendContext(
                categories = listOf("Supplies", "Transport"),
                projects = listOf("Office"),
                sites = listOf("Westlands HQ"),
                memberName = "Brian Otieno",
                floatKes = 18_450L,
            ),
        )

        override val snapshot: Flow<Pair<List<SpendRecord>, SpendContext>> = flow

        override suspend fun saveSpend(
            amountKes: Long,
            items: List<SpendItem>,
            category: String,
            project: String,
            site: String,
            paidTo: String,
            method: String,
            mpesaCode: String,
            hasReceipt: Boolean,
        ) {
            saved += Saved(amountKes, items, category, method, hasReceipt)
        }
    }
}
