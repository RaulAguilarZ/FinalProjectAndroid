package com.example.myproject.model

import android.util.Log
import com.example.myproject.model.ApiClient
import com.example.myproject.model.Message
import com.example.myproject.model.OpenAIRequest
import com.example.myproject.model.OpenAIResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class OpenAIManager(private val apiKey: String) {
    fun translateWord(word: String, onResult: (String?) -> Unit) {
        val request = OpenAIRequest(
            model = "gpt-3.5-turbo",
            messages = listOf(Message(content = "Translate this to Spanish: $word"))
        )

        val call = ApiClient.instance.translate(request)

        call.enqueue(object : Callback<OpenAIResponse> {
            override fun onResponse(call: Call<OpenAIResponse>, response: Response<OpenAIResponse>) {
                if (response.isSuccessful) {
                    val translatedWord = response.body()?.choices?.firstOrNull()?.message?.content ?: ""
                    onResult(translatedWord.takeIf { it.isNotBlank() } ?: "Sin traducción disponible")
                } else {
                    Log.e("OpenAIManager", "Error en la respuesta: ${response.errorBody()?.string()}")
                    onResult("Error en la API: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<OpenAIResponse>, t: Throwable) {
                Log.e("OpenAIManager", "Error en la solicitud: ${t.message}")
                onResult("Error en la solicitud: ${t.message}")
            }
        })
    }
}