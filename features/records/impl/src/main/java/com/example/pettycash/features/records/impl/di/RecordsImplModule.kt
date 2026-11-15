package com.example.pettycash.features.records.impl.di

import com.example.pettycash.datasource.local.ActivityLocalDataSource
import com.example.pettycash.datasource.local.CompanyLocalDataSource
import com.example.pettycash.datasource.local.RequestLocalDataSource
import com.example.pettycash.features.home.api.HomeActivityNavKey
import com.example.pettycash.features.home.api.HomeHistoryNavKey
import com.example.pettycash.features.home.api.HomeRequestsNavKey
import com.example.pettycash.features.records.api.AuditTrailNavKey
import com.example.pettycash.features.records.api.ExpenseDetailNavKey
import com.example.pettycash.features.records.api.NotificationsNavKey
import com.example.pettycash.features.records.api.VoucherNavKey
import com.example.pettycash.features.records.impl.data.LocalRecordsRepository
import com.example.pettycash.features.records.impl.data.RecordsRepository
import com.example.pettycash.features.records.impl.ui.records.ActivityRoute
import com.example.pettycash.features.records.impl.ui.records.MemberRequestsRoute
import com.example.pettycash.features.records.impl.ui.records.ActivityViewModel
import com.example.pettycash.features.records.impl.ui.records.AuditTrailRoute
import com.example.pettycash.features.records.impl.ui.records.ExpenseDetailRoute
import com.example.pettycash.features.records.impl.ui.records.NotificationsRoute
import com.example.pettycash.features.records.impl.ui.records.VoucherRoute
import com.example.pettycash.presentation.LocalNavigator
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val RecordsImplModule = module {
    single { ActivityLocalDataSource(androidContext()) }
    single { RequestLocalDataSource(androidContext()) }
    single<RecordsRepository> {
        LocalRecordsRepository(
            get(),
            get<RequestLocalDataSource>(),
            get<CompanyLocalDataSource>(),
            Json { ignoreUnknownKeys = true },
        )
    }

    viewModelOf(::ActivityViewModel)

    navigation<HomeHistoryNavKey> {
        val navigator = LocalNavigator.current
        ActivityRoute(
            onActivityEvent = { event ->
                when (event) {
                    is com.example.pettycash.features.records.impl.ui.records.ActivityEvent.OpenDetail ->
                        navigator.goTo(ExpenseDetailNavKey(event.kind.name, event.id))
                }
            },
        )
    }

    navigation<HomeRequestsNavKey> {
        val navigator = LocalNavigator.current
        MemberRequestsRoute(
            onOpenDetail = { kind, id -> navigator.goTo(ExpenseDetailNavKey(kind.name, id)) },
            repository = get(),
        )
    }

    navigation<HomeActivityNavKey> {
        val navigator = LocalNavigator.current
        NotificationsRoute(
            onOpenDetail = { kind, id -> navigator.goTo(ExpenseDetailNavKey(kind.name, id)) },
            repository = get(),
        )
    }

    navigation<NotificationsNavKey> {
        val navigator = LocalNavigator.current
        NotificationsRoute(
            onOpenDetail = { kind, id -> navigator.goTo(ExpenseDetailNavKey(kind.name, id)) },
            repository = get(),
        )
    }

    navigation<ExpenseDetailNavKey> { key ->
        val navigator = LocalNavigator.current
        val kind = com.example.pettycash.features.records.impl.data.ActivityKind.valueOf(key.kind)
        ExpenseDetailRoute(
            kind = kind,
            id = key.id,
            onAudit = { navigator.goTo(AuditTrailNavKey(key.kind, key.id)) },
            onVoucher = { navigator.goTo(VoucherNavKey(key.kind, key.id)) },
            repository = get(),
        )
    }

    navigation<AuditTrailNavKey> { key ->
        AuditTrailRoute(
            kind = com.example.pettycash.features.records.impl.data.ActivityKind.valueOf(key.kind),
            id = key.id,
            repository = get(),
        )
    }

    navigation<VoucherNavKey> { key ->
        VoucherRoute(
            kind = com.example.pettycash.features.records.impl.data.ActivityKind.valueOf(key.kind),
            id = key.id,
            repository = get(),
        )
    }
}
