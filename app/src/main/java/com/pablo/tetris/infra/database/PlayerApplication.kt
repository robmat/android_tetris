package com.pablo.tetris.infra.database

import android.app.Application
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.google.android.material.color.DynamicColors

class PlayerApplication() : Application(), ViewModelStoreOwner {
    val database by lazy { PlayerRoomDatabase.getDatabase(this) }
    val repository by lazy { PlayerRepository(database.playerDao()) }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }

    override val viewModelStore: ViewModelStore
        get() = ViewModelStore()
}