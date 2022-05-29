package com.interview.pagination.api

import com.interview.pagination.model.UsersResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UsersApi {
    @GET("api/?inc=gender,name,location,picture,email,id,nat")
    suspend fun getUsers(
        @Query("results") per_page: Int?,
        @Query("page") page: Int?,
    ): UsersResponse
}