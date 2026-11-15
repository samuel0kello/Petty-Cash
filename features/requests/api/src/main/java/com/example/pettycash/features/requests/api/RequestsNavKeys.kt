package com.example.pettycash.features.requests.api

import androidx.navigation3.runtime.NavKey

data object RequestCashNavKey : NavKey
data class RequestDetailNavKey(val requestId: String) : NavKey
data object ApprovalsNavKey : NavKey
data class ApprovalDetailNavKey(val requestId: String) : NavKey
