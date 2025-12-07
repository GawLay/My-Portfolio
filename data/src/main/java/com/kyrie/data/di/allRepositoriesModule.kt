package com.kyrie.data.di

import org.koin.dsl.module

val repositoriesModule =
    module {
        includes(
            profileRepoModule,
            skillRepoModule,
            expRepoModule,
            templateModule,
            faqRepoModule,
            storageModule,
        )
    }
