package com.example.composemap.presentation.screens.search_places.composable

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composemap.R
import com.example.composemap.extensions.collectWithLifecycle
import com.example.composemap.extensions.toastShort
import com.example.composemap.presentation.screens.search_places.composable.predictionListItem.OnPredictionClickItem
import com.example.composemap.presentation.screens.search_places.composable.predictionListItem.PredictionListItem
import com.example.composemap.presentation.screens.search_places.model.PredictionUI
import com.example.composemap.presentation.screens.search_places.viewmodel.SearchPlacesViewModel
import com.example.composemap.ui.navigation.nestedGraphs.SearchPlacesGraph

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MissingPermission")
@Composable
fun SearchPlacesScreen(
    viewModel: SearchPlacesViewModel = hiltViewModel(),
    navigationGraph: SearchPlacesGraph,
    popBackStack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val predictions = viewModel.placesPredictions.collectAsState(initial = null)
    var searchValue = viewModel.searchText.collectAsState()

    val onPredictionClickItem = object : OnPredictionClickItem {
        override fun onClick(id: String) {
            viewModel.getPlaceByID(id)
        }
    }

    viewModel.selectedPlaceLatLng.collectWithLifecycle(lifecycleOwner) {
        navigationGraph.navigateToMapWithLocation(
            latitude = it.latitude,
            longitude = it.longitude
        )
    }
    viewModel.errorState.collectWithLifecycle(lifecycleOwner) {
        context.toastShort(it)
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (searchTextField, predictionListItems) = createRefs()
        OutlinedTextField(
            value = searchValue.value,
            onValueChange = { viewModel.handleInputText(it) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = null
                )
            },
            label = { Text(text = stringResource(id = R.string.label_search)) },
            modifier = Modifier
                .constrainAs(searchTextField) {
                    start.linkTo(parent.start, margin = 75.dp)
                    end.linkTo(parent.end, margin = 75.dp)
                    top.linkTo(parent.top, margin = 10.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                }
        )
        predictions.value?.let {
            predictionListItems(
                data = it,
                onPredictionClickItem = onPredictionClickItem,
                modifier = Modifier
                    .padding(bottom = 18.dp)
                    .constrainAs(predictionListItems) {
                        start.linkTo(parent.start, margin = 20.dp)
                        top.linkTo(searchTextField.bottom, margin = 6.dp)
                    }
            )
        }
    }
}


@Composable
private fun predictionListItems(
    data: List<PredictionUI>,
    modifier: Modifier = Modifier,
    onPredictionClickItem: OnPredictionClickItem
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(end = 20.dp)
    ) {
        data.forEach {
            PredictionListItem(prediction = it, onPredictionClickItem = onPredictionClickItem)
        }
    }
}