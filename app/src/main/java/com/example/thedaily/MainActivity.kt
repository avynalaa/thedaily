package com.example.thedaily

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.thedaily.ui.CharacterEditScreen
import com.example.thedaily.ui.ChatScreen
import com.example.thedaily.ui.ContactsScreen
import com.example.thedaily.ui.SettingsScreen
import com.example.thedaily.ui.HomeViewModel
import com.example.thedaily.ui.HomeScreen
import com.example.thedaily.ui.theme.TheDailyTheme
import com.example.thedaily.viewmodel.ChatViewModel
import com.example.thedaily.viewmodel.ChatViewModelFactory
import com.example.thedaily.viewmodel.SettingsViewModel
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        setContent {
            TheDailyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    com.example.thedaily.ui.AppNavHost(
                        settingsViewModel = settingsViewModel,
                        homeViewModel = homeViewModel
                    )
                }
            }
        }
    }
}
