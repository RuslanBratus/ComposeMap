package com.example.composemap.ui.navigation

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.composemap.presentation.screens.add_marker.composable.AddMarkerScreen
import com.example.composemap.presentation.screens.edit_marker.composable.EditMarkerScreen
import com.example.composemap.presentation.screens.main.composable.MapScreen
import com.example.composemap.presentation.screens.search_places.composable.SearchPlacesScreen
import com.example.composemap.presentation.screens.tabs.locations.composable.LocationsScreen
import com.example.composemap.presentation.screens.tabs.settings.composable.SettingsScreen
import com.example.composemap.ui.navigation.nestedGraphs.LocationsGraph
import com.example.composemap.ui.navigation.nestedGraphs.MapGraph
import com.example.composemap.ui.navigation.nestedGraphs.SearchPlacesGraph
import com.google.android.gms.maps.model.LatLng

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.mainGraph(
    navController: NavHostController
) {
    Log.e("WatchingSomeNavigationStuff", "NavGraphBuilder.mapGraph")
    val mapGraph = MapGraph(navController)
    val locationsGraph = LocationsGraph(navController)
    val searchPlacesGraph = SearchPlacesGraph(navController)

    navigation(
        startDestination = Destination.MapScreen().route,
        route = MAIN_ROUTE,
    ) {
        composable(Destination.MapScreen().route) {
            MapScreen(
                navigationGraph = mapGraph,
            )
        }
        composable(Destination.MapScreen().routeWithMarker) { backStackEntry : NavBackStackEntry ->
            HandleMapScreenDestination(
                mapGraph = mapGraph,
                backStackEntry = backStackEntry
            )
        }
        composable(Destination.MapScreen().routeWithLatLng) { backStackEntry : NavBackStackEntry ->
            HandleMapScreenDestination(
                mapGraph = mapGraph,
                backStackEntry = backStackEntry
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
        composable(Destination.LocationsScreen.route) { backStackEntry : NavBackStackEntry ->
            LocationsScreen(
                popBackStack = { navController.popBackStack() },
                navGraph = locationsGraph
            )
        }
        composable(Destination.SettingsScreen.route) { backStackEntry : NavBackStackEntry ->
            SettingsScreen(
                popBackStack = { navController.popBackStack() }
            )
        }
        composable(Destination.SearchPlacesScreen.route) { backStackEntry : NavBackStackEntry ->
            SearchPlacesScreen(
                popBackStack = { navController.popBackStack() },
                navigationGraph = searchPlacesGraph
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HandleMapScreenDestination(
    mapGraph: MapGraph,
    backStackEntry : NavBackStackEntry
) {
    val routeType = MapScreenDestinationTypes.DEFAULT.getTypeById(
        backStackEntry.arguments?.getString(DestinationsKeys.ROUTE_TYPE_NUMBER.key)!!.toInt())

    Log.e("WatchingSomeStuff", "HandleMapScreenDestination routeType = $routeType")
    when (routeType) {
        MapScreenDestinationTypes.DEFAULT ->
            MapScreen(
                navigationGraph = mapGraph
            )

        MapScreenDestinationTypes.NAVIGATE_TO_MARKER, MapScreenDestinationTypes.BUILD_ROUTE_TO_MARKER  ->
            MapScreen(
                navigationGraph = mapGraph,
                argumentMarkerId = backStackEntry.arguments?.getString(DestinationsKeys.MARKER_ID.key)!!.toInt(),
                argumentNavigationTypeNumber = routeType.id
            )

        MapScreenDestinationTypes.NAVIGATE_TO_COORDINATES -> {
            val latitude = backStackEntry.arguments?.getString(DestinationsKeys.LATITUDE.key)!!.toDouble()
            val longitude = backStackEntry.arguments?.getString(DestinationsKeys.LONGITUDE.key)!!.toDouble()
            MapScreen(
                navigationGraph = mapGraph,
                argumentNavigateToCoordinates = LatLng(latitude, longitude),
                argumentNavigationTypeNumber = routeType.id
            )
        }
    }
}

