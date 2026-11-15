package com.example.pettycash.features.home.impl.data

import com.example.pettycash.datasource.local.ActivityLocalDataSource
import com.example.pettycash.datasource.local.HomeLocalDataSource
import java.util.Calendar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LocalHomeRepository(
    private val localDataSource: HomeLocalDataSource,
    private val activityLocalDataSource: ActivityLocalDataSource,
) : HomeRepository {

    override val role: Flow<HomeRole> = localDataSource.selectedRole.map { stored ->
        HomeRole.entries.firstOrNull { it.name == stored } ?: HomeRole.Admin
    }

    override val coachMarkPending: Flow<Boolean> = localDataSource.coachMarkPending

    override suspend fun dashboard(role: HomeRole): HomeDashboard {
        val firstVisit = role == HomeRole.Admin && localDataSource.firstVisitPending.first()
        return when (role) {
            HomeRole.Admin -> adminDashboard(firstVisit)
            HomeRole.Custodian -> custodianDashboard().let { dashboard ->
                if (countReminderPending()) dashboard.copy(showCountReminder = true) else dashboard
            }
            HomeRole.Member -> memberDashboard()
        }
    }

    override suspend fun selectRole(role: HomeRole) {
        localDataSource.saveSelectedRole(role.name)
    }

    override suspend fun dismissFirstVisit() {
        localDataSource.clearFirstVisitPending()
    }

    private suspend fun countReminderPending(): Boolean {
        val raw = activityLocalDataSource.reconciliationJson.first() ?: return true
        val month = java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.US).format(java.util.Date())
        return !raw.contains("\"month\":\"$month\"")
    }

    override suspend fun dismissCoachMark() {
        localDataSource.clearCoachMarkPending()
    }
}
