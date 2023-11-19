package com.thorne.sdk

import android.content.Context
import com.thorne.sdk.meds.Medication

interface MedSdk {
    fun initialize(context: Context)

    fun isInitialized(): Boolean

    fun addMedication(medication: Medication)

    fun removeMedication(id: String)

    fun removeMedication(medication: Medication)

    fun updateMedication(
        id: String, name: String, dosage: String, frequency: Int, startHour: Int, startMinute: Int, startDate: Long, endDate: Long, notes: String
    )

    fun getMedicationById(id: String): Medication?
    fun getMedicationList(): List<Medication>
    fun getSdkVersion(): String
}