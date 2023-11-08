package com.example.composemap.presentation.screens.tabs.settings.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.composemap.domain.preferences.IPreferences
import com.example.composemap.presentation.StateViewModel
import com.example.composemap.presentation.screens.tabs.settings.SettingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sp : IPreferences
): StateViewModel<SettingsState>() {
    private val _uiState = MutableStateFlow<SettingsState>(SettingsState.Loading)
    override val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

    fun saveSelectedMarkersListColor(id: Int) {
        sp.saveSelectedMarkersListColorID(id)
        getSelectedMarkersListColor()
    }

    fun getSelectedMarkersListColor() {
        viewModelScope.launch {
            _uiState.emit(SettingsState.Default(selectedColorID = sp.getSelectedMarkersListColorID()))
        }
    }

}