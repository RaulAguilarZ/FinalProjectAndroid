package com.example.newfinalproiect


import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.newfinalproiect.ui.theme.screens.SignInScreen

class SignInActivity  : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold (modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val content: Context = applicationContext
                    SignInScreen(content,modifier= Modifier.padding((innerPadding)))
                }
            }
        }
    }
}