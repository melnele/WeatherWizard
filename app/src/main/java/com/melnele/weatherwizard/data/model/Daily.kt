package com.melnele.weatherwizard.data.model

data class Daily(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Temp,
    val feels_like: Feels_like,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val wind_speed: Double,
    val wind_gust: Double,
    val wind_deg: Int,
    val weather: List<Weather>,
    val clouds: Int,
    val pop: Double,
    val uvi: Double,
    val rain: Double,
    val snow: Double
)