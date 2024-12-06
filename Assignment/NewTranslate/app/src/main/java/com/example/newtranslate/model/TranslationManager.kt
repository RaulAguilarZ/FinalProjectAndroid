package com.example.newtranslate.model

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class TranslationResult(
    val translatedText: String? = null,
    val errorMessage: String? = null
)

class TranslationManager {

    fun fetchTranslation(text: String, sourceLang: String, targetLang: String, onResult: (TranslationResult) -> Unit) {
        // Validar que el texto no esté vacío
        if (text.isBlank()) {
            onResult(TranslationResult(errorMessage = "El texto no puede estar vacío"))
            return
        }

        // Validar que los códigos de lenguaje sean válidos
        if (!isValidLanguage(sourceLang) || !isValidLanguage(targetLang)) {
            onResult(TranslationResult(errorMessage = "Códigos de idioma inválidos"))
            return
        }

        val call = ApiClient.service.getTranslation(text, "$sourceLang|$targetLang")

        call.enqueue(object : Callback<TranslationResponse> {
            override fun onResponse(call: Call<TranslationResponse>, response: Response<TranslationResponse>) {
                if (response.isSuccessful) {
                    val translatedText = response.body()?.responseData?.translatedText
                    onResult(TranslationResult(translatedText = translatedText))
                } else {
                    // Manejo de respuesta no exitosa
                    onResult(TranslationResult(errorMessage = "Error en la respuesta de la API: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<TranslationResponse>, t: Throwable) {
                // Manejo de fallos en la llamada
                onResult(TranslationResult(errorMessage = "Error en la conexión: ${t.message}"))
            }
        })
    }

    private fun isValidLanguage(lang: String): Boolean {
        val validLanguages = listOf("en", "es", "fr", "de", "it", "pt", "ru", "zh", "ja", "ko")
        return lang in validLanguages
    }
}
