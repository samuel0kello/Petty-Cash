package com.example.pettycash.features.invite.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import com.example.pettycash.features.invite.impl.data.InvitePreviewStatus
import com.example.pettycash.features.invite.impl.data.PendingInvite
import com.example.pettycash.features.invite.impl.data.TeamMember
import com.example.pettycash.features.invite.impl.data.TeamSnapshot
import com.example.pettycash.features.invite.impl.ui.invite.InviteFormScreen
import com.example.pettycash.features.invite.impl.ui.invite.InviteFormState
import com.example.pettycash.features.invite.impl.ui.invite.InvitesSentScreen
import com.example.pettycash.features.invite.impl.ui.join.JoinDataScreen
import com.example.pettycash.features.invite.impl.ui.join.JoinDataState
import com.example.pettycash.features.invite.impl.ui.join.JoinDetailsScreen
import com.example.pettycash.features.invite.impl.ui.join.JoinDetailsState
import com.example.pettycash.features.invite.impl.ui.preview.InvitePreviewScreen
import com.example.pettycash.features.invite.impl.ui.preview.InvitePreviewState
import com.example.pettycash.features.invite.impl.ui.team.TeamScreen
import com.example.pettycash.features.invite.impl.ui.team.TeamState
import com.example.pettycash.mai.theme.MaiTheme

@Composable
private fun PreviewDevice(content: @Composable () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(24.dp))
        Box(Modifier.weight(1f)) { content() }
    }
}

private val previewMembers = listOf(
    TeamMember("m1", "Amara Okello", "Admin", isSelf = true),
    TeamMember("m2", "Grace Wanjiku", "Approver"),
    TeamMember("m3", "Brian Otieno", "Member"),
)

private val previewInvites = listOf(
    PendingInvite("inv-1", "Kevin Mwangi", "+254 722 111 222", "Member", invitedBy = "Amara"),
    PendingInvite("inv-2", "Susan Achieng", "+254 733 444 555", "Custodian", invitedBy = "Amara"),
)

private val previewInvite = PendingInvite(
    "inv-1", "Kevin Mwangi", "+254 722 111 222", "Member", invitedBy = "Amara",
)

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Team")
@Composable
fun TeamScreenshot() {
    MaiTheme {
        PreviewDevice {
            TeamScreen(
                state = TeamState(
                    companyName = "Savanna Solar Ltd",
                    members = previewMembers,
                    invites = previewInvites,
                    loaded = true,
                ),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Team empty")
@Composable
fun TeamEmptyScreenshot() {
    MaiTheme {
        PreviewDevice {
            TeamScreen(
                state = TeamState(
                    companyName = "Savanna Solar Ltd",
                    members = listOf(previewMembers.first()),
                    invites = emptyList(),
                    loaded = true,
                ),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Invite people")
@Composable
fun InviteFormScreenshot() {
    MaiTheme {
        PreviewDevice {
            InviteFormScreen(
                state = InviteFormState(name = "Kevin Mwangi", phone = "722 111 222"),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Invites sent")
@Composable
fun InvitesSentScreenshot() {
    MaiTheme {
        PreviewDevice {
            InvitesSentScreen(companyName = "Savanna Solar Ltd", onDone = {})
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Invite preview")
@Composable
fun InvitePreviewScreenshot() {
    MaiTheme {
        PreviewDevice {
            InvitePreviewScreen(
                state = InvitePreviewState(
                    invite = previewInvite,
                    companyName = "Savanna Solar Ltd",
                    status = InvitePreviewStatus.Valid,
                    loaded = true,
                ),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Invite expired")
@Composable
fun InviteExpiredScreenshot() {
    MaiTheme {
        PreviewDevice {
            InvitePreviewScreen(
                state = InvitePreviewState(
                    invite = previewInvite,
                    companyName = "Savanna Solar Ltd",
                    status = InvitePreviewStatus.Expired,
                    loaded = true,
                ),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Invite wrong number")
@Composable
fun InviteWrongNumberScreenshot() {
    MaiTheme {
        PreviewDevice {
            InvitePreviewScreen(
                state = InvitePreviewState(
                    invite = previewInvite,
                    companyName = "Savanna Solar Ltd",
                    status = InvitePreviewStatus.WrongNumber,
                    loaded = true,
                ),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Your details")
@Composable
fun JoinDetailsScreenshot() {
    MaiTheme {
        PreviewDevice {
            JoinDetailsScreen(
                state = JoinDetailsState(
                    name = "Kevin Mwangi",
                    phone = "+254 722 111 222",
                    companyName = "Savanna Solar Ltd",
                    loaded = true,
                ),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "Your data")
@Composable
fun JoinDataScreenshot() {
    MaiTheme {
        PreviewDevice {
            JoinDataScreen(
                state = JoinDataState(
                    companyName = "Savanna Solar Ltd",
                    consent = true,
                    loaded = true,
                ),
                onAction = {},
            )
        }
    }
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, heightDp = 800, name = "How it works")
@Composable
fun HowItWorksScreenshot() {
    MaiTheme {
        PreviewDevice {
            com.example.pettycash.features.invite.impl.ui.join.HowItWorksScreen(
                state = com.example.pettycash.features.invite.impl.ui.join.HowItWorksState(),
                onAction = {},
            )
        }
    }
}
