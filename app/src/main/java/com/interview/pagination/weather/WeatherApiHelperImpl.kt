package com.interview.pagination.weather

import com.interview.pagination.utils.WeatherResponse
import retrofit2.Response
import javax.inject.Inject

class WeatherApiHelperImpl @Inject constructor(
    private val apiService: WeatherApiService,
) : WeatherApiHelper {

    override suspend fun getWeather(country: String): Response<WeatherResponse> =
        apiService.resultWeatherApi(country, "a461cd77dab8aa9df411138cb109c8a2")

    override suspend fun getWeatherLatLong(
        latitude: String,
        longitude: String
    ): Response<WeatherResponse> =apiService.resultWeatherApiLatLong(latitude,longitude,"a461cd77dab8aa9df411138cb109c8a2")


}