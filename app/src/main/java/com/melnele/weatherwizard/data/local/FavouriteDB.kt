package com.melnele.weatherwizard.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.melnele.weatherwizard.data.model.Favourite

@Database(entities = [Favourite::class], version = 1, exportSchema = false)
abstract class FavouriteDB : RoomDatabase() {
    abstract fun favouriteDao(): FavouriteDao

    companion object {
        var instance: FavouriteDB? = null
        fun getDatabase(context: Context): FavouriteDB {
            instance ?: synchronized(this) {
                val roomInstance =
                    Room.databaseBuilder(context, FavouriteDB::class.java, "favouriteDb").build()
                instance = roomInstance
            }
            return instance!!
        }
    }
}