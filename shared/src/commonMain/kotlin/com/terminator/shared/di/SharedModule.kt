package com.terminator.shared.di

import com.terminator.shared.network.api.AuthApi
import com.terminator.shared.network.api.CredentialApi
import com.terminator.shared.network.api.FamilyApi
import com.terminator.shared.network.api.NotificationApi
import com.terminator.shared.network.api.ReportApi
import com.terminator.shared.network.api.RiskApi
import com.terminator.shared.network.api.TaskApi
import com.terminator.shared.network.api.WarningApi
import com.terminator.shared.repository.BatchExecutionRepository
import com.terminator.shared.repository.CredentialRepository
import com.terminator.shared.repository.NotificationRepository
import com.terminator.shared.repository.ReportRepository
import com.terminator.shared.repository.TaskRepository
import com.terminator.shared.repository.TemplateLibraryRepository
import com.terminator.shared.repository.WarningRepository
import com.terminator.shared.storage.CredentialStorage
import com.terminator.shared.storage.TokenStorage
import com.terminator.shared.network.createHttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sharedModule = module {
    single { TokenStorage(get()) }
    single { CredentialStorage(get()) }

    single {
        createHttpClient {
            get<TokenStorage>().token
        }
    }

    singleOf(::AuthApi)
    singleOf(::TaskApi)
    singleOf(::RiskApi)
    singleOf(::FamilyApi)
    singleOf(::WarningApi)
    singleOf(::NotificationApi)
    singleOf(::ReportApi)
    singleOf(::CredentialApi)

    singleOf(::WarningRepository)
    singleOf(::NotificationRepository)
    singleOf(::ReportRepository)
    singleOf(::TaskRepository)
    singleOf(::CredentialRepository)
    singleOf(::TemplateLibraryRepository)
    singleOf(::BatchExecutionRepository)
}
