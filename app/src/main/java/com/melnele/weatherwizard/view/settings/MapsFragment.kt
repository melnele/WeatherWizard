package com.melnele.weatherwizard.view.settings

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.melnele.weatherwizard.R
import com.melnele.weatherwizard.data.local.FavouriteDB
import com.melnele.weatherwizard.data.model.Favourite
import com.melnele.weatherwizard.databinding.FragmentMapsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MapsFragment : Fragment() {
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private var favourite: Boolean = false
    private var latLng: LatLng? = null

    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.setOnMapClickListener {
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(it))
            latLng = it
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.map_menu_save) {
            latLng?.let {
                GlobalScope.launch(Dispatchers.IO) {
                    context?.let { context ->
                        val geoCoder = Geocoder(context)
                        val addresses = geoCoder.getFromLocation(it.latitude, it.longitude, 1)
                        if (favourite) {
                            val db = FavouriteDB.getDatabase(context)
                            val newFavourite = Favourite()
                            newFavourite.title = binding.etTitle.text.toString()
                            newFavourite.latitude = it.latitude
                            newFavourite.longitude = it.longitude
                            newFavourite.city =
                                if (addresses.isNotEmpty()) addresses[0].locality else null
                            db.favouriteDao().save(newFavourite)
                        } else {
                            PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
                                putString(
                                    getString(R.string.KEY_LOCATION),
                                    "${it.latitude},${it.longitude}"
                                )
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
                        }
                    }
                }
                activity?.finish()
                return true
            }
            Toast.makeText(context, getString(R.string.select_location_map), Toast.LENGTH_LONG)
                .show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getBoolean(getString(R.string.KEY_FAVOURITE))?.let { favourite = it }
        if (favourite) {
            binding.etTitle.visibility = View.VISIBLE
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}