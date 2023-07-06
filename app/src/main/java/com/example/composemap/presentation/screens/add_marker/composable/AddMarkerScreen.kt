package com.example.composemap.presentation.screens.add_marker.composable

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.composemap.R
import com.example.composemap.extensions.getBitmapFromUri
import com.example.composemap.presentation.screens.add_marker.AddMarkerState
import com.example.composemap.presentation.screens.add_marker.viewmodel.AddMarkerViewModel
import com.example.composemap.presentation.screens.main.model.MarkerUI
import com.example.composemap.presentation.utils.Utils.Companion.MARKER_DESCRIPTION_MAX_LENGTH
import com.example.composemap.presentation.utils.Utils.Companion.MARKER_TITLE_MAX_LENGTH

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMarkerScreen(
    viewModel: AddMarkerViewModel = hiltViewModel(),
    latitude: Double,
    longitude: Double,
    popBackStack: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState() //@TODO Collect with lifecycle

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) {
        Crossfade(uiState.value) { screenContent ->
            when (screenContent) {
                is AddMarkerState.Loading -> LoadingContent()
                is AddMarkerState.Success -> SavingSuccessContent(popBackStack)
                is AddMarkerState.Error -> ErrorContent(popBackStack)
                is AddMarkerState.Ready -> MainContent(viewModel, latitude, longitude, popBackStack)
            }
        }
    }

}

@Composable
private fun SavingSuccessContent(popBackStack: () -> Unit) {
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
        Text(text = stringResource(id = R.string.marker_aded_successfully),
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
private fun ErrorContent(popBackStack: () -> Unit) {
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
        Text(text = stringResource(id = R.string.oops_something_went_wrong),
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
private fun LoadingContent() {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.loading_animation))
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    viewModel: AddMarkerViewModel,
    latitude: Double,
    longitude: Double,
    popBackStack: () -> Unit
) {
    val inputTitleText = viewModel.inputTitleText.collectAsState()
    val inputDescriptionText = viewModel.inputDescriptionText.collectAsState()
    val context = LocalContext.current
    val imageUri = viewModel.imageUri.collectAsState()
    val bitmap = viewModel.bitmap.collectAsState()

    val launcher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) {
            uri: Uri? -> uri?.let { viewModel.updateImageUri(it) }
    }
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {

        val (title, titleInput, descriptionInput, pickImageButton, selectedImage,
            latitudeText, longitudeText, addButton, backArrow) = createRefs()

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
                .clickable { popBackStack() })

        Text(text = stringResource(id = R.string.adding_marker), fontSize = 20.sp, modifier = Modifier.constrainAs(title) {
            top.linkTo(parent.top, margin = 10.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        })

        OutlinedTextField(
            value = inputTitleText.value,
            onValueChange = { if (it.length <= MARKER_TITLE_MAX_LENGTH) viewModel.updateInputTitleText(it) },
            label = { Text(text = stringResource(id = R.string.title))},
            modifier = Modifier
                .constrainAs(titleInput) {
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    top.linkTo(title.bottom, margin = 26.dp)
                    width = Dimension.fillToConstraints
                })

        OutlinedTextField(
            value = inputDescriptionText.value,
            onValueChange = { if (it.length <= MARKER_DESCRIPTION_MAX_LENGTH) viewModel.updateInputDescriptionText(it) },
            label = { Text(text = stringResource(id = R.string.description))},
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(descriptionInput) {
                    start.linkTo(titleInput.start)
                    end.linkTo(titleInput.end)
                    top.linkTo(titleInput.bottom, margin = 20.dp)
                    width = Dimension.fillToConstraints
                })

        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier
                .size(200.dp, 40.dp)
                .constrainAs(pickImageButton) {
                    start.linkTo(descriptionInput.start)
                    top.linkTo(descriptionInput.bottom, margin = 20.dp)
                }) {
            Text(text = stringResource(id = R.string.pick_image))
        }
        imageUri.value?.let { uri ->
            context.getBitmapFromUri(uri) {
                //@TODO handle exception, file not found = ask user to remove it from DB
            }?.let {bitmap ->
                viewModel.updateBitmap(bitmap)
            }
        }
        bitmap.value?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp, 80.dp)
                    .constrainAs(selectedImage) {
                        end.linkTo(parent.end)
                        start.linkTo(pickImageButton.end)
                        top.linkTo(pickImageButton.top)
                        bottom.linkTo(pickImageButton.bottom)
                    })
        }

        Text(text = "Latitude: $latitude", fontSize = 20.sp, modifier = Modifier.constrainAs(latitudeText) {
            top.linkTo(pickImageButton.bottom, margin = 18.dp)
            start.linkTo(pickImageButton.start)
        })

        Text(text = "Longitude: $longitude", fontSize = 20.sp, modifier = Modifier.constrainAs(longitudeText) {
            top.linkTo(latitudeText.bottom, margin = 10.dp)
            start.linkTo(latitudeText.start)
        })

        Button(
            onClick = {
                if (isDataCorrect(latitude, longitude, inputTitleText.value,
                        inputDescriptionText.value, bitmap.value, context)) {
                    viewModel.addMarker(MarkerUI(id = 0, latitude = latitude, longitude = longitude,
                        title = inputTitleText.value, description = inputDescriptionText.value,
                        imageBitmap = bitmap.value!!), context.applicationContext)
                }
            },
            modifier = Modifier
                .height(60.dp)
                .constrainAs(addButton) {
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    width = Dimension.fillToConstraints
                }) {
            Text(text = stringResource(id = R.string.add_image))
        }


    }
}

private fun isDataCorrect(
    latitude: Double,
    longitude: Double,
    title: String,
    description: String,
    imageBitmap: Bitmap?,
    context: Context): Boolean {
    if (title.isEmpty()) {
        Toast.makeText(context, "Title can not be empty", Toast.LENGTH_SHORT).show()
        //@TODO implement flow in viewModel to catch events and show them
        return false
    }
    if (description.isEmpty()) {
        Toast.makeText(context, "Description can not be empty", Toast.LENGTH_SHORT).show()
        //@TODO implement flow in viewModel to catch events and show them
        return false
    }
    if (imageBitmap == null) {
        Toast.makeText(context, "Please, select image", Toast.LENGTH_SHORT).show()
        //@TODO implement flow in viewModel to catch events and show them
        return false
    }
    return true


}

@Preview
@Composable
private fun OverviewCardPreview(
) {
    AddMarkerScreen(
        latitude = 10.0,
        longitude = 20.0,
        popBackStack = {}
    )
}