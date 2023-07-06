package com.example.composemap.domain.base

sealed class Result<out T : Any> {

    data class Success<out T : Any>(val data: T) : Result<T>()

    data class Failure(val error: Error) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Failure -> "Error[exception=$error]"
        }
    }
}


inline fun <T : Any> Result<T>.whenSuccess(block: T.() -> Unit): Result<T> {
    if (this is Result.Success) {
        block.invoke(this.data)
    }
    return this
}

inline fun <T : Any> Result<T>.orError(block: Result.Failure.() -> T): T {
    return when (this) {
        is Result.Success -> return this.data
        is Result.Failure -> block.invoke(this)
    }
}
