package com.example.myapplication.data.api

import com.example.myapplication.data.model.ShoonyaLoginRequest
import com.example.myapplication.data.model.ShoonyaLoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ShoonyaApiService {
    @POST("QuickAuth")
    suspend fun login(@Body request: ShoonyaLoginRequest): Response<ShoonyaLoginResponse>

    companion object {
        const val BASE_URL = "https://api.shoonya.com/NorenWSTP/"
    }
}
