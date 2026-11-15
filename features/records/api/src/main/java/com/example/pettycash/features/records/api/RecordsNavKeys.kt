package com.example.pettycash.features.records.api

import androidx.navigation3.runtime.NavKey

data object NotificationsNavKey : NavKey
data class ExpenseDetailNavKey(val kind: String, val id: String) : NavKey
data class AuditTrailNavKey(val kind: String, val id: String) : NavKey
data class VoucherNavKey(val kind: String, val id: String) : NavKey
