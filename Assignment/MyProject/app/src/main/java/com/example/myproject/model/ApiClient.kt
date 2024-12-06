package com.example.myproject.model

import retrofit2.converter.moshi.MoshiConverterFactory

//import com.jakewharton.retrofit2.converter.moshi.MoshiConverterFactory

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit

object ApiCliente {
    private const val BASE_URL = "https://twinword-word-graph-dictionary.p.rapidapi.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val instance: TwinwordService by lazy {
        retrofit.create(TwinwordService::class.java)
    }
}