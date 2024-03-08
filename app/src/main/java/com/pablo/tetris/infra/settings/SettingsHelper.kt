package com.pablo.tetris.infra.settings

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InvalidClassException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object SettingsHelper {
    var settingsData: SettingsData = SettingsData()

    fun load(activity: AppCompatActivity): SettingsData {
        val settingsFile = settingsFile(activity)
        try {
            if (settingsFile.isFile && settingsFile.canRead()) {
                val fis = FileInputStream(settingsFile)
                ObjectInputStream(fis).use {
                    settingsData = it.readObject() as SettingsData
                }
            } else {
                Log.w(SettingsHelper::class.java.simpleName, "no settings yet, returned defaults")
            }
        } catch (e: InvalidClassException) {
            Log.e(SettingsHelper::class.java.simpleName, "classes incompatible, will delete settings", e)
            settingsFile.delete()
        }
        return settingsData
    }

    fun save(activity: AppCompatActivity, settingsData: SettingsData): SettingsData {
        val settingsFile = settingsFile(activity)
        val fos = FileOutputStream(settingsFile)
        ObjectOutputStream(fos).use {
            it.writeObject(settingsData)
        }
        return settingsData
    }

    private fun settingsFile(activity: AppCompatActivity) =
        File(activity.filesDir, SettingsHelper::class.java.simpleName)
}