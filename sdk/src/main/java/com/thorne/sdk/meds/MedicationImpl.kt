package com.thorne.sdk.meds

import java.io.Serializable
import java.util.Date
import java.util.UUID

class MedicationImpl(
    private var name: String,
    private var dosage: String,
    private var frequency: String,
    private var startDate: Date,
    private var endDate: Date,
    private var notes: String
) : Medication, Serializable {
    private var id: String = UUID.randomUUID().toString()

    override fun getId(): String {
        return id
    }

    override fun getName(): String {
        return name
    }

    override fun getDosage(): String {
        return dosage
    }

    override fun getFrequency(): String {
        return frequency
    }

    override fun getStartDate(): Date {
        return startDate
    }

    override fun getEndDate(): Date {
        return endDate
    }

    override fun getNotes(): String {
        return notes
    }

    override fun setId(id: String) {
        this.id = id
    }

    override fun setName(name: String) {
        this.name = name
    }

    override fun setDosage(dosage: String) {
        this.dosage = dosage
    }

    override fun setFrequency(frequency: String) {
        this.frequency = frequency
    }

    override fun setStartDate(startDate: Date) {
        this.startDate = startDate
    }

    override fun setEndDate(endDate: Date) {
        this.endDate = endDate
    }

    override fun setNotes(notes: String) {
        this.notes = notes
    }
}