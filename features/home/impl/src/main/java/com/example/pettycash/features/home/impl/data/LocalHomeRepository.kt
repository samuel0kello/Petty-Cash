package com.example.pettycash.features.home.impl.data

import com.example.pettycash.datasource.local.HomeLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LocalHomeRepository(
    private val localDataSource: HomeLocalDataSource,
) : HomeRepository {

    override val role: Flow<HomeRole> = localDataSource.selectedRole.map { stored ->
        HomeRole.entries.firstOrNull { it.name == stored } ?: HomeRole.Admin
    }

    override val coachMarkPending: Flow<Boolean> = localDataSource.coachMarkPending

    override suspend fun dashboard(role: HomeRole): HomeDashboard {
        val firstVisit = role == HomeRole.Admin && localDataSource.firstVisitPending.first()
        return when (role) {
            HomeRole.Admin -> adminDashboard(firstVisit)
            HomeRole.Custodian -> custodianDashboard()
            HomeRole.Member -> memberDashboard()
        }
    }

    override suspend fun selectRole(role: HomeRole) {
        localDataSource.saveSelectedRole(role.name)
    }

    override suspend fun dismissFirstVisit() {
        localDataSource.clearFirstVisitPending()
    }

    override suspend fun dismissCoachMark() {
        localDataSource.clearCoachMarkPending()
    }
}
