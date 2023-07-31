package com.example.composemap.ui.navigation.nestedGraphs

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.composemap.presentation.screens.add_marker.composable.AddMarkerScreen
import com.example.composemap.presentation.screens.edit_marker.composable.EditMarkerScreen
import com.example.composemap.presentation.screens.main.composable.MapScreen
import com.example.composemap.ui.navigation.Destination
import com.example.composemap.ui.navigation.DestinationsKeys
import com.example.composemap.ui.navigation.MAIN_ROUTE
import com.google.android.gms.maps.model.LatLng

class MapGraph(
    private val navController: NavHostController
) {
    fun navigateToAddMarker(location: LatLng) {
        navController.navigate(
            route = Destination.AddMarkerScreen().route.plus ("/${location.latitude}/${location.longitude}")
        ) { popUpTo(Destination.MapScreen.route) } }

    fun navigateToEditMarker(markerId: Int) {
        navController.navigate(Destination.EditMarkerScreen().route.plus("/$markerId"))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.mapGraph(
    navController: NavHostController
) {
    val mapGraph = MapGraph(navController)
    navigation(
        startDestination = Destination.MapScreen.route,
        route = MAIN_ROUTE,
    ) {
        composable(Destination.MapScreen.route) {
            MapScreen(
                navigationGraph = mapGraph
            )
        }
        composable(Destination.AddMarkerScreen().routeWithArgs) { backStackEntry : NavBackStackEntry ->
            AddMarkerScreen(
                latitude =  backStackEntry.arguments?.getString(DestinationsKeys.LATITUDE.key)!!.toDouble(),
                longitude = backStackEntry.arguments?.getString(DestinationsKeys.LONGITUDE.key)!!.toDouble(),
                popBackStack = { navController.popBackStack() }
            )
        }
        composable(Destination.EditMarkerScreen().routeWithArgs) { backStackEntry : NavBackStackEntry ->
            EditMarkerScreen(
                markerId = backStackEntry.arguments?.getString(DestinationsKeys.MARKER_ID.key)!!.toInt(),
                popBackStack = { navController.popBackStack() }
            )
        }
    }
}