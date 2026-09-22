package com.example.prstamolabctma

import android.app.Application
import com.example.prstamolabctma.data.AppContainer
import com.example.prstamolabctma.data.DefaultAppContainer

class PrestamoLabApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}