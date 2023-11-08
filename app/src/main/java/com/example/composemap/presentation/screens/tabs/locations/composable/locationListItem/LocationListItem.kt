package com.example.composemap.presentation.screens.tabs.locations.composable.locationListItem

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composemap.R
import com.example.composemap.domain.model.Marker

@Composable
fun LocationListItemHorizontal(
    marker: Marker,
    textColor: Color,
    onClickNavigator: OnItemClickNavigator
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.height(115.dp)
            .padding(start = 20.dp)
            .clickable { onClickNavigator.editItem(marker.id) }
    ) {
        Image(
            bitmap = BitmapFactory.decodeFile(marker.imagePath).asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(
                    width = 100.dp,
                    height = 100.dp)

        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = marker.title,
                fontSize = 24.sp,
                color = textColor
            )
            Image(
                painter = painterResource(id = R.drawable.ic_show_on_map),
                contentDescription = null,
                modifier = Modifier
                    .size(
                        width = 48.dp,
                        height = 48.dp)
                    .clickable { onClickNavigator.navigateToItem(markerId = marker.id) }
            )
            Image(
                painter = painterResource(id = R.drawable.ic_route_to_marker),
                contentDescription = null,
                modifier = Modifier
                    .size(
                        width = 48.dp,
                        height = 48.dp)
                    .clickable { onClickNavigator.buildRouteToItem(markerId = marker.id) }
            )
        }
    }
}

@Composable
fun LocationListItemVertical(
    marker: Marker,
    textColor: Color,
    onClickNavigator: OnItemClickNavigator
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
            .clickable { onClickNavigator.editItem(marker.id) }
    ) {
        Image(
            bitmap = BitmapFactory.decodeFile(marker.imagePath).asImageBitmap(),
            contentDescription = marker.description,
            modifier = Modifier
                .width(LocalConfiguration.current.screenWidthDp.dp - 100.dp)
                .height(LocalConfiguration.current.screenWidthDp.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = marker.title,
            fontSize = 24.sp,
            color = textColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = marker.description,
            fontSize = 20.sp,
            color = textColor
        )
        Spacer(modifier = Modifier.height(38.dp))

    }
}

interface OnItemClickNavigator {
    fun editItem(markerId: Int)
    fun navigateToItem(markerId: Int)
    fun buildRouteToItem(markerId: Int)
}