package com.example.composemap.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

interface UiState

abstract class StateViewModel<State: UiState>: ViewModel() {
    abstract val uiState: StateFlow<State>
}