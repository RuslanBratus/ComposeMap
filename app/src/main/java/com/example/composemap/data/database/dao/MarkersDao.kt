package com.example.composemap.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.composemap.data.database.entity.MarkerEntity
@Dao
interface MarkersDao {

    @Query("SELECT * FROM markers")
    suspend fun getAllMarkers(): List<MarkerEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(marker: MarkerEntity): Long?

    @Query("SELECT * FROM markers WHERE id=:id")
    fun getMarker(id: Int): MarkerEntity

    @Delete
    suspend fun delete(marker: MarkerEntity)

    @Update
    suspend fun updateMarker(marker: MarkerEntity): Int
}