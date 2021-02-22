package com.melnele.weatherwizard.utils

import android.content.Context
import java.util.*

class Convert {
    companion object {
        fun mpsToMPH(mps: Double) = (mps * 2.236936)
        fun cToK(c: Double) = (c + 273.15)
        fun cToF(c: Double) = (9 / 5.0 * c) + 32

        //    fun fToC(f: Double) = 5 / 9.0 * (f - 32)
        fun setLocale(context: Context, languageCode: String) {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = context.resources.configuration
            config.setLocale(locale)
            context.applicationContext.createConfigurationContext(config)
        }
    }
}