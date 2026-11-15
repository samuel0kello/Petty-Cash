package com.example.pettycash.features.onboarding.impl.splash

import com.example.pettycash.features.onboarding.impl.splash.SplashEvent.NavigateToIntro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun emitsNavigateToIntroAfterSplashDelay() = runTest(dispatcher) {
        val viewModel = SplashViewModel()
        val events = mutableListOf<SplashEvent>()
        backgroundScope.launch { viewModel.state.collect {} }
        backgroundScope.launch { viewModel.event.collect { events.add(it) } }

        advanceTimeBy(1499)
        assertEquals(emptyList<SplashEvent>(), events)

        advanceTimeBy(1500)
        advanceUntilIdle()
        assertEquals(listOf(NavigateToIntro), events)
    }
}
