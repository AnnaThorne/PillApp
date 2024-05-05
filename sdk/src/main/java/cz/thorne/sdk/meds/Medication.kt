package cz.thorne.sdk.meds

@kotlinx.serialization.Serializable
sealed interface Medication {
    fun getId(): String
    fun getName(): String
    fun getDosage(): String
    fun getFrequency(): Int
    fun getStartHour(): Int
    fun getStartMin(): Int
    fun getStartDate(): Long
    fun getEndDate(): Long
    fun getNotes(): String

    fun setId(id: String)
    fun setName(name: String)
    fun setDosage(dosage: String)
    fun setFrequency(frequency: Int)
    fun setStartHour(startHour: Int)
    fun setStartMin(startMin: Int)
    fun setStartDate(startDate: Long)
    fun setEndDate(endDate: Long)
    fun setNotes(notes: String)
}