package com.example.rickandmorty.RM04

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.rickandmorty.RM04.Character
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback

class AppDatabase {

    class MoviesManager(database: AppDatabase) {
        private val _charactersResponse = mutableStateOf<List<Character>>(emptyList()) // top level api
       // val api_key:String="<replaceWithKey>"

        val moviesResponse: MutableState<List<Character>> // state allows us to make data available to other classes
            @Composable get() = remember{
                _charactersResponse
            }

        // Iniciamos la llamada a la API en el init
        init{
            getCharacteres(database)
        }
        private fun getCharacteres(database: AppDatabase, page: Int = 1) {
            val service = Api.retrofitService.getCharacters(page)

            service.enqueue(object : Callback<CharacterResponse> {
                override fun onResponse(
                    call: Call<CharacterResponse>,
                    response: retrofit2.Response<CharacterResponse>
                ) {
                    if (response.isSuccessful){
                        Log.i("Data", "Data Loaded")
                        //val movieData = response.body()
                        //Log.i("DataStream", movieData.toString())
                        _charactersResponse.value = response.body()?.results?: emptyList()
                        Log.i("DataStream", _charactersResponse .toString())

                        GlobalScope.launch {
                            saveDataToDatabase(database = database, _charactersResponse.value)
                        }
                    }
                }

                override fun onFailure(call: Call<CharacterResponse>, t: Throwable) {
                    Log.d("error", "${t.message}")
                }
            })
        }
        private suspend fun saveDataToDatabase(database: AppDatabase, movies: List<Movie>){
            database.characterDao().insertAllCharacters(movies)
        }
    }

}