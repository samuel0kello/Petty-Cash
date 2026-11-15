package com.example.pettycash.features.auth.impl.data

interface AuthRepository {
    suspend fun requestCode(phone: String): Result<Unit>
    suspend fun verifyCode(code: String): Result<Boolean>
    suspend fun resendCode(): Result<Unit>
    suspend fun savePin(pin: String)
    suspend fun verifyPin(pin: String): Boolean
    suspend fun setFingerprintEnabled(enabled: Boolean)
    suspend fun isSignedIn(): Boolean
    suspend fun pendingPhone(): String?
    fun isNewUser(): Boolean
    fun requestPinReset()
    fun consumePinReset(): Boolean
    fun resetSession()
}
