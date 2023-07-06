package com.example.composemap.domain.model

import android.graphics.Bitmap

data class Marker(
    val id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val title: String,
    val description: String,
    val imagePath: String
)