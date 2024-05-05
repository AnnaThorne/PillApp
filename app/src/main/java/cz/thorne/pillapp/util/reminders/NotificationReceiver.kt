package cz.thorne.pillapp.util.reminders

import android.Manifest
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cz.thorne.pillapp.R
import cz.thorne.sdk.MedSdkImpl
import java.util.Date
import kotlin.random.Random

class NotificationReceiver : BroadcastReceiver() {

    // Sends notification on alarm receive
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("MedNotifs", "onReceive() called")
        val medId = intent?.getStringExtra("medId")
        val medName = intent?.getStringExtra("medName")
        val medDosage = intent?.getStringExtra("medDosage")
        val medFrequency = intent?.getIntExtra("medFrequency", 0)
        val medStartHour = intent?.getIntExtra("medStartHour", 0)
        val medStartMinute = intent?.getIntExtra("medStartMin", 0)
        val medStartDate = intent?.getLongExtra("medStartDate", 0)
        val medEndDate = intent?.getLongExtra("medEndDate", 0)
        val reminderId = intent?.getIntExtra("reminderId", 0)

        if (context != null) {
            showBasicNotification(context, medName!!, medDosage!!)
        }

        if (context != null) {
            if (Date().time > medEndDate!! || !checkIfMedicineStillExists(
                    context.applicationContext!!,
                    medId!!
                )
            ) {
                Log.d("MedNotifs", "Medicine has expired or no longer exists, stopping reminder.")
                MedNotificationService.stopReminder(context.applicationContext, reminderId!!)
                return
            }
            if (isMedChanged(
                    context.applicationContext!!,
                    medId,
                    medName!!,
                    medDosage!!,
                    medFrequency!!,
                    medStartHour!!,
                    medStartMinute!!,
                    medStartDate!!,
                    medEndDate
                )
            ) {
                Log.d("MedNotifs", "Medicine has changed, stopping reminder.")
                MedNotificationService.stopReminder(context.applicationContext, reminderId!!)
                return
            }
        }



        if (context != null) {
            Log.d("MedNotifs", "Medicine has not expired, rescheduling reminder?")
            MedNotificationService.startReminder(
                context.applicationContext,
                reminderId!!,
                medId!!,
                medName!!,
                medDosage!!,
                medFrequency!!,
                medStartHour!!,
                medStartMinute!!,
                medStartDate!!,
                medEndDate!!
            )
        }
    }

    private fun showBasicNotification(context: Context, medName: String, medDosage: String) {
        Log.i("MedNotifs", "showBasicNotification() called")
        val body = "It's time to take your $medName $medDosage!"
        val builder = NotificationCompat.Builder(
            context,
            context.getString(R.string.reminders_notification_channel_id)
        )
            .setContentTitle("Med Reminder")
            .setContentText("$medName $medDosage")
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(Random.nextInt(), builder.build())
        }
    }

    private fun checkIfMedicineStillExists(context: Context, medId: String): Boolean {
        if (!MedSdkImpl.getInstance().isInitialized()) {
            MedSdkImpl.getInstance().initialize(context)
        }
        return MedSdkImpl.getInstance().getMedicationById(medId) != null
    }

    private fun isMedChanged(
        context: Context,
        medId: String,
        medName: String,
        medDosage: String,
        medFrequency: Int,
        medStartHour: Int,
        medStartMinute: Int,
        medStartDate: Long,
        medEndDate: Long
    ): Boolean {
        if (!MedSdkImpl.getInstance().isInitialized()) {
            Log.d("MedNotifs", "Medication SDK not initialized, initializing now.")
            MedSdkImpl.getInstance().initialize(context)
        }
        val med = MedSdkImpl.getInstance().getMedicationById(medId)
        if (med!!.getId() == medId && med.getName() == medName && med.getDosage() == medDosage && med.getFrequency() == medFrequency && med.getStartHour() == medStartHour && med.getStartMin() == medStartMinute && med.getStartDate() == medStartDate && med.getEndDate() == medEndDate) {
            Log.d("MedNotifs", "Medication has not changed.")
            return false
        }
        Log.d("MedNotifs", "Medication has changed.")
        Log.d("MedNotifs", "SDK - Intent")
        Log.d("MedNotifs", "Medication ID: ${med.getId()} == $medId")
        Log.d("MedNotifs", "Medication Name: ${med.getName()} == $medName")
        Log.d("MedNotifs", "Medication Dosage: ${med.getDosage()} == $medDosage")
        Log.d("MedNotifs", "Medication Frequency: ${med.getFrequency()} == $medFrequency")
        Log.d("MedNotifs", "Medication Start Hour: ${med.getStartHour()} == $medStartHour")
        Log.d("MedNotifs", "Medication Start Minute: ${med.getStartMin()} == $medStartMinute")
        Log.d("MedNotifs", "Medication Start Date: ${med.getStartDate()} == $medStartDate")
        Log.d("MedNotifs", "Medication End Date: ${med.getEndDate()} == $medEndDate")
        return true
    }
}