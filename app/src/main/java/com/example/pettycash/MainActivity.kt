package com.example.pettycash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.example.pettycash.datasource.local.AppSettingsLocalDataSource
import com.example.pettycash.mai.theme.MaiTheme
import com.example.pettycash.features.onboarding.api.OnboardingSplashNavKey
import com.example.pettycash.presentation.LocalNavigator
import com.example.pettycash.presentation.Navigator
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val settings = remember { AppSettingsLocalDataSource(context) }
            val themeMode by settings.themeMode.collectAsState(initial = "system")
            val systemDark = isSystemInDarkTheme()
            MaiTheme(
                darkTheme = when (themeMode) {
                    "light" -> false
                    "dark" -> true
                    else -> systemDark
                },
            ) {
                App()
            }
        }
    }
}

@OptIn(KoinExperimentalAPI::class)
@Composable
fun App() {
    val backStack = remember { NavBackStack<NavKey>(OnboardingSplashNavKey) }
    val navigator = remember(backStack) { Navigator(backStack) }

    CompositionLocalProvider(LocalNavigator provides navigator) {
        Box(Modifier.fillMaxSize().statusBarsPadding()) {
            NavDisplay(
                backStack = backStack,
                entryProvider = koinEntryProvider(),
            )
        }
    }
}
