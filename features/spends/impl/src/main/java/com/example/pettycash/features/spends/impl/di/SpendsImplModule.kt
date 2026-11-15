package com.example.pettycash.features.spends.impl.di

import com.example.pettycash.datasource.local.ActivityLocalDataSource
import com.example.pettycash.datasource.local.CompanyLocalDataSource
import com.example.pettycash.datasource.local.InviteLocalDataSource
import com.example.pettycash.features.spends.api.RecordSpendNavKey
import com.example.pettycash.features.spends.impl.data.LocalSpendsRepository
import com.example.pettycash.features.spends.impl.data.SpendsRepository
import com.example.pettycash.features.spends.impl.ui.record.RecordSpendRoute
import com.example.pettycash.features.spends.impl.ui.record.RecordSpendViewModel
import com.example.pettycash.presentation.LocalNavigator
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val SpendsImplModule = module {
    single { ActivityLocalDataSource(androidContext()) }
    single<SpendsRepository> {
        LocalSpendsRepository(get(), get<CompanyLocalDataSource>(), get<InviteLocalDataSource>(), Json { ignoreUnknownKeys = true })
    }

    viewModelOf(::RecordSpendViewModel)

    navigation<RecordSpendNavKey> {
        val navigator = LocalNavigator.current
        RecordSpendRoute(
            onRecordSpendEvent = {
                navigator.goBack()
            },
        )
    }
}
