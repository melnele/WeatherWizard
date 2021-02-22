package com.melnele.weatherwizard.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.melnele.weatherwizard.data.model.Favourite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDao {
    @Insert(onConflict = REPLACE)
    fun save(favourite: Favourite)

    @Query("SELECT * FROM Favourite")
    fun loadAll(): Flow<List<Favourite>>

    @Delete
    fun delete(favourite: Favourite)
}