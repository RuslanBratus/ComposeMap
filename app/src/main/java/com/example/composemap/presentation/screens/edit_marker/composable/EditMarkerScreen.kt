package com.example.composemap.presentation.screens.edit_marker.composable

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
import com.example.composemap.presentation.screens.edit_marker.EditMarkerError
import com.example.composemap.presentation.screens.edit_marker.EditMarkerState
import com.example.composemap.presentation.screens.edit_marker.viewmodel.EditMarkerViewModel
import com.example.composemap.presentation.utils.Utils

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMarkerScreen(
    markerId: Int,
    viewModel: EditMarkerViewModel = hiltViewModel(),
    popBackStack: () -> Unit
) {
    viewModel.getMarker(context = LocalContext.current, markerId = markerId)
    val uiState = viewModel.uiState.collectAsState() //@TODO Collect with lifecycle

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) {
        Crossfade(uiState.value) { screenContent ->
            when (screenContent) {
                is EditMarkerState.Loading -> LoadingContent()
                is EditMarkerState.Success -> SuccessfullyEditedContent(popBackStack)
                is EditMarkerState.Error -> ErrorContent(popBackStack, screenContent.throwable)
                is EditMarkerState.Ready -> MainContent(viewModel, popBackStack)
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(viewModel: EditMarkerViewModel, popBackStack: () -> Unit) {
    val context = LocalContext.current
    val currentMarker = viewModel.markerEditing.collectAsState()

    val launcher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { finalUri: Uri ->
            context.getBitmapFromUri(finalUri) {
                //@TODO Handle image not found = replace or delete from db
            }?.let {
                viewModel.updateImageBitmap(it)
            }
        }
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

        Text(text = stringResource(id = R.string.editing_marker), fontSize = 20.sp, modifier = Modifier.constrainAs(title) {
            top.linkTo(parent.top, margin = 10.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        })

        OutlinedTextField(
            value = currentMarker.value.title,
            onValueChange = { if (it.length <= Utils.MARKER_TITLE_MAX_LENGTH) viewModel.updateInputTitleText(it) },
            label = { Text(text = stringResource(id = R.string.title))},
            modifier = Modifier
                .constrainAs(titleInput) {
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    top.linkTo(title.bottom, margin = 26.dp)
                    width = Dimension.fillToConstraints
                })

        OutlinedTextField(
            value = currentMarker.value.description,
            onValueChange = { if (it.length <= Utils.MARKER_DESCRIPTION_MAX_LENGTH) viewModel.updateInputDescriptionText(it) },
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
        Image(
            bitmap = currentMarker.value.imageBitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp, 80.dp)
                .constrainAs(selectedImage) {
                    end.linkTo(parent.end)
                    start.linkTo(pickImageButton.end)
                    top.linkTo(pickImageButton.top)
                    bottom.linkTo(pickImageButton.bottom)
                }
        )

        Text(text = "Latitude: ${currentMarker.value.latitude}", fontSize = 20.sp, modifier = Modifier.constrainAs(latitudeText) {
            top.linkTo(pickImageButton.bottom, margin = 18.dp)
            start.linkTo(pickImageButton.start)
        })

        Text(text = "Longitude: ${currentMarker.value.longitude}", fontSize = 20.sp, modifier = Modifier.constrainAs(longitudeText) {
            top.linkTo(latitudeText.bottom, margin = 10.dp)
            start.linkTo(latitudeText.start)
        })

        Button(
            onClick = {
                if (isDataCorrect(currentMarker.value.latitude, currentMarker.value.longitude,
                        currentMarker.value.title, currentMarker.value.description,
                        currentMarker.value.imageBitmap, context)
                ) {
                    viewModel.saveEditedMarker(context.applicationContext)
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
    context: Context
): Boolean {
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

@Composable
fun SuccessfullyEditedContent(popBackStack: () -> Unit) {
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
        Text(text = stringResource(id = R.string.marker_edited_successfully),
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
private fun ErrorContent(popBackStack: () -> Unit, throwable: Throwable) {
    val errorMessageResource =
        when (throwable) {
            is EditMarkerError.NOT_FOUNT -> R.string.marker_not_found
            else -> R.string.oops_something_went_wrong
        }
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
