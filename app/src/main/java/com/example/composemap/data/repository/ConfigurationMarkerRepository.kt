package com.example.composemap.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.composemap.data.database.dao.MarkersDao
import com.example.composemap.data.database.entity.MarkerEntity
import com.example.composemap.data.mappers.toEntity
import com.example.composemap.domain.model.Marker
import com.example.composemap.domain.repository.IConfigurationMarkerRepository
import com.example.composemap.extensions.safeCall
import javax.inject.Inject

class ConfigurationMarkerRepository @Inject constructor(
    private val markersDao: MarkersDao
): IConfigurationMarkerRepository {

    override suspend fun getMarker(markerId: Int): Result<MarkerEntity> {
        return try {
            Log.e("WatchingSomeStuff", "ConfigurationMarkerRepository getMarker try")
            Result.success(markersDao.getMarker(markerId))
        } catch (throwable: Throwable) {
            Log.e("WatchingSomeStuff", "ConfigurationMarkerRepository getMarker catch")
            Result.failure(throwable)
        }
    }

    override suspend fun getAllMarkers(): Result<List<MarkerEntity>> = safeCall {
        Result.success(markersDao.getAllMarkers())
    }

    override fun getAllMarkersLiveData(): LiveData<List<MarkerEntity>> {
        val result = markersDao.getAllMarkersLiveData()

        Log.e("WatchingSomeStuff", "Get markers res = $result")
        return result
    }

    override suspend fun updateMarker(marker: Marker): Result<Int> {
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