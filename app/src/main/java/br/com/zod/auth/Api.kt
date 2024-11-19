package br.com.zod.auth

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Api {

    private const val BASE_URL = "https://d4836fa7-40fc-4712-8e68-07b4268b957e-00-2by3xwiq4inmo.kirk.replit.dev"


    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}