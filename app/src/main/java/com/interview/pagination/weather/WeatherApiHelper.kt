package com.interview.pagination.weather

import com.interview.pagination.utils.WeatherResponse
import retrofit2.Response

interface WeatherApiHelper {
    suspend fun getWeather(country: String): Response<WeatherResponse>
    suspend fun getWeatherLatLong(latitude: String,longitude: String): Response<WeatherResponse>
}