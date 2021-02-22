package com.melnele.weatherwizard.data.model

import com.google.gson.annotations.SerializedName

data class Snow(
    @SerializedName("1h") val h: Double
)