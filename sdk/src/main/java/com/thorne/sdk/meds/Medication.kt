package com.thorne.sdk.meds

interface Medication {
    val id: String
    val name: String
    val dosage: String
    val frequency: String
    val startDate: String
    val endDate: String
    val notes: String

    fun getId(): String
}