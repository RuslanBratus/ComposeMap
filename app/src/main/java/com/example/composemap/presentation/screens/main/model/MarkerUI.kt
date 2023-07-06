package com.example.composemap.presentation.screens.main.model

import android.graphics.Bitmap

data class MarkerUI(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val title: String,
    val description: String,
    val imageBitmap: Bitmap
)