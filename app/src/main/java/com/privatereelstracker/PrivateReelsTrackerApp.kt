package com.privatereelstracker

import android.app.Application
import com.privatereelstracker.data.local.AppDatabase
import com.privatereelstracker.data.repository.ReelRepository

class PrivateReelsTrackerApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var repository: ReelRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
        repository = ReelRepository(
            reelDao = database.reelDao(),
            diagnosticDao = database.diagnosticDao()
        )
    }
}
