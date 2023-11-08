package com.example.composemap.data.datasource

import android.content.Context
import com.example.composemap.domain.preferences.IPreferences
import com.example.composemap.enums.MarkersListColors
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Preferences @Inject constructor(
    @ApplicationContext context: Context
): IPreferences {

    companion object {
        const val SHARED_PREFERENCES_NAME = "COMPOSE_MAP"
        const val KEY_CURRENT_MARKERS_LIST_COLOR_ID = "CURRENT_MARKERS_LIST_COLOR_ID"
    }

    private val sharedPreferences = context.getSharedPreferences(
        SHARED_PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )


    override fun getSelectedMarkersListColorID(): Int =
        sharedPreferences.getInt(KEY_CURRENT_MARKERS_LIST_COLOR_ID, MarkersListColors.BLACK.id)

    override fun saveSelectedMarkersListColorID(id: Int) {
        sharedPreferences
            .edit()
            .putInt(KEY_CURRENT_MARKERS_LIST_COLOR_ID, id)
            .apply()
    }

}