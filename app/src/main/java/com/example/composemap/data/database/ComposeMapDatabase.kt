package com.example.composemap.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.composemap.data.database.dao.MarkersDao
import com.example.composemap.data.database.entity.MarkerEntity

@Database(
    version = 1,
    entities = [
        MarkerEntity::class,
    ],
    exportSchema = true
)
//@TODO Implement singleton/reusable in Dagger-Hilt + injection
abstract class ComposeMapDatabase: RoomDatabase() {
    abstract fun markersDao(): MarkersDao

//    companion object {
//        private var instance: ComposeMapDatabase? = null
//
//        fun getInstance(
//            applicationContext: Context
//        ): ComposeMapDatabase {
//            if (instance == null) {
//                instance = Room.databaseBuilder(
//                    applicationContext, ComposeMapDatabase::class.java,
//                    DATABASE_NAME
//                ).build()
//            }
//            return instance!!
//        }
//
//        private const val DATABASE_NAME ="compose.map.db"
//    }
}