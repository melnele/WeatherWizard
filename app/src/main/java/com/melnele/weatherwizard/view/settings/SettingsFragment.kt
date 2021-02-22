package com.melnele.weatherwizard.view.settings

import android.Manifest
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.*
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.melnele.weatherwizard.R

class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var locationCallback: LocationCallback

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val gpsPreference = findPreference<SwitchPreferenceCompat>(getString(R.string.KEY_GPS))
        gpsPreference?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue == false) {
                return@setOnPreferenceChangeListener true
            }
            createLocationRequest()
        }
    }

    override fun onResume() {
        val locationPreference =
            findPreference<EditTextPreference>(getString(R.string.KEY_CITY))
        PreferenceManager.getDefaultSharedPreferences(context?.applicationContext).apply {
            locationPreference?.summary =
                getString(getString(R.string.KEY_CITY), getString(R.string.unknown_place))
            registerOnSharedPreferenceChangeListener(object :
                SharedPreferences.OnSharedPreferenceChangeListener {
                override fun onSharedPreferenceChanged(
                    sharedPreferences: SharedPreferences?,
                    key: String?
                ) {
                    if (sharedPreferences != null) {
                        locationPreference?.summary =
                            sharedPreferences.getString(
                                getString(R.string.KEY_CITY),
                                getString(R.string.unknown_place)
                            )
                    }
                    unregisterOnSharedPreferenceChangeListener(this)
                }
            })
        }
        super.onResume()
    }

    private fun createLocationRequest(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
            val locationRequest = LocationRequest.create().apply {
                interval = 10000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener {
                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        locationResult ?: return
                        locationResult.locations[0].let {
                            val sharedPref =
                                PreferenceManager.getDefaultSharedPreferences(requireContext())
                            val loc = "${it.latitude},${it.longitude}"
                            val geoCoder = Geocoder(context)
                            val addresses = geoCoder.getFromLocation(it.latitude, it.longitude, 1)
                            with(sharedPref.edit()) {
                                putString(getString(R.string.KEY_LOCATION), loc)
                                if (addresses.isNotEmpty())
                                    putString(
                                        getString(R.string.KEY_CITY),
                                        addresses[0].locality
                                    )
                                else
                                    putString(
                                        getString(R.string.KEY_CITY),
                                        null
                                    )
                                apply()
                            }
                            val locationPreference =
                                findPreference<EditTextPreference>(getString(R.string.KEY_CITY))
                            locationPreference?.summary = sharedPref.getString(
                                getString(R.string.KEY_CITY),
                                getString(R.string.unknown_place)
                            )
                            fusedLocationClient.removeLocationUpdates(locationCallback)
                        }
                    }
                }
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
            task.addOnFailureListener { exception ->
                val gpsPreference =
                    findPreference<SwitchPreferenceCompat>(getString(R.string.KEY_GPS))
                gpsPreference?.isChecked = false
                if (exception is ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        exception.startResolutionForResult(activity, 0)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }

            return true
        } else {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 0
                )
            }
            return false
        }
    }
}