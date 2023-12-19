package com.thorne.pillapp.util.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
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
    println("MedNotificationService.startReminder()")
        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent =
            Intent(
                applicationContext.applicationContext,
                NotificationReceiver::class.java
            ).let { intent ->
                intent.action = Actions.NOTIFICATION.name
                intent.putExtra("medId", medId)
                intent.putExtra("medName", medName)
                intent.putExtra("medDosage", medDosage)
                intent.putExtra("medFrequency", medFrequency)
                intent.putExtra("medStartHour", medStartHour)
                intent.putExtra("medStartMin", medStartMin)
                intent.putExtra("medStartDate", medStartDate)
                intent.putExtra("medEndDate", medEndDate)
                intent.putExtra("reminderId", reminderId)
//                applicationContext.sendBroadcast(intent)
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (
                alarmManager.canScheduleExactAlarms()
            ) {
                alarmManager.setInexactRepeating (
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    medFrequency.toLong() * AlarmManager.INTERVAL_HOUR,
                    intent
                )
            }
                Log.i("MedNotifs", "Alarm starting at ${calendar.timeInMillis}, repeating every ${medFrequency.toLong() * AlarmManager.INTERVAL_HOUR} milliseconds")
            if (alarmManager.nextAlarmClock.triggerTime > 0) {
                Log.d("MedNotifs", "Next alarm is scheduled for ${alarmManager.nextAlarmClock.triggerTime}")
            }
        }
    }

    fun stopReminder(
        applicationContext: Context,
        reminderId: Int = notificationCode
    ) {
        Log.i("MedNotifs", "stopReminder()")
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