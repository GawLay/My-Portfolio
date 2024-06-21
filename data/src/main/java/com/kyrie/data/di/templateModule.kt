package com.kyrie.data.di

import com.kyrie.data.repository.template.TemplateRepository
import com.kyrie.data.repository.template.TemplateRepositoryImpl
import org.koin.dsl.module

val templateModule = module {
    single<TemplateRepository> { TemplateRepositoryImpl(get()) }
}