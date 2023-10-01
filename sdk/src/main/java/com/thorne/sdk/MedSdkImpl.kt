package com.thorne.sdk

import com.thorne.sdk.meds.Medication

class MedSdkImpl private constructor() : MedSdk {

    companion object {
        // Private reference to the singleton instance
        @Volatile
        private var INSTANCE: MedSdkImpl? = null

        // Function to get or create the singleton instance
        fun getInstance(): MedSdkImpl {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MedSdkImpl().also { INSTANCE = it }
            }
        }
    }

    override fun initialize() {
        TODO("Not yet implemented")
    }

    override fun addMedication(medication: Medication) {
        TODO("Not yet implemented")
    }

    override fun removeMedication(medication: Medication) {
        TODO("Not yet implemented")
    }

    override fun removeMedication(id: String) {
        TODO("Not yet implemented")
    }

    override fun getMedicationById(id: String): Medication {
        TODO("Not yet implemented")
    }

    override fun getMedicationList(): List<Medication> {
        TODO("Not yet implemented")
    }

    override fun getSdkVersion(): String {
        TODO("Not yet implemented")
    }
}