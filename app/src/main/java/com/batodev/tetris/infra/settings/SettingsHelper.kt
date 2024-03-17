package com.batodev.tetris.infra.settings

import android.app.Activity
import android.util.Log
import com.google.gson.Gson
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object SettingsHelper {
    fun load(activity: Activity): SettingsData {
        val settingsFile = settingsFile(activity)
        try {
            if (settingsFile.isFile && settingsFile.canRead()) {
                FileReader(settingsFile).use {
                    return Gson().fromJson(it, SettingsData::class.java)
                }
            } else {
                Log.w(SettingsHelper::class.java.simpleName, "no settings yet, returned defaults")
            }
        } catch (e: Exception) {
            Log.e(
                SettingsHelper::class.java.simpleName,
                "classes incompatible, will delete settings",
                e
            )
            settingsFile.delete()
        }
        return SettingsData()
    }

    fun save(activity: Activity, settingsData: SettingsData): SettingsData {
        val settingsFile = settingsFile(activity)
        return try {
            settingsFile.delete()
            val json = Gson().toJson(settingsData)
            FileWriter(settingsFile).use {
                it.write(json)
            }
            settingsData
        } catch (e: Exception) {
            Log.e(
                SettingsHelper::class.java.simpleName,
                "error saving, will delete settings and retry",
                e
            )
            settingsFile.delete()
            save(activity, settingsData)
        }
    }

    private fun settingsFile(activity: Activity) =
        File(activity.filesDir, SettingsHelper::class.java.simpleName)
}