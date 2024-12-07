package com.example.newfinalproiect.ui.theme.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.newfinalproiect.MainActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * Composable function that displays the Sign-In screen with fields for email and password.
 * It provides an interface to authenticate users using Firebase Authentication.
 *
 * @param context Context used to show Toast messages and navigate between activities.
 * @param modifier Modifier used to customize the layout (optional).
 */
@Composable
fun SignInScreen(context: Context, modifier: Modifier = Modifier) {
    // State to hold user input for email and password
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }  // Not utilized in the current implementation
    var error by remember { mutableStateOf<String?>(null) }  // Not utilized in the current implementation

    // Get the software keyboard controller to hide the keyboard after login attempt
    val keyboardController = LocalSoftwareKeyboardController.current

    // Column layout for the sign-in form
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),  // Adds padding around the Column
        verticalArrangement = Arrangement.Center,  // Centers the content vertically
        horizontalAlignment = Alignment.CenterHorizontally  // Centers the content horizontally
    ) {
        // Email input field
        TextField(
            value = email,  // Holds the current value of email
            onValueChange = { email = it },  // Updates email value when user types
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),  // Adds padding around the TextField
            label = { Text("Email") },  // Label for the email field
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)  // Specifies email input type
        )

        // Password input field
        TextField(
            value = password,  // Holds the current value of password
            onValueChange = { password = it },  // Updates password value when user types
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),  // Adds padding around the TextField
            label = { Text("Password") },  // Label for the password field
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)  // Specifies password input type
        )

        // Sign In button
        Button(
            onClick = { performsSignIn(email, password, context, keyboardController) },  // Trigger sign-in function
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)  // Adds padding around the Button
        ) {
            Text("Sign In")  // Text displayed on the Button
        }
    }
}

/**
 * This function handles the Firebase Authentication sign-in process.
 *
 * @param email The email entered by the user.
 * @param password The password entered by the user.
 * @param context The context used for showing Toast messages and navigating between activities.
 * @param keyboardController The controller used to hide the software keyboard after the sign-in attempt.
 */
fun performsSignIn(
    email: String,
    password: String,
    context: Context,
    keyboardController: SoftwareKeyboardController?
) {
    // Get an instance of Firebase Authentication
    val auth = FirebaseAuth.getInstance()

    // Attempt to sign in with the provided email and password
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->  // Callback when sign-in process is complete
            if (task.isSuccessful) {  // If sign-in is successful
                // Show a success message
                Toast.makeText(context, "Sign In Successful", Toast.LENGTH_SHORT).show()

                // Create an Intent to navigate to MainActivity
                val intent = Intent(context, MainActivity::class.java)

                // Add flags to the Intent to indicate this is a new task
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                // Pass the current user ID to MainActivity
                intent.putExtra("userID", auth.currentUser?.uid)

                // Start MainActivity
                context.startActivity(intent)

            } else {  // If sign-in fails
                // Show an error message
                Toast.makeText(context, "Sign In Failed", Toast.LENGTH_SHORT).show()
            }
            // Hide the keyboard after sign-in attempt
            keyboardController?.hide()
        }
}