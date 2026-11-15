package com.example.pettycash.features.refunds.impl.di

import com.example.pettycash.datasource.local.ActivityLocalDataSource
import com.example.pettycash.datasource.local.CompanyLocalDataSource
import com.example.pettycash.datasource.local.InviteLocalDataSource
import com.example.pettycash.features.refunds.api.ClaimRefundNavKey
import com.example.pettycash.features.refunds.impl.data.LocalRefundsRepository
import com.example.pettycash.features.refunds.impl.data.RefundsRepository
import com.example.pettycash.features.refunds.impl.ui.claim.ClaimRefundRoute
import com.example.pettycash.features.refunds.impl.ui.claim.ClaimRefundViewModel
import com.example.pettycash.presentation.LocalNavigator
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val RefundsImplModule = module {
    single { ActivityLocalDataSource(androidContext()) }
    single<RefundsRepository> {
        LocalRefundsRepository(get(), get<CompanyLocalDataSource>(), get<InviteLocalDataSource>(), Json { ignoreUnknownKeys = true })
    }

    viewModelOf(::ClaimRefundViewModel)

    navigation<ClaimRefundNavKey> {
        val navigator = LocalNavigator.current
        ClaimRefundRoute(
            onClaimRefundEvent = {
                navigator.goBack()
            },
        )
    }
}
