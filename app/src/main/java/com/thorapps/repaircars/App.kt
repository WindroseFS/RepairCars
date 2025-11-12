package com.thorapps.repaircars

import android.app.Application
import androidx.work.WorkManager
import com.thorapps.repaircars.auth.SharedPreferencesHelper

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    lateinit var sharedPrefHelper: SharedPreferencesHelper

    override fun onCreate() {
        super.onCreate()
        instance = this
        sharedPrefHelper = SharedPreferencesHelper(this)

        // Inicializações seguras para Android 11+
        setupWorkManager()
    }

    private fun setupWorkManager() {
        try {
            // Inicialização segura do WorkManager
            WorkManager.initialize(this, androidx.work.Configuration.Builder().build())
        } catch (e: IllegalStateException) {
            // Já inicializado, ignora
        }
    }
}