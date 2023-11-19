package com.thorne.sdk.meds

import java.util.UUID

@kotlinx.serialization.Serializable
class MedicationImpl(
    private var name: String,
    private var dosage: String,
    private var frequency: Int,
    private var startHour: Int,
    private var startMin: Int,
    private var startDate: Long,
    private var endDate: Long,
    private var notes: String
) : Medication {
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

    override fun getFrequency(): Int {
        return frequency
    }

    override fun getStartHour(): Int {
        return startHour
    }

    override fun getStartMin(): Int {
        return startMin
    }

    override fun getStartDate(): Long {
        return startDate
    }

    override fun getEndDate(): Long {
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

    override fun setFrequency(frequency:  Int) {
        this.frequency = frequency
    }

    override fun setStartHour(startHour: Int) {
        this.startHour = startHour
    }

    override fun setStartMin(startMin: Int) {
        this.startMin = startMin
    }

    override fun setStartDate(startDate: Long) {
        this.startDate = startDate
    }

    override fun setEndDate(endDate: Long) {
        this.endDate = endDate
    }

    override fun setNotes(notes: String) {
        this.notes = notes
    }
}