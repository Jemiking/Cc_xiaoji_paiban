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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.cc_xiaoji.presentation.navigation.ScheduleNavHost
import com.example.cc_xiaoji.presentation.theme.ScheduleTheme
import com.example.cc_xiaoji.presentation.theme.ThemeManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * 应用主Activity
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var themeManager: ThemeManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")
        setContent {
            Log.d("MainActivity", "setContent called")
            
            // 收集主题状态
            val isDarkMode by themeManager.isDarkMode.collectAsState(initial = false)
            
            ScheduleTheme(darkTheme = isDarkMode) {
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