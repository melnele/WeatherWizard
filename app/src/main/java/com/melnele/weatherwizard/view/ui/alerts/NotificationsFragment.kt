package com.melnele.weatherwizard.view.ui.alerts

import android.app.AlarmManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.melnele.weatherwizard.R
import com.melnele.weatherwizard.databinding.FragmentNotificationsBinding
import com.melnele.weatherwizard.utils.Notification
import java.text.DateFormat
import java.util.*

class NotificationsFragment : Fragment() {
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        setUI()
        binding.tbtn.setOnCheckedChangeListener { _, isChecked ->
            binding.clOptions.visibility = if (isChecked) View.VISIBLE else View.GONE
            setAlert(isChecked)
        }
        binding.swtAlarm.setOnCheckedChangeListener { _, isChecked ->
            PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
                putBoolean(getString(R.string.KEY_ALARM), isChecked)
                apply()
            }
            binding.swtAlarm.text =
                if (isChecked) getString(R.string.alarm) else getString(R.string.notification)
        }
        binding.btnFrom.setOnClickListener {
            TimePickerFragment { _, hourOfDay, minute ->
                PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
                    putString(getString(R.string.KEY_ALERT_FROM), "$hourOfDay:$minute")
                    apply()
                }
                binding.tvFrom.text = timeText(hourOfDay, minute)
                val alarmMgr = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                val time = Calendar.getInstance()
                time.set(Calendar.HOUR_OF_DAY, hourOfDay)
                time.set(Calendar.MINUTE, minute)
                time.set(Calendar.SECOND, 0)
                time.set(Calendar.MILLISECOND, 0)

                context?.let { Notification.updateScanner(it, alarmMgr, time.timeInMillis) }
            }.show(childFragmentManager, "timePickerFrom")
        }

        binding.btnTo.setOnClickListener {
            TimePickerFragment { _, hourOfDay, minute ->
                PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
                    putString(getString(R.string.KEY_ALERT_TO), "$hourOfDay:$minute")
                    apply()
                }
                binding.tvTo.text = timeText(hourOfDay, minute)
            }.show(childFragmentManager, "timePickerTO")
        }
        return binding.root
    }

    private fun setUI() {
        val alerts: Boolean
        val alarm: Boolean
        val from: String?
        val to: String?
        PreferenceManager.getDefaultSharedPreferences(context).apply {
            alerts = getBoolean(getString(R.string.KEY_ALERTS), false)
            alarm = getBoolean(getString(R.string.KEY_ALARM), false)
            from = getString(getString(R.string.KEY_ALERT_FROM), null)
            to = getString(getString(R.string.KEY_ALERT_TO), null)
        }
        binding.clOptions.visibility = if (alerts) View.VISIBLE else View.GONE

        binding.swtAlarm.text =
            if (alarm) getString(R.string.alarm) else getString(R.string.notification)
        binding.tbtn.isChecked = alerts
        binding.swtAlarm.isChecked = alarm
        timeText(from)?.let { binding.tvFrom.text = it }
        timeText(to)?.let { binding.tvTo.text = it }
    }

    private fun timeText(hourOfDay: Int, minute: Int): String {
        val time = Calendar.getInstance()
        time.set(Calendar.HOUR_OF_DAY, hourOfDay)
        time.set(Calendar.MINUTE, minute)
        time.set(Calendar.SECOND, 0)
        time.set(Calendar.MILLISECOND, 0)
        return DateFormat.getTimeInstance(DateFormat.SHORT).format(time.time)
    }

    private fun timeText(time: String?): String? {
        val t = time?.split(":")
        return if (t != null && t.size == 2) {
            try {
                timeText(t[0].toInt(), t[1].toInt())
            } catch (e: Exception) {
                return null
            }
        } else
            null
    }

    private fun setAlert(alert: Boolean) {
        val from: String?
        PreferenceManager.getDefaultSharedPreferences(context).apply {
            from = getString(getString(R.string.KEY_ALERT_FROM), null)
        }
        PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
            putBoolean(getString(R.string.KEY_ALERTS), alert)
            apply()
        }
        val alarmMgr = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (alert) {
            val time = Calendar.getInstance()
            if (from != null) {
                val t = from.split(":")
                if (t.size == 2) {
                    time.set(Calendar.HOUR_OF_DAY, t[0].toInt())
                    time.set(Calendar.MINUTE, t[1].toInt())
                    time.set(Calendar.SECOND, 0)
                    time.set(Calendar.MILLISECOND, 0)
                }
            } else {
                time.add(Calendar.DATE, 1)
            }

            context?.let { Notification.updateScanner(it, alarmMgr, time.timeInMillis) }
        } else {
            context?.let { Notification.cancelScanner(it, alarmMgr) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}