package com.example.pettycash

import android.app.Application
import com.example.pettycash.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PettyCashApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PettyCashApplication)
            modules(AppModule)
        }
    }
}
