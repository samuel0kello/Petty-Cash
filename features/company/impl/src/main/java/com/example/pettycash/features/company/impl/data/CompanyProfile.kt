package com.example.pettycash.features.company.impl.data

import kotlinx.serialization.Serializable

@Serializable
data class CompanyProfile(
    val companyName: String = "",
    val industry: String? = null,
    val teamSize: String? = null,
    val kraPin: String = "",
    val businessReg: String = "",
    val mpesaType: String = "Paybill",
    val mpesaNumber: String = "",
    val mpesaAccountName: String = "",
    val mpesaConnected: Boolean = false,
    val floatName: String = "",
    val floatStartingAmount: String = "",
    val floatCustodian: String = "",
    val floatSingleSpendLimit: String = "",
    val floatReceiptDeadline: String = "",
    val floatNoReceiptLimit: String = "",
    val approverRule: String = "Admins",
    val categories: List<String> = emptyList(),
    val projects: List<String> = emptyList(),
    val sites: List<String> = emptyList(),
    val voucherStartFresh: Boolean = true,
    val lastVoucherNumber: String = "",
)
