package com.thorne.sdk

import android.annotation.SuppressLint
import android.content.Context
import com.thorne.sdk.meds.Medication
import com.thorne.sdk.storage.MedicationStorageManagerImpl

class MedSdkImpl private constructor() : MedSdk {
    ///////////////////////////////////////////////////////////////////////////
    // Variables
    ///////////////////////////////////////////////////////////////////////////
    companion object {
        // Private reference to the singleton instance

        // Suppress warning because we're only ever storing application context
        // which doesn't change, so we're not creating a memory leak
        @SuppressLint("StaticFieldLeak")

        @Volatile
        private var INSTANCE: MedSdkImpl? = null

        private var isInitialized = false
        private var medicationList = ArrayList<Medication>()
        private val storageManager = MedicationStorageManagerImpl()

        // Function to get or create the singleton instance
        fun getInstance(): MedSdkImpl {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MedSdkImpl().also { INSTANCE = it }
            }
        }
    }

    private var context: Context? = null

    ///////////////////////////////////////////////////////////////////////////
    // Public Functions
    ///////////////////////////////////////////////////////////////////////////
    override fun initialize(context: Context) {
        this.context = context.applicationContext
        isInitialized = true
        loadFromStorage()
    }

    override fun isInitialized(): Boolean {
        return isInitialized
    }

    override fun addMedication(medication: Medication) {
        assertInitialized()
        medicationList.add(medication)
        updateStorage()
    }

    override fun removeMedication(medication: Medication) {
        assertInitialized()
        medicationList.remove(medication)
        updateStorage()
    }

    override fun removeMedication(id: String) {
        assertInitialized()
        medicationList.removeIf { it.getId() == id }
        updateStorage()
    }

    override fun getMedicationById(id: String): Medication {
        assertInitialized()
        return medicationList.find { it.getId() == id }!!
    }

    override fun getMedicationList(): ArrayList<Medication> {
        assertInitialized()
        return medicationList
    }

    override fun getSdkVersion(): String {
        assertInitialized()
        return "1.0.0"
    }

    ///////////////////////////////////////////////////////////////////////////
    // Private Functions
    ///////////////////////////////////////////////////////////////////////////
    private fun assertInitialized() {
        if (!isInitialized) {
            throw IllegalStateException("MedSdkImpl not initialized, please call initialize() first")
        }
    }

    private fun updateStorage() {
        storageManager.saveToStorage(context!!, medicationList)
    }

    private fun loadFromStorage() {
        if (!storageManager.isEmpty(context!!)) {
            medicationList = storageManager.loadFromStorage(context!!)
        }
    }
}