package com.melnele.weatherwizard.data.model

data class Current(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    val wind_speed: Double,
    val wind_gust: Double,
    val wind_deg: Double,
    val weather: List<Weather>,
    val rain: Rain,
    val snow: Snow
)