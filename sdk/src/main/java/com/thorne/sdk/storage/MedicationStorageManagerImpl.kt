package com.thorne.sdk.storage

import android.content.Context
import com.thorne.sdk.meds.Medication
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileWriter
import java.io.IOException


class MedicationStorageManagerImpl : StorageManager<ArrayList<Medication>> {
    override fun saveToStorage(context: Context,data: ArrayList<Medication>) {
        context.openFileOutput("medications", Context.MODE_PRIVATE).use {
            it.write(Json.encodeToString(data).toByteArray())
            it.flush()
            it.close()
        }
    }

    override fun loadFromStorage(context: Context) : ArrayList<Medication> {
        context.openFileInput("medications").use {
            val data = it.readBytes().toString()
            it.close()
            return Json.decodeFromString(data) as ArrayList<Medication>
        }
    }

    override fun isEmpty(context: Context): Boolean {
        val file = File(context.filesDir, "medications")
        if (file.exists()) {
            return file.length() == 0L
        }
        return false
    }
}
