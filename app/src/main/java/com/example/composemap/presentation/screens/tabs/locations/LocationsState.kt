package com.example.composemap.presentation.screens.tabs.locations

import com.example.composemap.domain.model.Marker
import com.example.composemap.enums.MarkersListColors
import com.example.composemap.presentation.UiState

sealed class LocationsState: UiState {
    object Loading: LocationsState()
    object Empty: LocationsState()
    data class Error(val throwable: Throwable): LocationsState()
    data class HorizontalView(val data: List<Marker>, val textColorObject: MarkersListColors): LocationsState()
    data class VerticalView(val data: List<Marker>, val textColorObject: MarkersListColors): LocationsState()
}