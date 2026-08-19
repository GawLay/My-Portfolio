package com.kyrie.data.di

import com.kyrie.data.repository.attribute.AttributeRepository
import com.kyrie.data.repository.attribute.AttributeRepositoryImpl
import org.koin.dsl.module

val attributeRepoModule =
    module {
        single<AttributeRepository> { AttributeRepositoryImpl() }
    }
