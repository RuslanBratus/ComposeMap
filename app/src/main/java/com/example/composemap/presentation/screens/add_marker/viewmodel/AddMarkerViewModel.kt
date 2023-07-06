package com.example.composemap.presentation.screens.add_marker.viewmodel

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.composemap.domain.usecases.AddMarkerUseCase
import com.example.composemap.extensions.generateImageName
import com.example.composemap.extensions.saveMarkerImageInternally
import com.example.composemap.presentation.StateViewModel
import com.example.composemap.presentation.mappers.toDomain
import com.example.composemap.presentation.screens.add_marker.AddMarkerState
import com.example.composemap.presentation.screens.main.model.MarkerUI
import com.example.composemap.presentation.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AddMarkerViewModel @Inject constructor (
    private val addMarkerUseCase: AddMarkerUseCase,
) : StateViewModel<AddMarkerState>() {
    private val _uiState = MutableStateFlow<AddMarkerState>(AddMarkerState.Ready)
    override val uiState = _uiState.asStateFlow()

    private val _inputTitleText = MutableStateFlow("")
    private val _inputDescriptionText = MutableStateFlow("")
    private val _imageUri = MutableStateFlow<Uri?>(null)
    private val _bitmap = MutableStateFlow<Bitmap?>(null)


    val inputTitleText = _inputTitleText.asStateFlow()
    val inputDescriptionText = _inputDescriptionText.asStateFlow()
    val imageUri = _imageUri.asStateFlow()
    val bitmap = _bitmap.asStateFlow()

    fun updateInputTitleText(titleText: String) {
        viewModelScope.launch {
            _inputTitleText.value = titleText
        }
    }

    fun updateInputDescriptionText(descriptionText: String) {
        viewModelScope.launch {
            _inputDescriptionText.value = descriptionText
        }
    }

    fun updateImageUri(uri: Uri) {
        viewModelScope.launch {
            _imageUri.value = uri
        }
    }

    fun updateBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            _bitmap.value = bitmap
        }
    }


    fun addMarker(marker: MarkerUI, applicationContext: Context) {
        viewModelScope.launch {
            _uiState.emit(AddMarkerState.Loading)
            val imagePath: String? = applicationContext.saveMarkerImageInternally(
                bitmapImage = marker.imageBitmap, latitude = marker.latitude,
                longitude = marker.longitude
            )
            {
                //@TODO Implement exception handler
            }
            imagePath?.let {
                try {
                    val result = withContext(this.coroutineContext) {
                        addMarkerUseCase.execute(
                            AddMarkerUseCase.MarkerParameters(
                                marker.toDomain(
                                    imagePath
                                )
                            )
                        )
                    }
                    if (result != null) _uiState.emit(AddMarkerState.Success)
                }
                catch (throwable : Throwable) {
                    _uiState.emit(AddMarkerState.Error(throwable))
                }
            }
        }
    }
}