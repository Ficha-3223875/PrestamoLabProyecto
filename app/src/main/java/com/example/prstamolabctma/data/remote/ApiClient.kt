package com.example.prstamolabctma.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // Cambia esta URL por la IP de tu servidor backend o emulador (ej: "http://10.0.2.2:8000/" para localhost en emulador)
    private const val BASE_URL = "https://api.example.com/"

    val retrofitService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}