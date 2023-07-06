package com.example.composemap.data.repository

import android.util.Log
import com.example.composemap.data.database.dao.MarkersDao
import com.example.composemap.data.database.entity.MarkerEntity
import com.example.composemap.data.mappers.toEntity
import com.example.composemap.domain.model.Marker
import com.example.composemap.domain.repository.IConfigurationMarkerRepository
import javax.inject.Inject

class ConfigurationMarkerRepository @Inject constructor(
    private val markersDao: MarkersDao
): IConfigurationMarkerRepository {
    override suspend fun getMarker(markerId: Int): Result<MarkerEntity> {
        return try {
            Result.success(markersDao.getMarker(markerId))
        } catch (throwable: Throwable) {
            Result.failure(throwable)
        }
    }

    override suspend fun updateMarker(marker: Marker): Result<Int> {
        Log.e("LookAtTHeData", "We are updating = $marker")
        return try {
            Result.success(markersDao.updateMarker(marker.toEntity()))
        } catch (throwable: Throwable) {
            Result.failure(throwable)
        }
    }

    override suspend fun deleteMarker(marker: Marker) {
        TODO("Not yet implemented")
    }

}