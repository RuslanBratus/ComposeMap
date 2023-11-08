package com.example.composemap.ui.navigation.nestedGraphs

import androidx.navigation.NavHostController
import com.example.composemap.ui.navigation.Destination

class LocationsGraph(
    private val navController: NavHostController
) {
    fun navigateToEditMarker(markerId: Int) {
        navController.navigate(Destination.EditMarkerScreen().route.plus("/$markerId"))
    }

    fun navigateToMapWithMarker(markerId: Int) {
        navController.navigate( Destination.MapScreen().route.plus("/${markerId}/${1}"))
    }

    fun navigateToMapWithMarkerRoute(markerId: Int) {
        navController.navigate( Destination.MapScreen().route.plus("/${markerId}/${2}"))
    }
}