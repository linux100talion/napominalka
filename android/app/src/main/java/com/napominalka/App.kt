package com.napominalka

import android.app.Application
import com.napominalka.notif.Notifications

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Notifications.createChannels(this)
    }
}
