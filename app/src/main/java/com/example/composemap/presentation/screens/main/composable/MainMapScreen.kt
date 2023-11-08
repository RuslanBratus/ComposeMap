package com.example.composemap.presentation.screens.main.composable

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composemap.R
import com.example.composemap.extensions.collectWithLifecycle
import com.example.composemap.extensions.toastShort
import com.example.composemap.presentation.screens.main.model.PolylineUI
import com.example.composemap.presentation.screens.main.viewmodel.MapViewModel
import com.example.composemap.ui.components.DrawerContent
import com.example.composemap.ui.components.DrawerItemInfo
import com.example.composemap.ui.components.OnNavigationClickListener
import com.example.composemap.ui.components.TopMapBar
import com.example.composemap.ui.navigation.Destination
import com.example.composemap.ui.navigation.nestedGraphs.MapGraph
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.model.DirectionsResult
import kotlinx.coroutines.launch
import java.io.IOException


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MissingPermission")
@OptIn(MapsComposeExperimentalApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    navigationGraph: MapGraph,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    argumentMarkerId: Int? = null,
    argumentNavigateToCoordinates: LatLng? = null,
    argumentNavigationTypeNumber: Int = 0
    ) {
    val markers = viewModel.markers.collectAsState(null)
    val navigateToMarkerState = viewModel.navigateToMarker.collectAsState(initial = null)
    val navigateToCoordinatesState = viewModel.navigateToCoordinates.collectAsState(initial = null)
    val coroutineScope = rememberCoroutineScope()
    val cameraPositionState: CameraPositionState = rememberCameraPositionState()
    val context = LocalContext.current
    val polyline = viewModel.routePolyline.collectAsState(initial = null)
    val fusedLocationProviderClient =
        remember { LocationServices.getFusedLocationProviderClient(context) }

    val geoApiContext: GeoApiContext = GeoApiContext.Builder()
        .apiKey(stringResource(id = R.string.MAPS_API_KEY))
        .build()
    val locationPermissionGranted = viewModel.isLocationPermissionGranted.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.setLocationPermissionsGranted(true)
        } else {
            context.toastShort(R.string.error_no_access_to_location_granted_show_location)
        }
    }


    viewModel.errorMessagesState.collectWithLifecycle(LocalLifecycleOwner.current) {
        context.toastShort(it)
    }
    viewModel.buildRouteToMarker.collectWithLifecycle(LocalLifecycleOwner.current) { destinationMarker ->
        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnCompleteListener {
            if (it.isSuccessful)
                if (it.result != null) {
                    getRoute(
                        geoApiContext = geoApiContext,
                        currentDestination = com.google.maps.model.LatLng(it.result.latitude, it.result.longitude),
                        finalDestination = com.google.maps.model.LatLng(
                            destinationMarker.latitude,
                            destinationMarker.longitude
                        ),
                        viewModel = viewModel
                    )
                }
                else context.toastShort(R.string.error_location_is_turn_off)
            else context.toastShort(R.string.error_no_access_to_location_granted)
        }
    }

    viewModel.getMarkers(context)
    viewModel.handleNavigationAction(
        navigationTypeNumber = argumentNavigationTypeNumber,
        argumentMarkerId = argumentMarkerId,
        argumentNavigateToCoordinates = argumentNavigateToCoordinates
    )

    fun checkAndRequestLocationPermission(
        context : Context,
        permission: String,
        launcher: ManagedActivityResultLauncher<String, Boolean>
    ) {
        val permissionCheckResult = ContextCompat.checkSelfPermission(context, permission)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            viewModel.setLocationPermissionsGranted(true)
        } else {
            launcher.launch(permission)
        }
    }

    ModalNavigationDrawer(
        gesturesEnabled = drawerState.isOpen,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    drawerState = drawerState,
                    menuItems = generateDrawerButtons(),
                    defaultPick = Destination.MapScreen(),
                    navigateListener = object: OnNavigationClickListener{
                        override fun onClick(destination: Destination) {
                            navigationGraph.navigateTo(destination)
                        }
                    })
            }
        }) {
        Column {
            TopMapBar(onNavigationIconClick = { coroutineScope.launch { drawerState.open() } })
            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                val (myLocationButton, searchTextField) = createRefs()
                GoogleMap(
                    cameraPositionState = cameraPositionState,
                    modifier = Modifier.fillMaxSize(),
                    properties =  MapProperties(isMyLocationEnabled = locationPermissionGranted.value)
                ) {
                    navigateToMarkerState.value?.let {
                        cameraPositionState.move(CameraUpdateFactory.newLatLng(
                            LatLng(it.latitude, it.longitude)
                        ))
                    }
                    navigateToCoordinatesState.value?.let {
                        Log.e("WatchingSomeStuff", "navigateToCoordinatesState")
                        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(
                            LatLng(it.latitude, it.longitude), 12f
                        ))
                    }
                    polyline.value?.let { polylineUI ->
                        polylineUI.polyline.points.let {
                            Polyline(points = it)
                        }
                        cameraPositionState.move(polylineUI.cameraUpdate)
                    }
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
                OutlinedTextField(
                    value = "",
                    readOnly = true,
                    onValueChange = {},
                    leadingIcon = { Icon(painter = painterResource(id = R.drawable.ic_search), contentDescription = null) },
                    label = { Text(text = stringResource(id = R.string.label_search)) },
                    modifier = Modifier
                        .onFocusChanged {
                            if (it.isFocused) {
                                navigationGraph.navigateToSearchPlaces()
                            }
                        }
                        .constrainAs(searchTextField) {
                            start.linkTo(parent.start, margin = 75.dp)
                            end.linkTo(parent.end, margin = 75.dp)
                            top.linkTo(parent.top, margin = 10.dp)
                            width = Dimension.fillToConstraints
                            height = Dimension.wrapContent
                        }
                )
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
    }
}

