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

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    navigateToAddMarker: (location: LatLng) -> Unit,
    navigateToEditMarker: (markerId: Int) -> Unit
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
    val mapView = remember {
        MapView(context)
    }

    val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(LocalContext.current)

    fun getLastKnownLocation() {
        mFusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                //@TODO It works! Save location into variables and use them on button click
                Log.e("WatchingSomeInfo", "location.latitude = ${location.latitude}")
                Log.e("WatchingSomeInfo", "location.longitude = ${location.longitude}")
            }
        }
    }

    fun checkAndRequestLocationPermission(context : Context, permission: String,
                                          launcher: ManagedActivityResultLauncher<String, Boolean>) {
        val permissionCheckResult = ContextCompat.checkSelfPermission(context, permission)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            viewModel.setLocationPermissionsGranted(true)
            getLastKnownLocation()
        } else {
            launcher.launch(permission)
        }
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (myLocationButton) = createRefs()
        val tmp = GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties =  MapProperties(isMyLocationEnabled = locationPermissionGranted.value)
        ) {
            Log.e("WatchingSomeInfo", "InsideMap!")
            MapEffect() { map ->
                setMapLongClick(map, navigateToAddMarker)
            }

            markers.value?.forEach { markerUI ->
                Log.e("LookAtTHeData", "We got marker = $markerUI")
                Marker(
                    state = MarkerState(position = LatLng(markerUI.latitude, markerUI.longitude)),
                    title = markerUI.title,
                    snippet = markerUI.description,
                    icon = BitmapDescriptorFactory.fromBitmap(markerUI.imageBitmap),
                    onClick = {
                        navigateToEditMarker(markerUI.id)
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

private fun setMapLongClick(
    map: GoogleMap,
    navigateToAddMarker: (location : LatLng) -> Unit,
) {
    map.setOnMapLongClickListener { latLng ->
        navigateToAddMarker(latLng)
    }
}