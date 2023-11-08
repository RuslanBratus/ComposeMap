package com.example.composemap.presentation.screens.tabs.settings.composable

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composemap.R
import com.example.composemap.enums.MarkersListColors
import com.example.composemap.presentation.screens.edit_marker.composable.LoadingContent
import com.example.composemap.presentation.screens.tabs.settings.SettingsState
import com.example.composemap.presentation.screens.tabs.settings.viewmodel.SettingsViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    popBackStack: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState()
    viewModel.getSelectedMarkersListColor()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) {
        Crossfade(uiState.value) { screenContent ->
            when (screenContent) {
                is SettingsState.Loading -> LoadingContent()
                is SettingsState.Default -> DefaultContent(popBackStack, viewModel = viewModel,
                    selectedColorID = screenContent.selectedColorID)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultContent(
    popBackStack: () -> Unit,
    viewModel: SettingsViewModel,
    selectedColorID: Int
) {
    val selectedColor = MarkersListColors.getObjectByID(selectedColorID)
    var isExpanded = remember {
        mutableStateOf(false)
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (backArrow, selectedColorText, selectColorMenu) = createRefs()
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
        Text(
            text =stringResource(id = R.string.select_list_text_color),
            color = colorResource(id = selectedColor.colorResource),
            fontSize = 16.sp,
            modifier = Modifier
                .constrainAs(selectedColorText) {
                    start.linkTo(parent.start, 16.dp)
                    top.linkTo(backArrow.bottom, 16.dp)
                }
        )
        ExposedDropdownMenuBox(
            expanded = isExpanded.value,
            onExpandedChange = { newValue ->
                isExpanded.value = newValue
            },
            modifier = Modifier
                .background(colorResource(id = R.color.black))
                .size(width = 100.dp, height = 48.dp)
                .constrainAs(selectColorMenu) {
                    end.linkTo(parent.end, 16.dp)
                    top.linkTo(selectedColorText.top)
                    bottom.linkTo(selectedColorText.bottom)
                }
        ) {
            TextField(
                value = "",
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded.value)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    containerColor = colorResource(id = selectedColor.colorResource)
                ),
                modifier = Modifier
                    .menuAnchor()
                    .background(colorResource(id = R.color.black))
            )
            ExposedDropdownMenu(
                expanded = isExpanded.value,
                onDismissRequest = {
                    isExpanded.value = false
                }
            ) {
                MarkersListColors.values().forEach { enumColor ->
                    DropdownMenuItem(
                        text = {
                            Text(text = "S")
                        },
                        modifier = Modifier.background(colorResource(id = enumColor.colorResource)),
                        onClick = {
                            viewModel.saveSelectedMarkersListColor(enumColor.id)
                            isExpanded.value = false
                        }
                    )
                }
            }
        }
    }
}
