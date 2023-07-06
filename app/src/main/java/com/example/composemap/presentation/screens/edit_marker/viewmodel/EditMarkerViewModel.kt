package com.example.composemap.presentation.screens.edit_marker.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.example.composemap.data.mappers.toMarker
import com.example.composemap.domain.model.Marker
import com.example.composemap.domain.repository.IConfigurationMarkerRepository
import com.example.composemap.extensions.bitmapFromLocalStorage
import com.example.composemap.extensions.deleteMarkerImage
import com.example.composemap.extensions.saveMarkerImageInternally
import com.example.composemap.presentation.StateViewModel
import com.example.composemap.presentation.mappers.toDomain
import com.example.composemap.presentation.mappers.toUI
import com.example.composemap.presentation.screens.edit_marker.EditMarkerError
import com.example.composemap.presentation.screens.edit_marker.EditMarkerState
import com.example.composemap.presentation.screens.main.model.MarkerUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val IMAGE_HEIGHT = 150
private const val IMAGE_WIDTH = 150

@HiltViewModel
class EditMarkerViewModel @Inject constructor(
    private val configurationMarkerRepository: IConfigurationMarkerRepository
): StateViewModel<EditMarkerState>() {

    private val _uiState = MutableStateFlow<EditMarkerState>(EditMarkerState.Loading)
    override val uiState = _uiState.asStateFlow()
    private val _markerEditing = MutableStateFlow<MarkerUI?>(null)
    val markerEditing : StateFlow<MarkerUI> = _markerEditing.asStateFlow() as StateFlow<MarkerUI>

    private lateinit var originalMarkerUI: MarkerUI
        private lateinit var originalMarker: Marker
    val wasMarkerDataChanged = _uiState.asStateFlow()

    fun getMarker(context: Context, markerId: Int) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { configurationMarkerRepository.getMarker(markerId) }
            result.onFailure { _uiState.emit(EditMarkerState.Error(EditMarkerError.NOT_FOUNT)) }
            result.onSuccess {
                context.bitmapFromLocalStorage(
                    path = it.imagePath,
                    imageWidth = IMAGE_WIDTH,
                    imageHeight = IMAGE_HEIGHT,
                    doOnResourceReady = { resource: Bitmap ->
                        viewModelScope.launch {
                                val markerUI = it.toMarker().toUI(resource)
                            _uiState.emit(EditMarkerState.Ready(markerUI))
                            _markerEditing.emit(markerUI)
                            originalMarker = it.toMarker()
                            originalMarkerUI = markerUI
                        }
                    }
                )
            }
        }
    }

    fun updateImageBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            _markerEditing.emit(markerEditing.value.copy(imageBitmap = bitmap))
        }
    }

    fun updateInputTitleText(title: String) {
        viewModelScope.launch {
            _markerEditing.emit(markerEditing.value.copy(title = title))
        }
    }

    fun updateInputDescriptionText(description: String) {
        viewModelScope.launch {
            _markerEditing.emit(markerEditing.value.copy(description = description))
        }
    }

    fun saveEditedMarker(applicationContext: Context) {
        viewModelScope.launch {
            if (markerEditing.value.imageBitmap != originalMarkerUI.imageBitmap) {
                deleteMarkerImage(originalMarker.imagePath)
            }
            _uiState.emit(EditMarkerState.Loading)
            val marker = markerEditing.value
            val imagePath: String? = applicationContext.saveMarkerImageInternally(
                bitmapImage = marker.imageBitmap, latitude = marker.latitude,
                longitude = marker.longitude
                )
            {
                //@TODO Implement exception handler
            }
            imagePath?.let {
                val result = withContext(this.coroutineContext) {
                    configurationMarkerRepository.updateMarker(
                        marker = marker.toDomain(imagePath)
                    )
                }
                result.onFailure {
                    _uiState.emit(
                        EditMarkerState.Error(it)
                    )
                }
                result.onSuccess {
                    _uiState.emit(EditMarkerState.Success)
                }
            }
        }
    }
}