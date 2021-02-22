package com.melnele.weatherwizard.view

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.melnele.weatherwizard.R
import com.melnele.weatherwizard.utils.Convert
import com.melnele.weatherwizard.utils.Notification
import com.melnele.weatherwizard.view.settings.SettingsActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_favourites, R.id.navigation_notifications)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        PreferenceManager.getDefaultSharedPreferences(this).apply {
            if (!getBoolean(getString(R.string.KEY_FRESH), false)) {
                AlertDialog.Builder(this@MainActivity).setMessage(getString(R.string.ask_alerts))
                    .setPositiveButton(getString(R.string.yes)) { _, _ -> setAlerts(true) }
                    .setNegativeButton(getString(R.string.no)) { _, _ -> setAlerts(false) }
                    .create().show()
            }
        }
    }

    private fun setAlerts(alert: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().apply {
            putBoolean(getString(R.string.KEY_FRESH), true)
            putBoolean(getString(R.string.KEY_ALERTS), alert)
            apply()
        }
        if (alert) {
            val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val time = Calendar.getInstance()
            time.add(Calendar.DATE, 1)

            Notification.updateScanner(this, alarmMgr, time.timeInMillis)
        }
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings_menu_item) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            return true
        }
        if (item.itemId == R.id.exit_menu_item) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}