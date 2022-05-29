package com.interview.pagination.ui.list

import com.interview.pagination.weather.WeatherApiHelper
import javax.inject.Inject

class ListRepository @Inject constructor(
    private val apiHelper: WeatherApiHelper
) {
    suspend fun getWeatherApiLatLong(latitude: String, longitude: String) =
        apiHelper.getWeatherLatLong(latitude, longitude)
}