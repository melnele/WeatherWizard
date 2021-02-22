package com.melnele.weatherwizard.view.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.melnele.weatherwizard.R
import com.melnele.weatherwizard.data.model.Daily
import com.melnele.weatherwizard.data.remote.Constant
import com.melnele.weatherwizard.databinding.WeatherItemBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DaysAdapter(
    private val days: List<Daily>,
    private val timezone_offset: Int,
    private val getTempString: (temp: Double) -> String,
    private val getWindSpeedString: (speed: Double) -> String
) :
    RecyclerView.Adapter<DaysAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = WeatherItemBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val x = holder.itemView.context
        val day = days[position]
        holder.tvCurrentTemp.text = getTempString(day.temp.day)
        holder.tvWindSpeed.text = getWindSpeedString(day.wind_speed)
        holder.tvHumidity.text = x.getString(R.string.humidity, day.humidity)
        holder.tvPressure.text = x.getString(R.string.pressure, day.pressure)
        holder.tvClouds.text = x.getString(R.string.clouds, day.clouds)
        holder.tvCity.visibility = View.GONE


        val date = Date((day.dt + timezone_offset) * 1000)
        val pattern = "EEE, d MMM yyyy"
        val simpleDateFormat: DateFormat = SimpleDateFormat(pattern)
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        holder.tvDateTime.text = simpleDateFormat.format(date)

        holder.tvWeatherDesc.text = day.weather[0].description
        val icon = "${Constant.ICON_LINK + day.weather[0].icon}@4x.png"
        Glide.with(holder.itemView)
            .load(icon)
            .override(Target.SIZE_ORIGINAL)
            .into(holder.ivIcon)
    }

    override fun getItemCount(): Int = days.size

    class ViewHolder(binding: WeatherItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvCurrentTemp = binding.tvCurrentTemp
        val tvDateTime = binding.tvDateTime
        val tvCity = binding.tvCity
        val tvHumidity = binding.tvHumidity
        val tvWindSpeed = binding.tvWindSpeed
        val tvPressure = binding.tvPressure
        val tvClouds = binding.tvClouds
        val ivIcon = binding.ivIcon
        val tvWeatherDesc = binding.tvWeatherDesc
    }
}