fun getRoute(
    geoApiContext: GeoApiContext,
    currentDestination: com.google.maps.model.LatLng,
    finalDestination: com.google.maps.model.LatLng,
    viewModel: MapViewModel) {

    val result: DirectionsResult?
    try {
        result = DirectionsApi.newRequest(geoApiContext)
            .origin(currentDestination)
            .destination(finalDestination)
            .await()
        if (result.routes.isNullOrEmpty()) {
            viewModel.impossibleRoute()
            return
        }
        val path = result.routes[0].overviewPolyline.decodePath()

        val line = PolylineOptions()
        val latLngBuilder = LatLngBounds.Builder()

        for (i in path.indices) {
            line.add(LatLng(path[i].lat, path[i].lng))
            latLngBuilder.include(LatLng(path[i].lat, path[i].lng))
        }

        line.width(16f).color(R.color.app_color)
        line.color(R.color.purple_700)

        val latLngBounds = latLngBuilder.build()
        val track = CameraUpdateFactory.newLatLngBounds(
            latLngBounds,
            25
        )

        viewModel.savePolyline(
            PolylineUI(
                polyline = line,
                cameraUpdate = track
            )
        )

    } catch (e: ApiException) {
        viewModel.setError(R.string.oops_something_went_wrong)
        e.printStackTrace()
    } catch (e: InterruptedException) {
        viewModel.setError(R.string.oops_something_went_wrong)
        e.printStackTrace()
    } catch (e: IOException) {
        viewModel.setError(R.string.oops_something_went_wrong)
        e.printStackTrace()
    }
}

private fun generateDrawerButtons(): List<DrawerItemInfo<Destination>> {
    return arrayListOf(
        DrawerItemInfo(
            Destination.LocationsScreen,
            R.string.tab_list_title,
            R.drawable.ic_list,
            R.string.description
        ),
        DrawerItemInfo(
            Destination.SettingsScreen,
            R.string.tab_settings_title,
            R.drawable.ic_settings,
            R.string.description
        )
    )
}