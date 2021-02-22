package com.melnele.weatherwizard.data.model

import java.io.Serializable

data class Alerts(
    val sender_name: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String
) : Serializable