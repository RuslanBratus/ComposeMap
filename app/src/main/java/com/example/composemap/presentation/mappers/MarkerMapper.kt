package com.example.composemap.presentation.mappers

import android.graphics.Bitmap
import com.example.composemap.domain.model.Marker
import com.example.composemap.presentation.screens.main.model.MarkerUI

fun MarkerUI.toDomain(imagePath: String) =
    Marker(
        id = id,
        latitude = latitude,
        longitude = longitude,
        title = title,
        description = description,
        imagePath = imagePath
    )

fun Marker.toUI(bitmap: Bitmap) =
    MarkerUI(
        id = id,
        latitude = latitude,
        longitude = longitude,
        title = title,
        description = description,
        imageBitmap = bitmap
    )
