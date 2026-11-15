package com.example.pettycash.datasource.remote

sealed interface RemoteResult<out T> {
    data class Success<T>(val data: T) : RemoteResult<T>
    data class Error(val cause: Throwable, val message: String? = null) : RemoteResult<Nothing>
}

suspend fun <T> safeRemoteCall(block: suspend () -> T): RemoteResult<T> = try {
    RemoteResult.Success(block())
} catch (t: Throwable) {
    RemoteResult.Error(t)
}
