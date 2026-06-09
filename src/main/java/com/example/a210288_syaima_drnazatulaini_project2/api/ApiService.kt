package com.example.a210288_syaima_drnazatulaini_project2.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("advice")
    suspend fun getRandomAdvice(): AdviceResponse

    companion object {
        private const val BASE_URL = "https://api.adviceslip.com/"

        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}