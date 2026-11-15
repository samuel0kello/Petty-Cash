package com.example.pettycash.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.pettycash.mai.theme.MaiTheme

@Composable
fun PlaceholderScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("PettyCash", style = MaiTheme.customTypography.brandDisplay, color = MaiTheme.colors.onSurface)
    }
}
