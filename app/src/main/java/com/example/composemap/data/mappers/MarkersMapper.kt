package com.example.composemap.data.mappers

import com.example.composemap.data.database.entity.MarkerEntity
import com.example.composemap.domain.model.Marker

fun Marker.toEntity() =
    MarkerEntity(
        id = id,
        latitude = latitude,
        longitude = longitude,
        title = title,
        description = description,
        imagePath = imagePath
    )

fun MarkerEntity.toMarker() =
    Marker(
        id = id,
        latitude = latitude,
        longitude = longitude,
        title = title,
        description = description,
        imagePath = imagePath
    )