package com.example.composemap.presentation.screens.tabs.settings

import com.example.composemap.presentation.UiState

sealed class SettingsState: UiState {
    object Loading: SettingsState()
    data class Default(val selectedColorID: Int) : SettingsState()
}