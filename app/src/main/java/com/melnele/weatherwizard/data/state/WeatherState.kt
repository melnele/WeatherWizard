package com.melnele.weatherwizard.data.state

import com.melnele.weatherwizard.data.model.WeatherResponse

sealed class WeatherState {
    data class WeatherResponseState(val weatherResponse: WeatherResponse) : WeatherState()
    data class ErrorState(val error: String?) : WeatherState()
    object Idle : WeatherState()
}
