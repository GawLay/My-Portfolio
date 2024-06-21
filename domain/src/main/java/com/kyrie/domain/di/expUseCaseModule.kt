package com.kyrie.domain.di

import com.kyrie.domain.ExpUseCase
import org.koin.dsl.module

val expUseCaseModule = module {
    single { ExpUseCase(get()) }
}