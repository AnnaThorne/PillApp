package com.thorne.pillapp.util.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import kotlin.random.Random

object MedNotificationService {

    private val notificationCode = Random.nextInt()
    fun startReminder(
        applicationContext: Context,
        reminderId: Int = notificationCode,
        medId: String,
        medName: String,
        medDosage: String,
        medFrequency: Int,
        medStartHour: Int,
        medStartMin: Int,
        medStartDate: Long,
        medEndDate: Long,
    ) {
        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent =
            Intent(
                applicationContext.applicationContext,
                NotificationReceiver::class.java
            ).let { intent ->
                intent.putExtra("medId", medId)
                intent.putExtra("medName", medName)
                intent.putExtra("medDosage", medDosage)
                intent.putExtra("medFrequency", medFrequency)
                intent.putExtra("medStartHour", medStartHour)
                intent.putExtra("medStartMin", medStartMin)
                intent.putExtra("medStartDate", medStartDate)
                intent.putExtra("medEndDate", medEndDate)
                intent.putExtra("reminderId", reminderId)
                PendingIntent.getBroadcast(
                    applicationContext.applicationContext,
                    reminderId,
                    intent,
                    PendingIntent.FLAG_MUTABLE
                )
            }

        val calendar: Calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, medStartHour)
            set(Calendar.MINUTE, medStartMin)
        }

        // Sanity check for if the time has already passed today
        if (Calendar.getInstance()
                .apply { add(Calendar.MINUTE, 1) }.timeInMillis - calendar.timeInMillis > 0
        ) {
            calendar.add(Calendar.DATE, 1)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (
                alarmManager.canScheduleExactAlarms()
            ) {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    medFrequency.toLong() * 3600000,
                    intent
                )
            }
        }
    }

    fun stopReminder(
        applicationContext: Context,
        reminderId: Int = notificationCode
    ) {
        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(applicationContext, NotificationReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                applicationContext,
                reminderId,
                intent,
                PendingIntent.FLAG_MUTABLE
            )
        }
        alarmManager.cancel(intent)
    }

    fun getNextId(): Int {
        return notificationCode
    }
}