package com.dqitech.kttelematicsolutions

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration


class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .allowWritesOnUiThread(true)
            .allowQueriesOnUiThread(true)
            .name("demo")
            .build()
        Realm.setDefaultConfiguration(config)
    }
}
