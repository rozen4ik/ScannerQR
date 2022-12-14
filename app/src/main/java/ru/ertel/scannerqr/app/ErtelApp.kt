package ru.ertel.scannerqr.app

import android.app.Application
import ru.ertel.scannerqr.app.di.moduleNFC
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ErtelApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            printLogger()
            androidContext(this@ErtelApp)
            modules(moduleNFC)
        }
    }
}