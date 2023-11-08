package com.example.composemap.presentation.screens.main.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composemap.R
import com.example.composemap.data.mappers.toMarker
import com.example.composemap.domain.model.Marker
import com.example.composemap.domain.repository.IConfigurationMarkerRepository
import com.example.composemap.domain.usecases.GetMarkersUseCase
import com.example.composemap.extensions.bitmapFromLocalStorage
import com.example.composemap.presentation.mappers.toUI
import com.example.composemap.presentation.screens.main.model.MarkerUI
import com.example.composemap.presentation.screens.main.model.PolylineUI
import com.example.composemap.ui.navigation.MapScreenDestinationTypes
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val IMAGE_WIDTH_AS_MARKER = 100
private const val IMAGE_HEIGHT_AS_MARKER = 100

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getMarkersUseCase: GetMarkersUseCase,
    private val markersRepository: IConfigurationMarkerRepository
): ViewModel() {
    private val _markers = MutableSharedFlow<List<MarkerUI>>()
    val markers = _markers.asSharedFlow()
    private var _firstNavigationAction = true


    private val _isLocationPermissionGranted = MutableStateFlow(false)
    val isLocationPermissionGranted = _isLocationPermissionGranted.asStateFlow()
    private val _buildRouteToMarker = MutableSharedFlow<Marker>()
    val buildRouteToMarker = _buildRouteToMarker.asSharedFlow()
    private val _navigateToMarker = MutableSharedFlow<Marker>()
    val navigateToMarker = _navigateToMarker.asSharedFlow()
    private val _navigateToCoordinates = MutableSharedFlow<LatLng>()
    val navigateToCoordinates = _navigateToCoordinates.asSharedFlow()
    private val _routePolyline = MutableStateFlow<PolylineUI?>(null)
    val routePolyline = _routePolyline.asSharedFlow()

    @StringRes private val _errorMessagesState = MutableSharedFlow<Int>()
    val errorMessagesState = _errorMessagesState.asSharedFlow()

    fun getMarkers(context: Context) {
        viewModelScope.launch {
            val uiMarkers = mutableListOf<MarkerUI>()
            val markersDomain = getMarkersUseCase.execute()
            markersDomain?.let {
                it.forEachIndexed { index, marker ->
                    context.bitmapFromLocalStorage(
                        path = marker.imagePath,
                        imageWidth = IMAGE_WIDTH_AS_MARKER,
                        imageHeight = IMAGE_HEIGHT_AS_MARKER,
                        doOnResourceReady = { resource : Bitmap->
                            uiMarkers.add(marker.toUI(resource))
                            viewModelScope.launch {
                                if (index == markersDomain.size - 1) _markers.emit(uiMarkers)
                            }
                        }
                    )
                }
            }
        }
    }

    fun setLocationPermissionsGranted(arePermissionsGranted: Boolean) {
        viewModelScope.launch {
            _isLocationPermissionGranted.emit(arePermissionsGranted)
        }
    }

    fun savePolyline(line: PolylineUI) {
        viewModelScope.launch {
            _routePolyline.emit(line)
        }
    }

    fun impossibleRoute() {
        viewModelScope.launch {
            _errorMessagesState.emit(R.string.error_impossible_route)
        }
    }

    fun setError(errorResource: Int) {
        viewModelScope.launch {
            _errorMessagesState.emit(errorResource)
        }
    }

    fun handleNavigationAction(
        navigationTypeNumber: Int,
        argumentMarkerId: Int?,
        argumentNavigateToCoordinates: LatLng?
    ) {
        if (!_firstNavigationAction) return
        viewModelScope.launch {
            when (MapScreenDestinationTypes.DEFAULT.getTypeById(navigationTypeNumber)) {
                MapScreenDestinationTypes.DEFAULT -> {}
                MapScreenDestinationTypes.NAVIGATE_TO_MARKER -> {
                    navigateToMarker(argumentMarkerId!!)
                }
                MapScreenDestinationTypes.BUILD_ROUTE_TO_MARKER -> {
                    startBuildingRouteToMarker(markerId = argumentMarkerId!!)
                }
                MapScreenDestinationTypes.NAVIGATE_TO_COORDINATES -> {
                    navigateToCoordinates(argumentNavigateToCoordinates!!)
                }
            }
        }
    }

    private suspend fun startBuildingRouteToMarker(markerId: Int) {
        _firstNavigationAction = false
        val result = markersRepository.getMarker(markerId)
        result.onSuccess {
            _buildRouteToMarker.emit(it.toMarker())
        }
        result.onFailure {
            it.printStackTrace()
            _errorMessagesState.emit(R.string.oops_something_went_wrong)
        }
    }

    private suspend fun navigateToCoordinates(coordinates: LatLng) {
        _navigateToCoordinates.emit(coordinates)
    }

    private suspend fun navigateToMarker(markerId: Int) {
        val result = markersRepository.getMarker(markerId)
        result.onSuccess {
            _navigateToMarker.emit(it.toMarker())
        }
        result.onFailure {
            it.printStackTrace()
            _errorMessagesState.emit(R.string.oops_something_went_wrong)
        }
    }
}