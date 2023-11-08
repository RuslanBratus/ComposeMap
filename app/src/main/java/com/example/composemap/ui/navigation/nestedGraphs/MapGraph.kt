package com.example.composemap.ui.navigation.nestedGraphs

import android.util.Log
import androidx.navigation.NavHostController
import com.example.composemap.ui.navigation.Destination
import com.google.android.gms.maps.model.LatLng


class MapGraph(
    private val navController: NavHostController
) {
    fun navigateToAddMarker(location: LatLng) {
        navController.navigate(
            route = Destination.AddMarkerScreen().route.plus ("/${location.latitude}/${location.longitude}")
        ) { popUpTo(Destination.MapScreen().route) } }

    fun navigateToEditMarker(markerId: Int) {
        navController.navigate(Destination.EditMarkerScreen().route.plus("/$markerId"))
    }

    fun navigateToLocations() {
        navController.navigate(Destination.LocationsScreen.route)
    }

    fun navigateToSettings() {
        navController.navigate(Destination.SettingsScreen.route)
    }

    fun navigateToSearchPlaces() {
        navController.navigate(Destination.SearchPlacesScreen.route)
    }

    fun navigateTo(destination: Destination) {
        when (destination) {
            is Destination.LocationsScreen -> navigateToLocations()
            is Destination.SettingsScreen -> navigateToSettings()
            else -> {}
        }
    }
}