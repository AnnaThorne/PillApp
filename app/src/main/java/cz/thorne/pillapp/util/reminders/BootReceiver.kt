package cz.thorne.pillapp.util.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cz.thorne.sdk.MedSdkImpl
import cz.thorne.sdk.meds.Medication

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            MedSdkImpl.getInstance().initialize(context.applicationContext)
            // Fetch all medications from storage
            val medications: List<Medication> = MedSdkImpl.getInstance().getMedicationList()

            // Reschedule alarms for each medication
            for (medication in medications) {
                MedNotificationService.startReminder(
                    context,
                    medId = medication.getId(),
                    medName = medication.getName(),
                    medDosage = medication.getDosage(),
                    medFrequency = medication.getFrequency(),
                    medStartHour = medication.getStartHour(),
                    medStartMin = medication.getStartMin(),
                    medStartDate = medication.getStartDate(),
                    medEndDate = medication.getEndDate()
                )
            }
        }
    }
}