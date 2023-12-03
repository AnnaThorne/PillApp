package com.thorne.pillapp.util.reminders

import android.Manifest
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.thorne.pillapp.R
import com.thorne.sdk.MedSdkImpl
import java.util.Date
import kotlin.random.Random

class NotificationReceiver : BroadcastReceiver() {


    // Sends notification on alarm receive
    override fun onReceive(context: Context?, intent: Intent?) {

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
            if (Date().time > medEndDate!! || !checkIfMedicineStillExists(
                    context.applicationContext!!,
                    medId!!
                )
            ) {
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
                MedNotificationService.stopReminder(context.applicationContext, reminderId!!)
                return
            }
        }

        if (context != null) {
            showBasicNotification(context, medName!!, medDosage!!)
        }

        if (context != null) {
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
            MedSdkImpl.getInstance().initialize(context)
        }
        val med = MedSdkImpl.getInstance().getMedicationById(medId)

        if (med!!.getId() == medId && med.getName() == medName && med.getDosage() == medDosage && med.getFrequency() == medFrequency && med.getStartHour() == medStartHour && med.getStartMin() == medStartMinute && med.getStartDate() == medStartDate && med.getEndDate() == medEndDate) {
            return false
        }
        return true
    }
}