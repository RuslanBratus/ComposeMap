package com.example.composemap.presentation.screens.tabs.locations.viewmodel

import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.composemap.data.mappers.toMarker
import com.example.composemap.domain.preferences.IPreferences
import com.example.composemap.domain.repository.IConfigurationMarkerRepository
import com.example.composemap.enums.MarkersListColors
import com.example.composemap.presentation.StateViewModel
import com.example.composemap.presentation.screens.tabs.locations.LocationsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationsViewModel @Inject constructor(
    markersRepository: IConfigurationMarkerRepository,
    private val sp: IPreferences
): StateViewModel<LocationsState>() {

    private val _uiState = MutableStateFlow<LocationsState>(LocationsState.Loading)
    override val uiState: StateFlow<LocationsState> = _uiState.asStateFlow()

    val markers = markersRepository.getAllMarkersLiveData().map { markers ->
        val mappedMarkers = markers.map { it.toMarker() }
        viewModelScope.launch {
            if (markers.isEmpty()) _uiState.emit(LocationsState.Empty)
            else {
                if (_uiState.value !is LocationsState.VerticalView)
                    _uiState.emit(LocationsState.HorizontalView(mappedMarkers,
                        textColorObject = MarkersListColors.getObjectByID(sp.getSelectedMarkersListColorID())))
                else _uiState.emit(LocationsState.VerticalView(mappedMarkers,
                    textColorObject = MarkersListColors.getObjectByID(sp.getSelectedMarkersListColorID())))
            }
        }
        mappedMarkers
    }

    fun changeScreenOrientation() {
        viewModelScope.launch {
            if (markers.value.isNullOrEmpty()) return@launch
            _uiState.emit(
                if (_uiState.value is LocationsState.HorizontalView)
                    LocationsState.VerticalView(markers.value!!,
                        textColorObject = MarkersListColors.getObjectByID(sp.getSelectedMarkersListColorID()))
                else LocationsState.HorizontalView(markers.value!!,
                    textColorObject =MarkersListColors.getObjectByID(sp.getSelectedMarkersListColorID()))
            )
        }
    }

}