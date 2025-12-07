package com.kyrie.data.di

import com.kyrie.data.storage.local.IPrefSource
import com.kyrie.data.storage.local.PrefSourceImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val storageModule =
    module {
        single<IPrefSource> { PrefSourceImpl(androidContext()) }
    }
