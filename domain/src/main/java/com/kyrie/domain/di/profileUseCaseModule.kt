package com.kyrie.domain.di

import com.kyrie.data.repository.ProfileRepositoryImpl
import com.kyrie.domain.ProfileUseCase
import org.koin.dsl.module

val profileUseCaseModule = module {
    single { ProfileUseCase(get()) }
}