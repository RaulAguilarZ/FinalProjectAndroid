package com.example.newfinalproiect

import android.Manifest
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.newfinalproiect.DataB.AppDatabase
import com.example.newfinalproiect.DataB.TranslationEntity
import com.example.newfinalproiect.model.Api
import com.example.newfinalproiect.model.TextRequestBody
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * MainActivity class that extends ComponentActivity.
 * This class manages the app's lifecycle and UI using Jetpack Compose.
 * It includes features such as requesting runtime permissions, displaying a splash screen,
 * and navigating to the main functionality of the app.
 */
class MainActivity : ComponentActivity() {

    /**
     * Called when the activity is first created. This method sets up the UI, handles
     * runtime permission requests, and manages the splash screen transition.
     *
     * @param savedInstanceState The saved instance state bundle.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register an activity result launcher to request audio recording permissions
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            // Notify the user if the permission is denied
            if (!isGranted) {
                Toast.makeText(this, "Microphone permission is required", Toast.LENGTH_LONG).show()
            }
        }

        // Launch the permission request for RECORD_AUDIO
        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)

        // Set the content of the activity using Jetpack Compose
        setContent {
            MaterialTheme {
                // Set the status bar color to black
                window.statusBarColor = getColor(R.color.black)

                // Display the background of the app
                //BakGround()

                // Define the main surface of the app with full screen size
                Surface(modifier = Modifier.fillMaxSize()) {

                    // State variable to track whether to show the splash screen
                    var showSplash by remember { mutableStateOf(true) }

                    // Show the splash screen if `showSplash` is true, otherwise show the main app
                    if (showSplash) {
                        SplashScreen(onSplashFinished = { showSplash = false })
                    } else {
                        TranslationAndSpeechApp()
                    }
                }
            }
        }
    }
}

/**
 * Composable function that sets the background of the app with a full-screen image.
 */
//@Composable
//fun BakGround() {
//    Image(
//        painter = painterResource(id = R.drawable.background),
//        contentDescription = null, // No content description as this is a decorative image
//        contentScale = ContentScale.Crop, // Crop image to fit the screen
//        modifier = Modifier.fillMaxSize() // Fill the entire screen size
//    )
//}

/**
 * Composable function for the splash screen.
 * It plays a video using a VideoView and navigates to the main app once the video finishes.
 *
 * @param onSplashFinished Callback triggered when the splash video finishes.
 */
@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    val context = LocalContext.current
    val videoUri = Uri.parse("android.resource://${context.packageName}/raw/introv7")

    AndroidView(
        factory = { ctx ->
            VideoView(ctx).apply {
                setVideoURI(videoUri) // Set the URI of the splash video
                setOnCompletionListener { onSplashFinished() } // Notify when the video finishes
                start() // Start video playback
            }
        },
        modifier = Modifier.fillMaxSize() // Use full screen for the video
    )
}

/**
 * Composable function representing the main app.
 * It includes functionality for speech-to-text, text-to-speech, translation,
 * and interacting with a local database for saving translations.
 */
@Composable
fun TranslationAndSpeechApp() {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // State variables for managing input, results, and errors
    var query by remember { mutableStateOf(TextFieldValue("")) }
    var capturedText by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var recognizedText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Text-to-Speech initialization
    val tts = remember { TextToSpeech(context, null) }
    DisposableEffect(context) {
        tts.language = Locale("en", "MX") // Set TTS language to Spanish (Mexico)
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    // Speech-to-Text initialization
    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onError(error: Int) {
                    recognizedText = "Error recognizing speech: $error"
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    recognizedText = matches?.firstOrNull() ?: "No audio recognized"
                    query = TextFieldValue(recognizedText)
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    // Intent for speech recognition
    val speechIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
    }

    // Configure audio settings
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 50, 0)

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Save button
                Button(onClick = {
                    coroutineScope.launch {
                        if (resultText.isNotBlank()) {
                            val translation = TranslationEntity(
                                textEnglish = resultText,
                                textSpanish = resultText,
                                status = "Nice"
                            )
                            val db = AppDatabase.getInstance(context)
                            db.translationDao().insertTranslation(translation)
                            Toast.makeText(context, "Translation saved", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Both fields must be filled", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.database),
                        contentDescription = "Save",
                        modifier = Modifier.size(40.dp)
                    )
                }

                // Database query button
                IconButton(onClick = { /* Query the database */ }) {
                    Icon(Icons.Filled.AccountBox, contentDescription = "Database")
                }

                // Clear button
                IconButton(onClick = {
                    query = TextFieldValue(" ")
                    resultText = ""
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = "Clear",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Text input field
            item {
                Spacer(modifier = Modifier.height(32.dp))
                OutlinedTextField(
                    value = query.text,
                    onValueChange = { query = TextFieldValue(it) },
                    label = { Text("Text to translate or speak") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Button to swap texts
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    capturedText = query.text
                    val temp = query.text
                    query = TextFieldValue(resultText)
                    resultText = temp
                }) {
                    Text("Swap Texts")
                }
            }

            // Read-only translation field
            item {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = resultText,
                    onValueChange = {},
                    label = { Text("Translation") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Buttons for text-to-speech, voice recognition, and translation
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Text-to-Speech button
                        Button(onClick = {
                            if (query.text.isNotBlank()) {
                                tts.speak(query.text, TextToSpeech.QUEUE_FLUSH, null, null)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Write something to convert to speech",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }) {
                            Text("Text-to-Speech")
                        }

                        // Voice recognition button
                        Button(onClick = {
                            speechRecognizer.startListening(speechIntent)
                        }) {
                            Text("Voice")
                        }

// Translation button
                        Button(onClick = {
                            coroutineScope.launch {
                                try {
                                    val requestBody = listOf(TextRequestBody(query.text))
                                    val response = Api.service.translateText(text = requestBody)
                                    if (response.isSuccessful) {
                                        response.body()?.let { translationResponse ->
                                            resultText = translationResponse.joinToString {
                                                it.translations.firstOrNull()?.text
                                                    ?: "No translation available"
                                            }
                                        } ?: run {
                                            resultText = "Empty response from server"
                                        }
                                    } else {
                                        resultText = "Error ${response.code()}: ${response.errorBody()?.string()}"
                                    }
                                } catch (e: Exception) {
                                    resultText = "Error: ${e.localizedMessage}"
                                }
                            }
                        }) {
                            Text("Translate")
                        }                    }
                }
            }
        }
    }
}
