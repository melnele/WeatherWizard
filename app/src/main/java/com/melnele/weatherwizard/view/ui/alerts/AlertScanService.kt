package com.melnele.weatherwizard.view.ui.alerts

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.JobIntentService
import androidx.preference.PreferenceManager
import com.melnele.weatherwizard.R
import com.melnele.weatherwizard.data.model.Alerts
import com.melnele.weatherwizard.data.remote.RetrofitConfig
import com.melnele.weatherwizard.data.repository.WeatherRepo
import com.melnele.weatherwizard.utils.Notification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class AlertScanService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        val flag: Boolean
        val loc: String?
        val lang: String?
        PreferenceManager.getDefaultSharedPreferences(this).apply {
            flag = getBoolean(getString(R.string.KEY_ALERTS), true)
            loc = getString(getString(R.string.KEY_LOCATION), null)
            lang = getString(getString(R.string.KEY_LANG), null)
        }
        if (!flag)
            return
        val latLng = loc?.split(',')

        if (latLng != null && latLng.size == 2) {
            GlobalScope.launch(Dispatchers.IO) {
                val weatherRepo =
                    WeatherRepo(RetrofitConfig.getApiService(this@AlertScanService))
                val res =
                    weatherRepo.getWeather(latLng[0].toDouble(), latLng[1].toDouble(), lang, false)
                if (res.isSuccessful) {
//                    res.body()?.alerts =
//                        listOf(Alerts("sender", "event", 1613538966, 1613538966, "desc"))

                    res.body()?.let {
                        if (it.alerts != null && it.alerts.isNotEmpty()) {
                            setAlerts(it.alerts, it.timezone_offset)
                        } else {
                            Notification.createNotification(
                                this@AlertScanService,
                                0,
                                getString(R.string.app_name),
                                getString(R.string.no_weather_alerts)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setAlerts(alerts: List<Alerts>, timezone_offset: Int) {
        val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        for (alert in alerts) {
            val date = Date((alert.start + timezone_offset) * 1000)
            val alarmIntent: PendingIntent =
                Intent(this, AlertReceiver::class.java).let { intent ->
                    val bundle = Bundle()
                    val alertArg = getString(R.string.KEY_ALERT_ARG)
                    bundle.putSerializable(alertArg, alert)
                    intent.putExtra(alertArg, bundle)
                    PendingIntent.getBroadcast(this, alert.hashCode(), intent, 0)
                }

            alarmMgr.set(AlarmManager.RTC_WAKEUP, date.time, alarmIntent)
        }
    }

    companion object {
        private const val JOB_ID = 1111
        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, AlertScanService::class.java, JOB_ID, work)
        }
    }
}