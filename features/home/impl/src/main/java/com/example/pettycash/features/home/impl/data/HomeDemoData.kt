package com.example.pettycash.features.home.impl.data

import com.example.pettycash.mai.components.MaiFloatState
import com.example.pettycash.mai.components.MaiStatus

internal fun adminDashboard(firstVisit: Boolean): HomeDashboard {
    val float = if (firstVisit) {
        HomeFloatAccount("Kilimani site float", 48_200, 60_000, 11_800, MaiFloatState.Healthy)
    } else {
        HomeFloatAccount("Westlands office float", 18_450, 25_000, 6_550, MaiFloatState.Healthy)
    }
    val rows = if (firstVisit) {
        listOf(
            HomeRow("Fuel for generator", "Kilimani site · 28 Oct, 10:42", 4_500, incoming = false, MaiStatus.Paid),
            HomeRow("Fuel for generator", "Kilimani site · 28 Oct, 10:42", 4_500, incoming = false, MaiStatus.Pending),
        )
    } else {
        listOf(
            HomeRow("Printer paper and pens", "Westlands HQ · 3 Oct, 10:24", 1_500, incoming = false, MaiStatus.Paid),
            HomeRow("Fuel for generator", "Garissa · 2 Oct, 16:05", 4_500, incoming = false, MaiStatus.Approved),
            HomeRow("Float top-up", "Paybill 123456 · 1 Oct", 10_000, incoming = true, MaiStatus.Paid),
        )
    }
    return HomeDashboard(
        userName = "Grace",
        companyName = "Savanna Solar Ltd",
        role = HomeRole.Admin,
        switchableRoles = listOf(HomeRole.Admin, HomeRole.Custodian),
        float = float,
        showTopUp = true,
        banner = if (firstVisit) "Payouts are capped at KES 10,000 a day until your company is verified." else null,
        setupTitle = if (firstVisit) "Finish setting up" else null,
        setupDone = if (firstVisit) 3 else 0,
        setupTotal = if (firstVisit) 5 else 0,
        setupSteps = if (firstVisit) {
            listOf(
                HomeSetupStep("Company details", done = true),
                HomeSetupStep("Connect M-Pesa", done = true),
                HomeSetupStep("First float", done = true),
                HomeSetupStep("Approval rule", done = false, actionable = true),
                HomeSetupStep("Invite your team", done = false),
            )
        } else {
            emptyList()
        },
        approvalCard = if (firstVisit) null else HomeInfoCard("Needs your approval · 3", "KES 7,900 waiting for you."),
        unaccountedCard = if (firstVisit) null else HomeInfoCard("KES 1,500 not accounted for", "September. Tap to see why."),
        sectionTitle = "Recent activity",
        rows = rows,
        tabs = listOf(HomeTab.Home, HomeTab.Approvals, HomeTab.Float, HomeTab.People, HomeTab.More),
    )
}

internal fun custodianDashboard() = HomeDashboard(
    userName = "Wanjiru",
    companyName = "Savanna Solar Ltd",
    role = HomeRole.Custodian,
    switchableRoles = listOf(HomeRole.Admin, HomeRole.Custodian),
    float = HomeFloatAccount("Westlands office float", 18_450, 25_000, 6_550, MaiFloatState.Healthy),
    showTopUp = false,
    receiptsCard = HomeInfoCard("Receipts due · 2", "Add them by Fri 30 Oct."),
    sectionTitle = "Your requests",
    rows = requestRows(),
    tabs = listOf(HomeTab.Home, HomeTab.Requests, HomeTab.History, HomeTab.Profile),
    fabLabel = "Record a spend",
)

internal fun memberDashboard() = HomeDashboard(
    userName = "Brian",
    companyName = "Savanna Solar Ltd",
    role = HomeRole.Member,
    switchableRoles = listOf(HomeRole.Member),
    sectionTitle = "Your requests",
    rows = requestRows(),
    tabs = listOf(HomeTab.Home, HomeTab.Requests, HomeTab.History, HomeTab.Profile),
    fabLabel = "New",
)

private fun requestRows() = listOf(
    HomeRow("Fuel for Garissa site visit", "Cash in advance · 28 Oct", 3_200, incoming = false, MaiStatus.Pending, showSign = false),
    HomeRow("Matatu fare to Garissa site office", "Refund claim · 26 Oct", 850, incoming = false, MaiStatus.Approved, showSign = false),
)
