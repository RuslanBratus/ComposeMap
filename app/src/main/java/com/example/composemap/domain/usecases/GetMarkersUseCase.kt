package com.example.composemap.domain.usecases

import com.example.composemap.data.database.dao.MarkersDao
import com.example.composemap.data.mappers.toMarker
import com.example.composemap.domain.model.Marker
import com.example.composemap.domain.usecases.interfaces.EmptyParamsUseCase
import com.example.composemap.domain.usecases.interfaces.UseCase
import javax.inject.Inject

class GetMarkersUseCase @Inject constructor(
    private val markersDao: MarkersDao
): EmptyParamsUseCase<List<Marker>?> {

    override suspend fun execute(parameters: UseCase.EmptyParameters): List<Marker>? {
        //@TODO it should be paging/flow
        val markers = markersDao.getAllMarkers()
        return if (markers.isEmpty()) null
        else {
            markers.map { it!!.toMarker() }
        }
    }
}