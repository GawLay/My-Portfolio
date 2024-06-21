package com.kyrie.domain.di

import com.kyrie.domain.TemplateUseCase
import org.koin.dsl.module

val templateUseCaseModule = module {
    single { TemplateUseCase(get()) }
}