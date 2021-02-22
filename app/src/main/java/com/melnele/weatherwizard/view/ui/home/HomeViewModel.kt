package com.melnele.weatherwizard.view.ui.home

import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.melnele.weatherwizard.data.repository.WeatherRepo
import com.melnele.weatherwizard.data.state.WeatherState
import kotlinx.coroutines.launch

class HomeViewModel(private val weatherRepo: WeatherRepo) : ViewModel() {
    private val _weather = MutableLiveData<WeatherState>(WeatherState.Idle)
    val weather: LiveData<WeatherState> = _weather

    fun getWeather(latLng: LatLng?, lang: String?, refresh: Boolean) {
        viewModelScope.launch {
            if (latLng == null)
                return@launch
            _weather.value = try {
                val res = weatherRepo.getWeather(latLng.latitude, latLng.longitude, lang, !refresh)

//                res.body()?.alerts =
//                    listOf(Alerts("home_sender", "event", 1613538966, 1613538966, "desc"))

                val body = res.body()
                if (body != null && res.isSuccessful) {
                    WeatherState.WeatherResponseState(body)
                } else
                    WeatherState.ErrorState(res.message())
            } catch (e: Exception) {
                WeatherState.ErrorState(e.message)
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(private val weatherRepo: WeatherRepo) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        (HomeViewModel(weatherRepo) as T)
}