package com.example.composemap.presentation.screens.search_places.viewmodel

import android.content.Context
import android.graphics.Typeface
import android.text.style.StyleSpan
import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.example.composemap.R
import com.example.composemap.presentation.StateViewModel
import com.example.composemap.presentation.screens.search_places.SearchPlacesState
import com.example.composemap.presentation.screens.search_places.model.PredictionUI
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class SearchPlacesViewModel @Inject constructor(
    @ApplicationContext context: Context
): StateViewModel<SearchPlacesState>() {

    private val _uiState = MutableStateFlow<SearchPlacesState>(SearchPlacesState.Default)
    override val uiState: StateFlow<SearchPlacesState> = _uiState.asStateFlow()
    private val placesClient = Places.createClient(context)

    private val _placesPredictions = MutableSharedFlow<List<PredictionUI>>()
    val placesPredictions = _placesPredictions.asSharedFlow()
    private val _selectedPlaceLatLng = MutableSharedFlow<LatLng>()
    val selectedPlaceLatLng = _selectedPlaceLatLng.asSharedFlow()
    @StringRes private val _errorState = MutableSharedFlow<Int>()
    val errorState = _errorState.asSharedFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    fun handleInputText(searchText: String) {
        viewModelScope.launch {
            _searchText.emit(searchText)
            getPredictions(searchText)
        }
    }

    private fun getPredictions(constraint: CharSequence) {
        CoroutineScope(Dispatchers.IO).launch {
            val resultList = mutableListOf<PredictionUI>()
            val token = AutocompleteSessionToken.newInstance()

            val request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setQuery(constraint.toString())
                .build()
            val autocompletePredictions = placesClient.findAutocompletePredictions(request)

            try {
                Tasks.await(
                    autocompletePredictions,
                    20,
                    TimeUnit.SECONDS
                )
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: TimeoutException) {
                e.printStackTrace()
            }
            if (autocompletePredictions.isSuccessful) {
                val findAutocompletePredictionsResponse = autocompletePredictions.result
                if (findAutocompletePredictionsResponse != null)
                    for (prediction in findAutocompletePredictionsResponse.autocompletePredictions) {
                        resultList.add(
                            PredictionUI(
                                name = prediction.getFullText(StyleSpan(Typeface.BOLD)).toString(),
                                id = prediction.placeId,
                            )
                        )
                    }
                _placesPredictions.emit(resultList)
            }
        }
    }

    fun getPlaceByID(placeId: String) {
        val placeFields = listOf(Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                viewModelScope.launch {
                    val place = response.place
                    if (place.latLng != null) _selectedPlaceLatLng.emit(place.latLng!!)
                    else _errorState.emit(R.string.oops_something_went_wrong)
                }
            }.addOnFailureListener { exception: Exception ->
                if (exception is ApiException) {
                    viewModelScope.launch {
                        _errorState.emit(R.string.place_not_found)
                    }
                }
            }
    }

}