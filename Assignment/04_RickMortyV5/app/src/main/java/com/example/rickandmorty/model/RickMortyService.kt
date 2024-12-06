package com.example.rickandmorty.model

import retrofit2.Call
import retrofit2.http.GET

interface  RickMortyService{
    @GET("character")
    fun getCharacters(): Call<CharacterResponse>
}