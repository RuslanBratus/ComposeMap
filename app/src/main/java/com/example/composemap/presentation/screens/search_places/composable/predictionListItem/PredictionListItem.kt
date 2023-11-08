package com.example.composemap.presentation.screens.search_places.composable.predictionListItem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composemap.R
import com.example.composemap.presentation.screens.search_places.model.PredictionUI

@Composable
fun PredictionListItem(
    prediction: PredictionUI,
    onPredictionClickItem: OnPredictionClickItem
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .clickable { onPredictionClickItem.onClick(prediction.id) }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = prediction.name,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(end = 20.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Divider(
                color = Color(R.color.black),
                thickness = 0.5.dp,
                modifier = Modifier
                    .padding(end = 20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

interface OnPredictionClickItem {
    fun onClick(id: String)
}