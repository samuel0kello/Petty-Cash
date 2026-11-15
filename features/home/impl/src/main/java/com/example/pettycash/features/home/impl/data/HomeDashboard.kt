package com.example.pettycash.features.home.impl.data

import com.example.pettycash.mai.components.MaiFloatState
import com.example.pettycash.mai.components.MaiStatus

enum class HomeRole { Admin, Custodian, Member }

enum class HomeTab { Home, Approvals, Float, People, More, Requests, History, Profile }

data class HomeFloatAccount(
    val name: String,
    val balanceKes: Long,
    val issuedKes: Long,
    val spentKes: Long,
    val state: MaiFloatState,
)

data class HomeRow(
    val title: String,
    val meta: String,
    val amountKes: Long,
    val incoming: Boolean,
    val status: MaiStatus,
    val showSign: Boolean = true,
)

data class HomeInfoCard(
    val title: String,
    val body: String,
)

data class HomeSetupStep(
    val label: String,
    val done: Boolean,
    val actionable: Boolean = false,
)

data class HomeDashboard(
    val userName: String,
    val companyName: String,
    val role: HomeRole,
    val switchableRoles: List<HomeRole>,
    val float: HomeFloatAccount? = null,
    val showTopUp: Boolean = false,
    val banner: String? = null,
    val setupTitle: String? = null,
    val setupDone: Int = 0,
    val setupTotal: Int = 0,
    val setupSteps: List<HomeSetupStep> = emptyList(),
    val approvalCard: HomeInfoCard? = null,
    val unaccountedCard: HomeInfoCard? = null,
    val showCountReminder: Boolean = false,
    val receiptsCard: HomeInfoCard? = null,
    val sectionTitle: String? = null,
    val rows: List<HomeRow> = emptyList(),
    val tabs: List<HomeTab> = emptyList(),
    val fabLabel: String? = null,
)
