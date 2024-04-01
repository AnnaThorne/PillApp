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

        // Suppress warning because we're only ever keeping application context,
        // which doesn't change, so we're not creating a memory leak
        @SuppressLint("StaticFieldLeak")

        @Volatile
        private var INSTANCE: MedSdkImpl? = null

        private var isInitialized = false

        // In-memory storage buffer,
        // loaded from disk on initialization
        // and saved to disk on any changes
        private var medicationList = ArrayList<Medication>()

        // Storage manager
        private var storageManager = MedicationStorageManagerImpl()

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

    /**
     * Initialize the SDK with the application context
     * @param context Application Context
     */
    override fun initialize(context: Context) {
        this.context = context.applicationContext
        isInitialized = true
        loadFromStorage()
    }

    /**
     * Assert the SDK is initialized
     * @return Boolean True if the SDK is initialized
     */
    override fun isInitialized(): Boolean {
        return isInitialized
    }

    /**
     * Add new Medication
     * @param medication Medication to add
     */
    override fun addMedication(medication: Medication) {
        assertInitialized()
        medicationList.add(medication)
        updateStorage()
    }

    /**
     * Remove Medication
     * @param medication Medication to remove
     */
    override fun removeMedication(medication: Medication) {
        assertInitialized()
        medicationList.remove(medication)
        updateStorage()
    }

    /**
     * Update Medication
     * @param id: ID of the Medication to update
     * @param name: new Name of the Medication
     * @param dosage: new Dosage of the Medication
     * @param frequency: new Frequency of the Medication
     * @param startHour: new Start Hour of the Medication
     * @param startMinute: new Start Minute of the Medication
     * @param startDate: new Start Date of the Medication
     * @param endDate: new End Date of the Medication
     * @param notes: new Notes of the Medication
     */
    override fun updateMedication(
        id: String,
        name: String,
        dosage: String,
        frequency: Int,
        startHour: Int,
        startMinute: Int,
        startDate: Long,
        endDate: Long,
        notes: String
    ) {
        assertInitialized()
        medicationList.forEach {
            if (it.getId() == id) {
                it.setName(name)
                it.setDosage(dosage)
                it.setFrequency(frequency)
                it.setStartHour(startHour)
                it.setStartMin(startMinute)
                it.setStartDate(startDate)
                it.setEndDate(endDate)
                it.setNotes(notes)
            }
        }
        updateStorage()
    }

    /**
     * Remove Medication by ID
     * @param id ID of the Medication to remove
     */
    override fun removeMedication(id: String) {
        assertInitialized()
        medicationList.removeIf { it.getId() == id }
        updateStorage()
    }

    /**
     * Get Medication by ID
     * @param id ID of the Medication to get
     * @return Medication? Medication with the provided ID
     */
    override fun getMedicationById(id: String): Medication? {
        assertInitialized()
        return medicationList.find { it.getId() == id }
    }

    /**
     * Get list of all Medications
     * @return List<Medication> List of all Medications
     */
    override fun getMedicationList(): ArrayList<Medication> {
        assertInitialized()
        return medicationList
    }

    /**
     * Get SDK version
     * @return String SDK version
     */
    override fun getSdkVersion(): String {
        assertInitialized()
        return "1.0.2"
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

    // Internal helper function to make the class testable
    internal fun setCustomStorageManager(customStorageManager: MedicationStorageManagerImpl) {
        storageManager = customStorageManager
    }
}