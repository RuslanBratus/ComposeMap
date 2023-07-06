package com.example.composemap.presentation.screens.edit_marker

import com.example.composemap.presentation.UiState
import com.example.composemap.presentation.screens.main.model.MarkerUI

sealed class EditMarkerState: UiState {
    object Loading: EditMarkerState()
    data class Ready(val value: MarkerUI): EditMarkerState()
    data class Error(val throwable: Throwable): EditMarkerState()
    object Success: EditMarkerState()
}

sealed class EditMarkerError: Throwable() {
    object NOT_FOUNT : EditMarkerError()
}