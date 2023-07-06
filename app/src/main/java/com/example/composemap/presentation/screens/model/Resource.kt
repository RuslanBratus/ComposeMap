package com.example.composemap.presentation.screens.model

sealed class Resource {
    data class Success<out P>(val value: P): Resource()
    data class Error(val throwable: Throwable): Resource()
}