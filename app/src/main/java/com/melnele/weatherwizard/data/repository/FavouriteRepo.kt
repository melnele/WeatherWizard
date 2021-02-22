package com.melnele.weatherwizard.data.repository

import com.melnele.weatherwizard.data.local.FavouriteDao

class FavouriteRepo(private val favouriteDao: FavouriteDao) {
    fun loadAll() = favouriteDao.loadAll()
}