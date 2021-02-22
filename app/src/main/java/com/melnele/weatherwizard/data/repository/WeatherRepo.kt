package com.melnele.weatherwizard.data.repository

import com.melnele.weatherwizard.data.model.WeatherResponse
import com.melnele.weatherwizard.data.remote.ApiService
import com.melnele.weatherwizard.data.remote.Constant
import retrofit2.Response

class WeatherRepo(private val apiService: ApiService) {
    suspend fun getWeather(
        lat: Double,
        lon: Double,
        lang: String?,
        cache: Boolean
    ): Response<WeatherResponse> {
        return if (cache)
            apiService.getWeather(lat, lon, "minutely", "metric", lang, Constant.API_KEY, null)
        else
            apiService.getWeather(
                lat,
                lon,
                "minutely",
                "metric",
                lang,
                Constant.API_KEY,
                "no-cache"
            )
    }
}