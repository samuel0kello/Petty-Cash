package com.example.pettycash.presentation

import androidx.compose.runtime.staticCompositionLocalOf

val LocalNavigator = staticCompositionLocalOf<Navigator> {
    error("No Navigator provided. Wrap content in MaiTheme/App content that provides LocalNavigator.")
}
