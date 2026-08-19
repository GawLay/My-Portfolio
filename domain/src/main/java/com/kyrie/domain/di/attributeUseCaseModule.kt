package com.kyrie.domain.di

import com.kyrie.domain.AttributeUseCase
import org.koin.dsl.module

val attributeUseCaseModule =
    module {
        single { AttributeUseCase(get()) }
    }
