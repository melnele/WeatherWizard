package com.melnele.weatherwizard.data.model

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)