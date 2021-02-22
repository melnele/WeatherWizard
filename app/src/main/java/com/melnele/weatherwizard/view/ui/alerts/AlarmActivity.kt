package com.melnele.weatherwizard.view.ui.alerts


import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.melnele.weatherwizard.R
import com.melnele.weatherwizard.data.model.Alerts
import com.melnele.weatherwizard.databinding.ActivityAlarmBinding


class AlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setTurnScreenOn(true)
            setShowWhenLocked(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val alertArg = getString(R.string.KEY_ALERT_ARG)
        val alerts = intent.getBundleExtra(alertArg)?.getSerializable(alertArg) as Alerts?
        alerts?.let {
            binding.tvTitle.text = it.event
            binding.tvDesc.text = it.description
            binding.tvSender.text = it.sender_name
        }
    }
}