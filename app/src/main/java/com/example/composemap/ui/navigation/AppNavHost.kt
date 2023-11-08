package com.example.composemap.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

const val ROOT_ROUTE = "root_route"
const val MAIN_ROUTE = "main_route"

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MAIN_ROUTE,
        route = ROOT_ROUTE
    ) {
        mainGraph(navController)
    }
}

sealed class Destination(val route: String) {
    data class MapScreen(
        val routeWithMarker : String = "map/{marker_id}/{route_type_number}",
        val routeWithLatLng : String = "map/{latitude}/{longitude}/{route_type_number}"
    )
        : Destination("map")
    object LocationsScreen : Destination("locations")
    object SettingsScreen : Destination("settings")
    object SearchPlacesScreen : Destination("search_places")
    data class AddMarkerScreen(val routeWithArgs : String = "addMarker/{latitude}/{longitude}") : Destination("addMarker")
    data class EditMarkerScreen(val routeWithArgs : String = "editMarker/{marker_id}") : Destination("editMarker")
}

enum class DestinationsKeys(val key: String) {
    LATITUDE("latitude"),
    LONGITUDE("longitude"),
    MARKER_ID("marker_id"),
    ROUTE_TYPE_NUMBER("route_type_number"),
}

enum class MapScreenDestinationTypes(val id: Int) {
    DEFAULT(0),
    NAVIGATE_TO_MARKER(1),
    BUILD_ROUTE_TO_MARKER(2),
    NAVIGATE_TO_COORDINATES(3);

    fun getTypeById(id: Int): MapScreenDestinationTypes {
        return when (id) {
            0 -> DEFAULT
            1 -> NAVIGATE_TO_MARKER
            2 -> BUILD_ROUTE_TO_MARKER
            3 -> NAVIGATE_TO_COORDINATES
            else -> DEFAULT
        }
    }
}