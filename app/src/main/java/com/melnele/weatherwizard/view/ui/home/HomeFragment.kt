package com.melnele.weatherwizard.view.ui.home

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.google.android.gms.maps.model.LatLng
import com.melnele.weatherwizard.R
import com.melnele.weatherwizard.data.model.Alerts
import com.melnele.weatherwizard.data.model.WeatherResponse
import com.melnele.weatherwizard.data.remote.Constant
import com.melnele.weatherwizard.data.remote.RetrofitConfig
import com.melnele.weatherwizard.data.repository.WeatherRepo
import com.melnele.weatherwizard.data.state.WeatherState
import com.melnele.weatherwizard.databinding.FragmentHomeBinding
import com.melnele.weatherwizard.utils.Convert
import com.melnele.weatherwizard.view.ui.alerts.AlertReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel by viewModels<HomeViewModel> {
        HomeViewModelFactory(WeatherRepo(RetrofitConfig.getApiService(requireContext())))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        observeViewModel()

        getWeather(false)
        return binding.root
    }

    private fun observeViewModel() {
        homeViewModel.weather.observe(viewLifecycleOwner, {
            when (it) {
                is WeatherState.ErrorState -> {
                    GlobalScope.launch(Dispatchers.Main) {
                        Toast.makeText(context, it.error, Toast.LENGTH_LONG).show()
                    }
                }

                is WeatherState.WeatherResponseState -> {
                    GlobalScope.launch(Dispatchers.Main) {
                        it.weatherResponse.let { weatherResponse ->
                            setUI(weatherResponse)
                            val favourite =
                                arguments?.getParcelable<LatLng>(getString(R.string.KEY_LAT_LNG))
                            val flag: Boolean
                            PreferenceManager.getDefaultSharedPreferences(context).apply {
                                flag = getBoolean(getString(R.string.KEY_ALERTS), false)
                            }
                            if (flag && favourite == null && weatherResponse.alerts != null && weatherResponse.alerts.isNotEmpty())
                                setAlerts(weatherResponse.alerts, weatherResponse.timezone_offset)
                        }
                    }
                }
                else -> {
                    binding.weatherItem.root.visibility = View.GONE
                    binding.rvHourly.visibility = View.GONE
                    binding.rvDaily.visibility = View.GONE
                    binding.swiperefresh.isRefreshing = false
//                    Toast.makeText(context, getString(R.string.error), Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun getWeather(refresh: Boolean) {
        val lang: String?
        PreferenceManager.getDefaultSharedPreferences(context).apply {
            lang = getString(getString(R.string.KEY_LANG), "en")
        }
        val favourite = arguments?.getParcelable<LatLng>(getString(R.string.KEY_LAT_LNG))
        if (favourite != null) {
            homeViewModel.getWeather(favourite, lang, refresh)
        } else {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            val loc = sharedPref.getString(getString(R.string.KEY_LOCATION), null)
            val ll = loc?.split(',')
            val latLng = ll?.get(1)?.let { LatLng(ll[0].toDouble(), it.toDouble()) }

            homeViewModel.getWeather(latLng, lang, refresh)
        }
    }

    private fun setAlerts(alerts: List<Alerts>, timezone_offset: Int) {
        val alarmMgr = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        for (alert in alerts) {
            val date = Date((alert.start + timezone_offset) * 1000)
            val alarmIntent: PendingIntent =
                Intent(context, AlertReceiver::class.java).let { intent ->
                    val bundle = Bundle()
                    val alertArg = getString(R.string.KEY_ALERT_ARG)
                    bundle.putSerializable(alertArg, alert)
                    intent.putExtra(alertArg, bundle)
                    PendingIntent.getBroadcast(context, alert.hashCode(), intent, 0)
                }

            alarmMgr.set(AlarmManager.RTC_WAKEUP, date.time, alarmIntent)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setUI(weatherResponse: WeatherResponse) {
        binding.weatherItem.root.visibility = View.VISIBLE
        binding.rvHourly.visibility = View.VISIBLE
        binding.rvDaily.visibility = View.VISIBLE
        binding.weatherItem.tvCurrentTemp.text = getTempString(weatherResponse.current.temp)
        binding.weatherItem.tvWindSpeed.text =
            getWindSpeedString(weatherResponse.current.wind_speed)
        binding.weatherItem.tvHumidity.text =
            getString(R.string.humidity, weatherResponse.current.humidity)
        binding.weatherItem.tvPressure.text =
            getString(R.string.pressure, weatherResponse.current.pressure)
        binding.weatherItem.tvClouds.text =
            getString(R.string.clouds, weatherResponse.current.clouds)
        binding.weatherItem.tvCity.text = weatherResponse.timezone.split('/')[1]

        val date = Date((weatherResponse.current.dt + weatherResponse.timezone_offset) * 1000)
        val pattern = "EEE, d MMM yyyy\nh:mm a"
        val simpleDateFormat: DateFormat = SimpleDateFormat(pattern)
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        binding.weatherItem.tvDateTime.text = simpleDateFormat.format(date)

        binding.weatherItem.tvWeatherDesc.text = weatherResponse.current.weather[0].description
        val icon = "${Constant.ICON_LINK + weatherResponse.current.weather[0].icon}@4x.png"
        Glide.with(binding.root)
            .load(icon)
            .override(Target.SIZE_ORIGINAL)
            .into(binding.weatherItem.ivIcon)

        binding.rvHourly.adapter =
            HoursAdapter(
                weatherResponse.hourly.subList(1, 25),
                weatherResponse.timezone_offset,
                getTempString
            )
        binding.rvHourly.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvHourly.scrollToPosition(0)

        binding.rvDaily.adapter =
            DaysAdapter(
                weatherResponse.daily.subList(1, 8),
                weatherResponse.timezone_offset,
                getTempString,
                getWindSpeedString
            )
        binding.rvDaily.layoutManager = LinearLayoutManager(context)
        binding.rvDaily.scrollToPosition(0)
        binding.swiperefresh.setOnRefreshListener {
            getWeather(true)
        }
        binding.swiperefresh.isRefreshing = false
    }

    private val getTempString: (temp: Double) -> String = { temp ->
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        when (sharedPref.getString(getString(R.string.KEY_TEMP), "celsius")) {
            "kelvin" -> getString(R.string.temp, Convert.cToK(temp), "°K")
            "fahrenheit" -> getString(R.string.temp, Convert.cToF(temp), "°F")
            else -> getString(R.string.temp, temp, "°C")
        }
    }

    private val getWindSpeedString: (speed: Double) -> String = { speed ->
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        when (sharedPref.getString(getString(R.string.KEY_WIND), "mps")) {
            "mph" -> getString(R.string.wind_speed, Convert.mpsToMPH(speed), "MpH")
            else -> getString(R.string.wind_speed, speed, "M/S")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}