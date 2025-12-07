package com.kyrie.domain.di

import com.kyrie.domain.SkillUseCase
import org.koin.dsl.module

val skillUseCaseModule =
    module {
        single { SkillUseCase(get()) }
    }
