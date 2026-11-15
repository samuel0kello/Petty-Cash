package com.example.pettycash.features.auth.impl.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.pettycash.features.auth.impl.R
import com.example.pettycash.mai.components.MaiPinPad
import com.example.pettycash.mai.components.MaiPinPadState
import com.example.pettycash.mai.components.MaiTopAppBar

@Composable
internal fun AuthTopBar(onBack: () -> Unit, title: String) {
    MaiTopAppBar(title = title, onNavigationClick = onBack)
}

@Composable
internal fun PinEntryScaffold(
    title: String,
    message: String?,
    entered: Int,
    pinState: MaiPinPadState,
    onBack: () -> Unit,
    onDigit: (Int) -> Unit,
    onDelete: () -> Unit,
    onBiometric: (() -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(Modifier.fillMaxSize()) {
            AuthTopBar(onBack = onBack, title = stringResource(R.string.auth_pin_title))
            Spacer(Modifier.weight(1f))
            MaiPinPad(
                entered = entered,
                state = pinState,
                title = title,
                message = message,
                onDigit = onDigit,
                onDelete = onDelete,
                onBiometric = onBiometric,
            )
            footer?.invoke()
        }
    }
}
