package com.thorne.sdk

import com.thorne.sdk.meds.Medication

interface  MedSdk {
    fun initialize()


    fun addMedication(medication: Medication)

    fun removeMedication(medication: Medication)

    fun removeMedication(id: String)
    fun getMedicationById(id: String): Medication
    fun getMedicationList(): List<Medication>
    fun getSdkVersion(): String
}