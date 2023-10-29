package com.thorne.sdk.storage

import android.content.Context
import com.thorne.sdk.meds.Medication
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File


class MedicationStorageManagerImpl : StorageManager<ArrayList<Medication>> {
    override fun saveToStorage(context: Context, data: ArrayList<Medication>) {
        createFileIfNotExists(context)
        context.openFileOutput("medications.json", Context.MODE_PRIVATE).use {
            it.write(Json.encodeToString(data).toByteArray())
            it.flush()
            it.close()
        }
    }

    override fun loadFromStorage(context: Context): ArrayList<Medication> {
        createFileIfNotExists(context)
        context.openFileInput("medications.json").use {
            val data = String(it.readBytes(), Charsets.UTF_8)
            it.close()
            return Json.decodeFromString(data) as ArrayList<Medication>
        }
    }
    override fun isEmpty(context: Context): Boolean {
        val file = File(context.filesDir, "medications.json")

        if (file.exists()) {
            return file.length().toInt() == 0
        }
        return false
    }

    private fun createFileIfNotExists(context: Context) {
        val file = File(context.filesDir, "medications.json")
        if (!file.exists()) {
            file.createNewFile()
        }
    }
}
