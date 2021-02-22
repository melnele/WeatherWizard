package com.melnele.weatherwizard.view.settings

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.melnele.weatherwizard.R
import com.melnele.weatherwizard.utils.Convert
import kotlin.system.exitProcess

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finishAffinity()
        startActivity(packageManager.getLaunchIntentForPackage(packageName))
        exitProcess(0)
//        super.onBackPressed()
    }

    override fun attachBaseContext(newBase: Context?) {
        val lang: String
        PreferenceManager.getDefaultSharedPreferences(newBase).apply {
            if (newBase != null) {
                lang = getString(newBase.getString(R.string.KEY_LANG), "en").toString()
                Convert.setLocale(newBase, lang)
            }
        }
        super.attachBaseContext(newBase)
    }
}