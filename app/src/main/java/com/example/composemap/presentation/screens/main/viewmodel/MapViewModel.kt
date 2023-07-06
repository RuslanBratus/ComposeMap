package com.example.composemap.presentation.screens.main.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.composemap.domain.usecases.GetMarkersUseCase
import com.example.composemap.extensions.bitmapFromLocalStorage
import com.example.composemap.presentation.mappers.toUI
import com.example.composemap.presentation.screens.main.model.MarkerUI
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
    private val getMarkersUseCase: GetMarkersUseCase
): ViewModel() {
    private val _markers = MutableSharedFlow<List<MarkerUI>>()
    private val _isLocationPermissionGranted = MutableStateFlow(false)

    val isLocationPermissionGranted = _isLocationPermissionGranted.asStateFlow()
    val markers = _markers.asSharedFlow()

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
}