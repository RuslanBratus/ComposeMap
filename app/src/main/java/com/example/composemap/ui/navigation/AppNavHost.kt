package com.example.composemap.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composemap.presentation.screens.add_marker.composable.AddMarkerScreen
import com.example.composemap.presentation.screens.edit_marker.composable.EditMarkerScreen
import com.example.composemap.presentation.screens.main.composable.MapScreen
import com.example.composemap.ui.navigation.Destinations.*
import com.example.composemap.ui.navigation.DestinationsKeys.*

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = MAP.startRoute
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(MAP.startRoute) {
            MapScreen(
                navigateToAddMarker = { location ->
                    navController.navigate(
                        "${ADD_MARKER.startRoute}/${location.latitude}/${location.longitude}")
                    { popUpTo(MAP.startRoute) } },
                navigateToEditMarker = { markerId -> navController.navigate(
                    "${EDIT_MARKER.startRoute}/${markerId}"
                )}
            )
        }
        composable(ADD_MARKER.routeWithArguments,) { backStackEntry : NavBackStackEntry ->
            AddMarkerScreen(
                latitude =  backStackEntry.arguments?.getString(LATITUDE.key)!!.toDouble(),
                longitude = backStackEntry.arguments?.getString(LONGITUDE.key)!!.toDouble(),
                popBackStack = { navController.popBackStack() }
            )

        }
        composable(EDIT_MARKER.routeWithArguments) {backStackEntry : NavBackStackEntry ->
            EditMarkerScreen(
                markerId = backStackEntry.arguments?.getString(MARKER_ID.key)!!.toInt(),
                popBackStack = { navController.popBackStack() }
            )
        }
    }
}

private enum class Destinations(val startRoute: String, val routeWithArguments: String = "") {
    MAP("map"),
    ADD_MARKER("addMarker", "addMarker/{${LATITUDE.key}}/{${LONGITUDE.key}}"),
    EDIT_MARKER("editMarker", "editMarker/{${MARKER_ID.key}}")
}

private enum class DestinationsKeys(val key: String) {
    LATITUDE("latitude"), LONGITUDE("longitude"), MARKER_ID("marker_id")
}
