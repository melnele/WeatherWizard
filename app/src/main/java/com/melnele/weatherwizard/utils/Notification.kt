package com.melnele.weatherwizard.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.melnele.weatherwizard.R
import com.melnele.weatherwizard.view.ui.alerts.ScanReceiver

object Notification {
    private const val ALERT_CHANNEL_ID: String = "Alert"
    private const val ALARM_CHANNEL_ID: String = "Alarm"

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alert"
            val descriptionText = "Weather alerts"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(ALERT_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createAlarmChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alarm"
            val descriptionText = "Weather alarms"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val channel = NotificationChannel(ALARM_CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setSound(sound, attributes)
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createNotification(context: Context, notificationId: Int, title: String, desc: String) {
        createNotificationChannel(context)
        val builder = NotificationCompat.Builder(context, ALERT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(desc)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }
    }

    fun createAlarm(
        context: Context,
        notificationId: Int,
        title: String,
        desc: String,
        fullScreenIntent: Intent
    ) {
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, 0,
            fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        createAlarmChannel(context)
        val builder = NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(desc)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setSound(sound)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }
    }

    fun updateScanner(context: Context, alarmMgr: AlarmManager, time: Long) {
        val alarmIntent = Intent(context, ScanReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                context,
                ScanReceiver.SCANNER_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        alarmMgr.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            time,
            AlarmManager.INTERVAL_DAY,
            alarmIntent
        )
    }

    fun cancelScanner(context: Context, alarmMgr: AlarmManager) {
        val alarmIntent = Intent(context, ScanReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                context,
                ScanReceiver.SCANNER_CODE,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_CANCEL_CURRENT
            )
        }

        alarmMgr.cancel(alarmIntent)
    }
}
