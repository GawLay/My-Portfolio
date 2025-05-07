package com.kyrie.myportfolio

import androidx.multidex.MultiDexApplication
import com.kyrie.data.di.repositoriesModule
import com.kyrie.domain.di.useCasesModule
import com.kyrie.myportfolio.di.viewModelModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyPortfolioApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@MyPortfolioApp)
            modules(
                repositoriesModule,
                useCasesModule,
                viewModelModules,
            )
        }
    }
}
