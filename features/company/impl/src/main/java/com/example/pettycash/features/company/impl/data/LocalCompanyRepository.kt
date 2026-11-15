package com.example.pettycash.features.company.impl.data

import com.example.pettycash.datasource.local.CompanyLocalDataSource
import kotlinx.serialization.json.Json

internal class LocalCompanyRepository(
    private val local: CompanyLocalDataSource,
) : CompanyRepository {

    override suspend fun completeOnboarding(profile: CompanyProfile) {
        local.saveProfileJson(Json.encodeToString(CompanyProfile.serializer(), profile))
        local.markSetupComplete()
    }

    override suspend fun isSetupComplete(): Boolean = local.setupCompleteValue()
}
