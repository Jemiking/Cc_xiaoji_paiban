package com.example.cc_xiaoji

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.cc_xiaoji.presentation.navigation.ScheduleNavHost
import com.example.cc_xiaoji.presentation.theme.ScheduleTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * 应用主Activity
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")
        setContent {
            Log.d("MainActivity", "setContent called")
            ScheduleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScheduleApp()
                }
            }
        }
    }
}

@Composable
fun ScheduleApp() {
    Log.d("ScheduleApp", "ScheduleApp Composable called")
    
    val navController = rememberNavController()
    ScheduleNavHost(navController = navController)
}