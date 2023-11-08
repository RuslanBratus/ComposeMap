package com.example.composemap.ui.navigation.nestedGraphs

import androidx.navigation.NavHostController
import com.example.composemap.ui.navigation.Destination

class SearchPlacesGraph(
    private val navController: NavHostController
) {
    fun navigateToMapWithLocation(latitude: Double, longitude: Double) {
        navController.navigate(Destination.MapScreen().route.plus("/${latitude}/${longitude}/${3}"))
    }
}