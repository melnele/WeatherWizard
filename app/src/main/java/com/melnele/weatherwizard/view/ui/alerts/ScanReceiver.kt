package com.melnele.weatherwizard.view.ui.alerts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScanReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val i = Intent(context.applicationContext, AlertScanService::class.java)
        AlertScanService.enqueueWork(context.applicationContext, i)
    }

    companion object {
        const val SCANNER_CODE = 11111
    }
}