package com.example.composemap.extensions

import kotlin.Exception

suspend fun <T : Any> safeCall(
    call: suspend () -> Result<T>
): Result<T> {
    return try {
        call.invoke()
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }
}
