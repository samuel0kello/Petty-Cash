package com.example.pettycash.features.invite.impl.ui.invite

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pettycash.features.invite.impl.R
import com.example.pettycash.features.invite.impl.data.InviteDraft
import com.example.pettycash.features.invite.impl.data.InviteRepository
import com.example.pettycash.mai.components.MaiButton
import com.example.pettycash.mai.components.MaiButtonType
import com.example.pettycash.mai.theme.MaiTheme
import org.koin.compose.koinInject

@Composable
internal fun InvitesSentRoute(
    onDone: () -> Unit,
) {
    val inviteRepository = koinInject<InviteRepository>()
    val draft by inviteRepository.draft.collectAsStateWithLifecycle(initialValue = InviteDraft())
    InvitesSentScreen(companyName = draft.companyName, onDone = onDone)
}

@Composable
internal fun InvitesSentScreen(
    companyName: String,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MaiTheme.spacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(36.dp),
                )
            }
            Text(
                stringResource(R.string.invite_sent_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = MaiTheme.spacing.xl),
            )
            Text(
                stringResource(R.string.invite_sent_message, companyName),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = MaiTheme.spacing.sm),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MaiTheme.spacing.section),
                verticalArrangement = Arrangement.spacedBy(MaiTheme.spacing.md),
            ) {
                MaiButton(
                    text = stringResource(R.string.invite_share_link),
                    onClick = {
                        val send = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Join $companyName on PettyCash")
                        }
                        context.startActivity(Intent.createChooser(send, null))
                    },
                    type = MaiButtonType.Outlined,
                    leadingIcon = Icons.Filled.Share,
                    modifier = Modifier.fillMaxWidth(),
                )
                MaiButton(
                    text = stringResource(R.string.invite_done),
                    onClick = onDone,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
