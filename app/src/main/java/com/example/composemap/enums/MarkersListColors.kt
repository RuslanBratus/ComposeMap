package com.example.composemap.enums

import com.example.composemap.R

enum class MarkersListColors(val id: Int, val colorResource: Int) {
    APP_COLOR(id = 0, colorResource = R.color.app_color),
    BLACK(id = 1, colorResource = R.color.black),
    PURPLE_200(id = 2, colorResource = R.color.purple_200),
    PURPLE_700(id = 3, colorResource = R.color.purple_700),
    TEAL_700(id = 4, colorResource = R.color.teal_700),;

    companion object {
        fun getObjectByID(id: Int): MarkersListColors {
            MarkersListColors.values().forEach {
                if (it.id == id) return it
            }
            return BLACK
        }
    }
}