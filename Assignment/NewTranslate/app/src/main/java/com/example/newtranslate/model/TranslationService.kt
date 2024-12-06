package com.example.newtranslate.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TranslationService {
    @GET("get")
    fun getTranslation(
        @Query("q") text: String,
        @Query("langpair") langpair: String
    ): Call<TranslationResponse>
}
