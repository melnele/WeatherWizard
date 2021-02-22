package com.melnele.weatherwizard.view.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.melnele.weatherwizard.data.model.Hourly
import com.melnele.weatherwizard.data.remote.Constant
import com.melnele.weatherwizard.databinding.HourListItemBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class HoursAdapter(
    private val hours: List<Hourly>,
    private val timezone_offset: Int,
    private val getTempString: (m: Double) -> String
) :
    RecyclerView.Adapter<HoursAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HourListItemBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hour = hours[position]

        holder.tvTemp.text = getTempString(hour.temp)

        val date = Date((hour.dt + timezone_offset) * 1000)
        val pattern = "h a"
        val simpleDateFormat: DateFormat = SimpleDateFormat(pattern)
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        holder.tvTime.text = simpleDateFormat.format(date)

        val icon = "${Constant.ICON_LINK + hour.weather[0].icon}@4x.png"
        Glide.with(holder.itemView)
            .load(icon)
            .override(Target.SIZE_ORIGINAL)
            .into(holder.ivIcon)
    }

    override fun getItemCount(): Int = hours.size

    class ViewHolder(binding: HourListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvTime = binding.tvTime
        val ivIcon = binding.ivIcon
        val tvTemp = binding.tvTemp
    }
}