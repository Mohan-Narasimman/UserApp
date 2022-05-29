package com.interview.pagination.ui.detail

import com.interview.pagination.weather.WeatherApiHelper
import javax.inject.Inject

class DetailRepository @Inject constructor(
    private val apiHelper: WeatherApiHelper
) {
    suspend fun getWeatherApi(country: String) = apiHelper.getWeather(country)
}