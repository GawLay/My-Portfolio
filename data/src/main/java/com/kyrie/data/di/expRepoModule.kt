package com.kyrie.data.di

import com.kyrie.data.repository.exp.ExpRepository
import com.kyrie.data.repository.exp.ExpRepositoryImpl
import org.koin.dsl.module

val expRepoModule =
    module {
        single<ExpRepository> { ExpRepositoryImpl() }
    }
