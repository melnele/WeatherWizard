package com.melnele.weatherwizard.view.ui.alerts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.melnele.weatherwizard.R
import com.melnele.weatherwizard.data.model.Alerts
import com.melnele.weatherwizard.utils.Notification

class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alertArg = context.getString(R.string.KEY_ALERT_ARG)
        val alarm: Boolean
        val flag: Boolean
        PreferenceManager.getDefaultSharedPreferences(context).apply {
            flag = getBoolean(context.getString(R.string.KEY_ALERTS), true)
            alarm = getBoolean(context.getString(R.string.KEY_ALARM), false)
        }
        if (!flag)
            return
        val alerts = intent.getBundleExtra(alertArg)?.getSerializable(alertArg) as Alerts?
        alerts?.let { alert ->
            if (alarm) {
                val fullScreenIntent = Intent(context, AlarmActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val bundle = Bundle()
                    bundle.putSerializable(alertArg, alert)
                    putExtra(alertArg, bundle)
                }
                Notification.createAlarm(
                    context,
                    alert.hashCode(),
                    alert.event,
                    alert.description,
                    fullScreenIntent
                )
            } else
                Notification.createNotification(
                    context,
                    alert.hashCode(),
                    alert.event,
                    alert.description
                )
        }
    }
}