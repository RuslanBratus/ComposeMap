package com.example.composemap.domain.repository

import androidx.lifecycle.LiveData
import com.example.composemap.data.database.entity.MarkerEntity
import com.example.composemap.domain.model.Marker

interface IConfigurationMarkerRepository {

    suspend fun getMarker(markerId: Int): Result<MarkerEntity>

    suspend fun getAllMarkers(): Result<List<MarkerEntity>>

    fun getAllMarkersLiveData(): LiveData<List<MarkerEntity>>

    suspend fun updateMarker(marker: Marker): Result<Int>

    suspend fun deleteMarker(marker: Marker)

}