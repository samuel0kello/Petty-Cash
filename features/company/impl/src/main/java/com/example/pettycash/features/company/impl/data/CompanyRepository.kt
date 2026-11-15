package com.example.pettycash.features.company.impl.data

interface CompanyRepository {
    suspend fun completeOnboarding(profile: CompanyProfile)
    suspend fun isSetupComplete(): Boolean
}
