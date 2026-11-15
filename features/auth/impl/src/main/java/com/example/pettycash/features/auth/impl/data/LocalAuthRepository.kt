package com.example.pettycash.features.auth.impl.data

import com.example.pettycash.datasource.local.AuthLocalDataSource

internal class LocalAuthRepository(
    private val local: AuthLocalDataSource,
) : AuthRepository {

    private var pendingPhone: String? = null
    private var codeAttempts = 0
    private var newUser = false
    private var pendingPin: String? = null
    private var pinResetRequested = false

    override suspend fun requestCode(phone: String): Result<Unit> {
        pendingPhone = phone
        local.savePendingPhone(phone)
        codeAttempts = 0
        newUser = local.pinValue() == null
        return Result.success(Unit)
    }

    override suspend fun verifyCode(code: String): Result<Boolean> {
        if (code == DEV_CODE) {
            pendingPhone?.let { local.savePhone(it) }
            return Result.success(newUser)
        }
        codeAttempts++
        return if (codeAttempts >= MAX_CODE_ATTEMPTS) {
            Result.failure(BlockedException())
        } else {
            Result.failure(WrongCodeException(MAX_CODE_ATTEMPTS - codeAttempts))
        }
    }

    override suspend fun resendCode(): Result<Unit> {
        codeAttempts = 0
        return Result.success(Unit)
    }

    override suspend fun savePin(pin: String) {
        pendingPin = pin
    }

    override suspend fun verifyPin(pin: String): Boolean {
        val expected = pendingPin ?: local.pinValue() ?: return false
        if (pin == expected) {
            local.savePin(pin)
            pendingPin = null
            return true
        }
        return false
    }

    override suspend fun setFingerprintEnabled(enabled: Boolean) {
        local.setFingerprintEnabled(enabled)
    }

    override suspend fun isSignedIn(): Boolean = local.pinValue() != null

    override suspend fun pendingPhone(): String? = pendingPhone ?: local.phoneValue()

    override fun isNewUser(): Boolean = newUser

    override fun requestPinReset() {
        pinResetRequested = true
    }

    override fun consumePinReset(): Boolean {
        val requested = pinResetRequested
        pinResetRequested = false
        return requested
    }

    override fun resetSession() {
        pendingPhone = null
        pendingPin = null
        pinResetRequested = false
        codeAttempts = 0
    }

    class WrongCodeException(val triesLeft: Int) : Exception()
    class BlockedException : Exception()

    private companion object {
        const val DEV_CODE = "123456"
        const val MAX_CODE_ATTEMPTS = 3
    }
}
