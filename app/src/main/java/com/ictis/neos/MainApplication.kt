package com.ictis.neos

import android.app.Application
import android.util.Log
import com.ictis.neos.core.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin

class MainApplication : Application(), KoinComponent {

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(
                coreModule,
                authModule,
                apiModule,
                cameraModule,
                trainingModule,
                recognitionModule,
            )
        }
    }

    companion object {
        private const val TAG = "MainApplication"
        lateinit var instance: MainApplication
    }
}