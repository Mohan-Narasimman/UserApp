package com.interview.pagination.weather

import com.interview.pagination.utils.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather?")
    suspend fun resultWeatherApi(
        @Query("q") q: String?,
        @Query("APPID") appId: String?
    ): Response<WeatherResponse>


    @GET("weather?")
    suspend fun resultWeatherApiLatLong(
        @Query("lat") lat: String?,
        @Query("lon") lon: String?,
        @Query("APPID") appId: String?
    ): Response<WeatherResponse>
}