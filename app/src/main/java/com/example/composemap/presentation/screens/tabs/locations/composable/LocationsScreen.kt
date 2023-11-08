package com.example.composemap.presentation.screens.tabs.locations.composable

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composemap.R
import com.example.composemap.domain.model.Marker
import com.example.composemap.enums.MarkersListColors
import com.example.composemap.presentation.screens.edit_marker.composable.LoadingContent
import com.example.composemap.presentation.screens.tabs.locations.LocationsState
import com.example.composemap.presentation.screens.tabs.locations.composable.locationListItem.LocationListItemHorizontal
import com.example.composemap.presentation.screens.tabs.locations.composable.locationListItem.LocationListItemVertical
import com.example.composemap.presentation.screens.tabs.locations.composable.locationListItem.OnItemClickNavigator
import com.example.composemap.presentation.screens.tabs.locations.viewmodel.LocationsViewModel
import com.example.composemap.ui.navigation.nestedGraphs.LocationsGraph


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsScreen(
    viewModel: LocationsViewModel = hiltViewModel(),
    popBackStack: () -> Unit,
    navGraph: LocationsGraph
) {
    val uiState = viewModel.uiState.collectAsState()
    viewModel.markers.observe(LocalLifecycleOwner.current) {}

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) {
        Crossfade(uiState.value) { screenContent ->
            when (screenContent) {
                is LocationsState.Loading -> LoadingContent()
                is LocationsState.Empty -> ErrorContent(popBackStack, R.string.error_no_locations)
                is LocationsState.HorizontalView -> LocationsContent(
                    popBackStack, screenContent.data, true,
                    changeScreenOrientation = { viewModel.changeScreenOrientation() },
                    navGraph = navGraph, selectedColorObject = screenContent.textColorObject
                )
                is LocationsState.VerticalView -> LocationsContent(
                    popBackStack, screenContent.data, false,
                    changeScreenOrientation = { viewModel.changeScreenOrientation() },
                    navGraph = navGraph, selectedColorObject = screenContent.textColorObject
                )
                is LocationsState.Error -> ErrorContent(popBackStack, R.string.oops_something_went_wrong)
            }
        }
    }
}

@Composable
fun LocationsContent(
    popBackStack: () -> Unit,
    locations: List<Marker>,
    isHorizontal: Boolean,
    selectedColorObject: MarkersListColors,
    changeScreenOrientation: () -> Unit,
    navGraph: LocationsGraph
) {
    val onClickNavigator = object : OnItemClickNavigator {
        override fun editItem(markerId: Int) {
            navGraph.navigateToEditMarker(markerId = markerId)
        }
        override fun navigateToItem(markerId: Int) {
            navGraph.navigateToMapWithMarker(markerId = markerId)
        }
        override fun buildRouteToItem(markerId: Int) {
            navGraph.navigateToMapWithMarkerRoute(markerId = markerId)
        }
    }
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (backArrow,
            locationsListItems,
            btnChangeViewOrientation) = createRefs()
        Image(
            painter = painterResource(id = R.drawable.ic_back_arrow),
            contentDescription = null,
            modifier = Modifier
                .rotate(180F)
                .size(24.dp, 24.dp)
                .constrainAs(backArrow) {
                    start.linkTo(parent.start, margin = 20.dp)
                    top.linkTo(parent.top, margin = 12.dp)
                }
                .clickable { popBackStack() }
        )
        Image(
            painter = painterResource(id = if (isHorizontal) R.drawable.swap_horizontal else R.drawable.swap_vertical),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp, 24.dp)
                .constrainAs(btnChangeViewOrientation) {
                    end.linkTo(parent.end, margin = 20.dp)
                    top.linkTo(backArrow.top, margin = 0.dp)
                }
                .clickable { changeScreenOrientation.invoke() }
        )
        locationsListItems(
            data = locations,
            isHorizontal = isHorizontal,
            onClickNavigator = onClickNavigator,
            textColor = colorResource(id = selectedColorObject.colorResource),
            modifier = Modifier
                .padding(bottom = 18.dp)
                .constrainAs(locationsListItems) {
                    start.linkTo(parent.start, margin = 0.dp)
                    top.linkTo(backArrow.bottom, margin = 12.dp)
                }
        )
    }

}

@Composable
private fun ErrorContent(
    popBackStack: () -> Unit,
    @StringRes errorMessageResource: Int
) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (backArrow, errorText) = createRefs()
        Image(
            painter = painterResource(id = R.drawable.ic_back_arrow),
            contentDescription = null,
            modifier = Modifier
                .rotate(180F)
                .size(24.dp, 24.dp)
                .constrainAs(backArrow) {
                    start.linkTo(parent.start, margin = 20.dp)
                    top.linkTo(parent.top, margin = 12.dp)
                }
                .clickable { popBackStack() }
        )
        Text(text = stringResource(id = errorMessageResource),
            fontSize = 20.sp,
            modifier = Modifier.constrainAs(errorText) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            })
    }
}

@Composable
private fun locationsListItems(
    data: List<Marker>,
    isHorizontal: Boolean,
    modifier: Modifier = Modifier,
    onClickNavigator: OnItemClickNavigator,
    textColor: Color
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        if (isHorizontal) {
            data.forEach {
                LocationListItemHorizontal(
                    marker = it,
                    textColor = textColor,
                    onClickNavigator = onClickNavigator
                )
            }
        }
        else {
            data.forEach {
                LocationListItemVertical(
                    marker = it,
                    textColor = textColor,
                    onClickNavigator = onClickNavigator
                )
            }
        }
    }
}
