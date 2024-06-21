package com.kyrie.domain.di

import org.koin.dsl.module

val useCasesModule = module {
    includes(profileUseCaseModule, skillUseCaseModule, expUseCaseModule, templateUseCaseModule,faqUseCaseModule)
}