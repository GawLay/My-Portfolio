package com.kyrie.data.di

import com.kyrie.data.repository.faq.FaqRepository
import com.kyrie.data.repository.faq.FaqRepositoryImpl
import org.koin.dsl.module

val faqRepoModule = module {
    single<FaqRepository> { FaqRepositoryImpl() }
}