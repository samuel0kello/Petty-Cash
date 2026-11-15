package com.example.pettycash.features.home.impl.data

import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    val role: Flow<HomeRole>
    suspend fun dashboard(role: HomeRole): HomeDashboard
    suspend fun selectRole(role: HomeRole)
    suspend fun dismissFirstVisit()
}
