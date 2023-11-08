package com.example.composemap.presentation.screens.search_places

import com.example.composemap.presentation.UiState
import com.example.composemap.presentation.screens.main.model.MarkerUI

sealed class SearchPlacesState: UiState {
    object Default: SearchPlacesState()
    object LoadingResult: SearchPlacesState()
    object EmptyResult: SearchPlacesState()
    data class ReadyResult(val value: MarkerUI): SearchPlacesState()
    data class Error(val throwable: Throwable): SearchPlacesState()
}