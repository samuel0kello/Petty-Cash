package com.example.pettycash.features.auth.impl.phonenumber

import com.example.pettycash.features.auth.impl.data.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PhoneNumberViewModelTest {

    @Test
    fun `normalize accepts local formats`() {
        assertEquals("254712345678", PhoneNumberViewModel.normalizeKenyanPhone("0712345678"))
        assertEquals("254712345678", PhoneNumberViewModel.normalizeKenyanPhone("712345678"))
        assertEquals("254712345678", PhoneNumberViewModel.normalizeKenyanPhone("+254712345678"))
        assertEquals("254712345678", PhoneNumberViewModel.normalizeKenyanPhone("254712345678"))
    }

    @Test
    fun `normalize rejects invalid numbers`() {
        assertNull(PhoneNumberViewModel.normalizeKenyanPhone("12345"))
        assertNull(PhoneNumberViewModel.normalizeKenyanPhone("07123456789"))
        assertNull(PhoneNumberViewModel.normalizeKenyanPhone("0812345678"))
        assertNull(PhoneNumberViewModel.normalizeKenyanPhone(""))
    }

    @Test
    fun `formatForDisplay shows local format`() {
        assertEquals("0712345678", PhoneNumberViewModel.formatForDisplay("254712345678"))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `continue with invalid phone sets error and does not navigate`() = runTest {
        val repo = FakeAuthRepository()
        val viewModel = PhoneNumberViewModel(repo)
        viewModel.onAction(PhoneNumberAction.OnPhoneChange("12345"))
        viewModel.onAction(PhoneNumberAction.OnContinue)
        advanceUntilIdle()

        assertTrue(viewModel.state.first().isError)
        assertTrue(repo.requestedPhones.isEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `continue with valid phone requests code and navigates`() = runTest {
        val repo = FakeAuthRepository()
        val viewModel = PhoneNumberViewModel(repo)
        viewModel.onAction(PhoneNumberAction.OnPhoneChange("0712345678"))
        viewModel.onAction(PhoneNumberAction.OnContinue)
        advanceUntilIdle()

        assertEquals(listOf("254712345678"), repo.requestedPhones)
        assertEquals(PhoneNumberEvent.NavigateToSmsCode, viewModel.event.first())
    }

    private class FakeAuthRepository : AuthRepository {
        val requestedPhones = mutableListOf<String>()
        override suspend fun requestCode(phone: String): Result<Unit> {
            requestedPhones.add(phone)
            return Result.success(Unit)
        }
        override suspend fun verifyCode(code: String) = Result.success(false)
        override suspend fun resendCode() = Result.success(Unit)
        override suspend fun savePin(pin: String) = Unit
        override suspend fun verifyPin(pin: String) = false
        override suspend fun setFingerprintEnabled(enabled: Boolean) = Unit
        override suspend fun isSignedIn() = false
        override suspend fun pendingPhone(): String? = null
        override fun isNewUser() = true
        override fun requestPinReset() = Unit
        override fun consumePinReset() = false
        override fun resetSession() = Unit
    }
}
