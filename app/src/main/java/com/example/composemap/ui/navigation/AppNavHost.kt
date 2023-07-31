package com.example.composemap.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.composemap.ui.navigation.nestedGraphs.mapGraph

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
        mapGraph(navController)
    }
}

sealed class Destination(val route: String) {
    object MapScreen : Destination("map")
    data class AddMarkerScreen(val routeWithArgs : String = "addMarker/{latitude}/{longitude}") : Destination("addMarker")
    data class EditMarkerScreen(val routeWithArgs : String = "editMarker/{marker_id}") : Destination("editMarker")
}

enum class DestinationsKeys(val key: String) {
    LATITUDE("latitude"), LONGITUDE("longitude"), MARKER_ID("marker_id")
}