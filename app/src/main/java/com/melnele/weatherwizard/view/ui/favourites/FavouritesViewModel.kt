package com.melnele.weatherwizard.view.ui.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.melnele.weatherwizard.data.repository.FavouriteRepo

class FavouritesViewModel(favouriteRepo: FavouriteRepo) : ViewModel() {
    val favourites = favouriteRepo.loadAll().asLiveData()
}

@Suppress("UNCHECKED_CAST")
class FavouritesViewModelFactory(private val favouriteRepo: FavouriteRepo) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        (FavouritesViewModel(favouriteRepo) as T)
}