package com.example.composemap.presentation.screens.main.model

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.PolylineOptions

data class PolylineUI(
    val polyline: PolylineOptions,
    val cameraUpdate: CameraUpdate)