package com.melnele.weatherwizard.view.settings

import android.content.Context
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.melnele.weatherwizard.R
import com.melnele.weatherwizard.databinding.ActivityMapBinding
import com.melnele.weatherwizard.utils.Convert

class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.findFragmentById(R.id.fragment)?.arguments = intent.extras
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_menu, menu)
        return true
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
}