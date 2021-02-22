package com.melnele.weatherwizard.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Favourite {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo
    var title: String = ""

    @ColumnInfo
    var latitude: Double = 0.0

    @ColumnInfo
    var longitude: Double = 0.0

    @ColumnInfo
    var city: String? = null
}