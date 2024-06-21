package com.kyrie.data.di

import com.kyrie.data.repository.ProfileRepository
import com.kyrie.data.repository.ProfileRepositoryImpl
import org.koin.dsl.module

val profileRepoModule = module {
    single<ProfileRepository> { ProfileRepositoryImpl() }
}