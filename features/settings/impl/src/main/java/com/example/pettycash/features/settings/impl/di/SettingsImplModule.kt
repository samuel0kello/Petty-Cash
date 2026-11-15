package com.example.pettycash.features.settings.impl.di

import com.example.pettycash.datasource.local.ActivityLocalDataSource
import com.example.pettycash.datasource.local.AppSettingsLocalDataSource
import com.example.pettycash.datasource.local.AuthLocalDataSource
import com.example.pettycash.datasource.local.CompanyLocalDataSource
import com.example.pettycash.datasource.local.HomeLocalDataSource
import com.example.pettycash.datasource.local.InviteLocalDataSource
import com.example.pettycash.features.home.api.HomeMoreNavKey
import com.example.pettycash.features.home.api.HomeProfileNavKey
import com.example.pettycash.features.invite.api.TeamNavKey
import com.example.pettycash.features.onboarding.api.OnboardingWelcomeNavKey
import com.example.pettycash.features.settings.api.SettingsAppearanceNavKey
import com.example.pettycash.features.settings.api.SettingsCompanyNavKey
import com.example.pettycash.features.settings.api.SettingsLimitsNavKey
import com.example.pettycash.features.settings.api.SettingsMpesaNavKey
import com.example.pettycash.features.settings.api.SettingsPersonNavKey
import com.example.pettycash.features.settings.api.SettingsProjectsNavKey
import com.example.pettycash.features.settings.api.SettingsSecurityNavKey
import com.example.pettycash.features.settings.api.SettingsVouchersNavKey
import com.example.pettycash.features.settings.impl.data.LocalSettingsRepository
import com.example.pettycash.features.settings.impl.data.SettingsRepository
import com.example.pettycash.features.settings.impl.ui.settings.AppearanceRoute
import com.example.pettycash.features.settings.impl.ui.settings.CompanyFormContent
import com.example.pettycash.features.settings.impl.ui.settings.CompanyFormRoute
import com.example.pettycash.features.settings.impl.ui.settings.CompanyFormViewModel
import com.example.pettycash.features.settings.impl.ui.settings.LimitsFormContent
import com.example.pettycash.features.settings.impl.ui.settings.MoreRoute
import com.example.pettycash.features.settings.impl.ui.settings.MpesaFormContent
import com.example.pettycash.features.settings.impl.ui.settings.PersonRoute
import com.example.pettycash.features.settings.impl.ui.settings.ProfileRoute
import com.example.pettycash.features.settings.impl.ui.settings.ProfileViewModel
import com.example.pettycash.features.settings.impl.ui.settings.ProjectsFormContent
import com.example.pettycash.features.settings.impl.ui.settings.SecurityRoute
import com.example.pettycash.features.settings.impl.ui.settings.SecurityViewModel
import com.example.pettycash.features.settings.impl.ui.settings.AppearanceViewModel
import com.example.pettycash.features.settings.impl.ui.settings.VouchersFormContent
import com.example.pettycash.presentation.LocalNavigator
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val SettingsImplModule = module {
    single { AuthLocalDataSource(androidContext()) }
    single { AppSettingsLocalDataSource(androidContext()) }
    single { HomeLocalDataSource(androidContext()) }
    single { ActivityLocalDataSource(androidContext()) }
    single<SettingsRepository> {
        LocalSettingsRepository(
            get(),
            get<AuthLocalDataSource>(),
            get<InviteLocalDataSource>(),
            get<HomeLocalDataSource>(),
            get<AppSettingsLocalDataSource>(),
            Json { ignoreUnknownKeys = true },
        )
    }

    viewModelOf(::ProfileViewModel)
    viewModelOf(::AppearanceViewModel)
    viewModelOf(::SecurityViewModel)
    viewModelOf(::CompanyFormViewModel)

    navigation<HomeProfileNavKey> {
        val navigator = LocalNavigator.current
        ProfileRoute(
            onSignedOut = { navigator.goTo(OnboardingWelcomeNavKey) },
            onAppearance = { navigator.goTo(SettingsAppearanceNavKey) },
            onSecurity = { navigator.goTo(SettingsSecurityNavKey) },
        )
    }

    navigation<HomeMoreNavKey> {
        val navigator = LocalNavigator.current
        MoreRoute(
            onCompany = { navigator.goTo(SettingsCompanyNavKey) },
            onTeam = { navigator.goTo(TeamNavKey) },
            onLimits = { navigator.goTo(SettingsLimitsNavKey) },
            onProjects = { navigator.goTo(SettingsProjectsNavKey) },
            onVouchers = { navigator.goTo(SettingsVouchersNavKey) },
            onMpesa = { navigator.goTo(SettingsMpesaNavKey) },
            onAppearance = { navigator.goTo(SettingsAppearanceNavKey) },
            onSecurity = { navigator.goTo(SettingsSecurityNavKey) },
            repository = get(),
        )
    }

    navigation<SettingsAppearanceNavKey> { AppearanceRoute() }
    navigation<SettingsSecurityNavKey> { SecurityRoute() }

    navigation<SettingsLimitsNavKey> {
        CompanyFormRoute(com.example.pettycash.features.settings.impl.R.string.settings_limits_title) { profile, onChange ->
            LimitsFormContent(profile, onChange)
        }
    }

    navigation<SettingsProjectsNavKey> {
        CompanyFormRoute(com.example.pettycash.features.settings.impl.R.string.settings_projects_title) { profile, onChange ->
            ProjectsFormContent(profile, onChange)
        }
    }

    navigation<SettingsVouchersNavKey> {
        CompanyFormRoute(com.example.pettycash.features.settings.impl.R.string.settings_vouchers_title) { profile, onChange ->
            VouchersFormContent(profile, onChange)
        }
    }

    navigation<SettingsMpesaNavKey> {
        CompanyFormRoute(com.example.pettycash.features.settings.impl.R.string.settings_mpesa_title) { profile, onChange ->
            MpesaFormContent(profile, onChange)
        }
    }

    navigation<SettingsCompanyNavKey> {
        CompanyFormRoute(com.example.pettycash.features.settings.impl.R.string.settings_company_title) { profile, onChange ->
            CompanyFormContent(profile, onChange)
        }
    }

    navigation<SettingsPersonNavKey> { key ->
        val navigator = LocalNavigator.current
        PersonRoute(
            name = key.name,
            repository = get(),
            onRemoved = { navigator.goBack() },
        )
    }
}
