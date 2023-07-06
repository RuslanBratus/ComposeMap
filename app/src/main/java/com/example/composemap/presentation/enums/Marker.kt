package com.example.composemap.presentation.enums

import androidx.annotation.DrawableRes
import com.example.composemap.R

enum class Marker(@DrawableRes val image: Int) {
    BLACK(R.drawable.ic_black_marker),
    BLACK_LIGHT(R.drawable.ic_light_black_marker),
    RED(R.drawable.ic_red_marker),
    GREEN(R.drawable.ic_green_marker);

    fun getMarkerByDrawable(@DrawableRes drawable: Int): Marker? {
        return when (drawable) {
            R.drawable.ic_black_marker -> BLACK
            R.drawable.ic_light_black_marker -> BLACK_LIGHT
            R.drawable.ic_red_marker -> RED
            R.drawable.ic_green_marker -> GREEN
            else -> null
        }
    }
}