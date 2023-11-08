package com.example.composemap.domain.preferences

interface IPreferences {

    fun getSelectedMarkersListColorID(): Int
    fun saveSelectedMarkersListColorID(id: Int)

}