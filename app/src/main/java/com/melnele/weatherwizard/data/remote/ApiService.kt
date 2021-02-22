package com.melnele.weatherwizard.data.remote

import com.melnele.weatherwizard.data.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {
    @GET("onecall")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String,
        @Query("units") units: String?,
        @Query("lang") lang: String?,
        @Query("appid") app_id: String,
        @Header("Cache-Control") cache: String?,
    ): Response<WeatherResponse>
}