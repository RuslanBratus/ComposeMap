package com.example.composemap.domain.usecases

import com.example.composemap.data.database.dao.MarkersDao
import com.example.composemap.data.mappers.toEntity
import com.example.composemap.domain.model.Marker
import com.example.composemap.domain.usecases.interfaces.UseCase
import javax.inject.Inject

class AddMarkerUseCase @Inject constructor(
    private val markersDao: MarkersDao
): UseCase<AddMarkerUseCase.MarkerParameters, Long?> {

    override suspend fun execute(parameters: MarkerParameters): Long? {
        return markersDao.insert(parameters.marker.toEntity())
    }

    data class MarkerParameters(val marker: Marker): UseCase.Parameters
}