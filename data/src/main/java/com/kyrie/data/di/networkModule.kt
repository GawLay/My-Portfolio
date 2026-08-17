package com.kyrie.data.di

import com.kyrie.data.remote.SupabaseStorageClient
import okhttp3.OkHttpClient
import org.koin.dsl.module

val networkModule =
    module {
        single { OkHttpClient.Builder().build() }
        single { SupabaseStorageClient(get()) }
    }
