package com.kyrie.domain.di

import com.kyrie.domain.FaqUseCase
import org.koin.dsl.module

val faqUseCaseModule = module {
    single { FaqUseCase(get()) }
}