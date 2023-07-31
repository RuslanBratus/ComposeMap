package com.example.composemap.presentation.screens.main.composable

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composemap.R
import com.example.composemap.presentation.screens.main.viewmodel.MapViewModel
import com.example.composemap.ui.components.DrawerContent
import com.example.composemap.ui.components.DrawerItemInfo
import com.example.composemap.ui.navigation.nestedGraphs.MapGraph
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState


@OptIn(MapsComposeExperimentalApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    navigationGraph: MapGraph,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    ) {
    val locationPermissionGranted = viewModel.isLocationPermissionGranted.collectAsState()
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.setLocationPermissionsGranted(true)
        } else {
            //TODO Show dialog
        }
    }
    val context = LocalContext.current
    val markers = viewModel.markers.collectAsState(null)
    viewModel.getMarkers(context)

    fun checkAndRequestLocationPermission(context : Context, permission: String,
                                          launcher: ManagedActivityResultLauncher<String, Boolean>) {
        val permissionCheckResult = ContextCompat.checkSelfPermission(context, permission)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            viewModel.setLocationPermissionsGranted(true)
        } else {
            launcher.launch(permission)
        }
    }

//    ModalNavigationDrawer(
//        drawerState = drawerState,
//        drawerContent = {
//            DrawerContent(
//                drawerState = drawerState,
//                menuItems = generateDrawerButtons(),
//                defaultPick = ,
//                onClick = )
//        }) {
//
//    }
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (myLocationButton) = createRefs()
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties =  MapProperties(isMyLocationEnabled = locationPermissionGranted.value)
        ) {
            MapEffect { map ->
                map.setOnMapLongClickListener { latLng ->
                    navigationGraph.navigateToAddMarker(latLng)
                }
            }

            markers.value?.forEach { markerUI ->
                Marker(
                    state = MarkerState(position = LatLng(markerUI.latitude, markerUI.longitude)),
                    title = markerUI.title,
                    snippet = markerUI.description,
                    icon = BitmapDescriptorFactory.fromBitmap(markerUI.imageBitmap),
                    onClick = {
                        navigationGraph.navigateToEditMarker(markerUI.id)
                        return@Marker true
                    }
                )
            }

        }

        if (!locationPermissionGranted.value) {
            Image(
                painter = painterResource(
                    id = R.drawable.ic_my_location_question
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp, 28.dp)
                    .constrainAs(myLocationButton) {
                        top.linkTo(parent.top, margin = 10.dp)
                        end.linkTo(parent.end, margin = 10.dp)
                    }
                    .clickable {
                        checkAndRequestLocationPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            launcher
                        )
                    }

            )
        }

    }
}

//@TODO!
//fun generateDrawerButtons(): List<DrawerItemInfo<Enum<*>>> {
//    return arrayListOf(
//        DrawerItemInfo(
//            MainNavOption.HomeScreen,
//            R.string.drawer_home,
//            R.drawable.ic_home,
//            R.string.drawer_home_description
//        ),
//        DrawerItemInfo(
//            MainNavOption.SettingsScreen,
//            R.string.drawer_settings,
//            R.drawable.ic_settings,
//            R.string.drawer_settings_description
//        ),
//        DrawerItemInfo(
//            MainNavOption.AboutScreen,
//            R.string.drawer_about,
//            R.drawable.ic_info,
//            R.string.drawer_info_description
//        )
//    )
//}
