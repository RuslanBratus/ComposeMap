package com.example.composemap.presentation.context

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.composemap.R
import com.example.composemap.ui.navigation.AppNavHost
import com.example.composemap.ui.theme.AppTheme
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPlacesAPI()
        setContent {
//            ComposeMapTheme {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    private fun initPlacesAPI() {
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.MAPS_API_KEY), Locale.US);
        }
    }
}