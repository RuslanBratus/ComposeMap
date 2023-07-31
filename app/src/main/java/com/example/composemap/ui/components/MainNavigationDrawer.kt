package com.example.composemap.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.composemap.R
import kotlinx.coroutines.launch

//@Composable
//class MainNavigationDrawer {
//}

data class DrawerItemInfo<T>(
    val drawerOption: T,
    @StringRes val title: Int,
    @DrawableRes val drawableId: Int,
    @StringRes val descriptionId: Int
)

// T for generic type to be used for the picking
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T: Enum<T>> DrawerContent(
    drawerState: DrawerState,
    menuItems: List<DrawerItemInfo<T>>,
    defaultPick: T,
    onClick: (T) -> Unit
) {
    // default home destination to avoid duplication
    var currentPick = remember { mutableStateOf(defaultPick) }
    val coroutineScope = rememberCoroutineScope()

    ModalDrawerSheet {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // header image on top of the drawer
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Main app icon",
                    modifier = Modifier.size(150.dp)
                )
                // column of options to pick from for user
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // generates on demand the required composables
                    itemsIndexed(items = menuItems) {index, item ->
                        // custom UI representation of the button
                        DrawerItem(item = item) { navOption ->
                            if (currentPick == navOption) {
                                return@DrawerItem
                            }
//                            currentPick = navOption //@TODO UNDO!

                            // close the drawer after clicking the option
                            coroutineScope.launch {
                                drawerState.close()
                            }

                            // navigate to the required screen
                            onClick(navOption)
                        }
                    }
//                    items(items = menuItems) { item ->
//                        // custom UI representation of the button
//                        DrawerItem(item = item) { navOption ->
//
//                            // if it is the same - ignore the click
//                            if (currentPick == navOption) {
//                                return@AppDrawerItem
//                            }
//
//                            currentPick = navOption
//
//                            // close the drawer after clicking the option
//                            coroutineScope.launch {
//                                drawerState.close()
//                            }
//
//                            // navigate to the required screen
//                            onClick(navOption)
//                        }
//                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DrawerItem(item: DrawerItemInfo<T>, onClick: (options: T) -> Unit) =
    // making surface clickable causes to show the appropriate splash animation
    Surface(
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
            .width(150.dp),
        onClick = { onClick(item.drawerOption) },
        shape = RoundedCornerShape(50),
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = item.drawableId),
                contentDescription = stringResource(id = item.descriptionId),
                modifier = Modifier
                    .size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(id = item.title),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }
    }