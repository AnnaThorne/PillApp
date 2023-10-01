package com.thorne.sdk.meds

import java.util.Date

interface Medication {
    fun getId(): String
    fun getName(): String
    fun getDosage(): String
    fun getFrequency(): String
    fun getStartDate(): Date
    fun getEndDate(): Date
    fun getNotes(): String

    fun setId(id: String)
    fun setName(name: String)
    fun setDosage(dosage: String)
    fun setFrequency(frequency: String)
    fun setStartDate(startDate: Date)
    fun setEndDate(endDate: Date)
    fun setNotes(notes: String)
}