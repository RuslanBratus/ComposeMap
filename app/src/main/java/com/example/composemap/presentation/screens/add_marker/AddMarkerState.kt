package com.example.composemap.presentation.screens.add_marker

import com.example.composemap.presentation.UiState

sealed class AddMarkerState: UiState {
    object Loading: AddMarkerState()
    object Ready: AddMarkerState()
    data class Error(val throwable: Throwable): AddMarkerState()
    object Success: AddMarkerState()
}