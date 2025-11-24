package com.example.reg_with_firebase

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun SignupScreen(navController: NavController) {
    // State for the email and password fields.
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    // Get the current context for showing Toasts.
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Sign Up", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.padding(16.dp))

            // Email input field.
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
            )

            Spacer(modifier = Modifier.padding(10.dp))

            // Password input field.
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                visualTransformation = PasswordVisualTransformation(),
            )

            Spacer(modifier = Modifier.padding(16.dp))

            // Sign-up button.
            Button(
                onClick = {
                    // Basic validation to ensure fields are not empty.
                    if (email.isNotBlank() && password.isNotBlank()) {
                        // Attempt to create a user with Firebase Auth.
                        Firebase.auth.createUserWithEmailAndPassword(email.trim(), password.trim())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // On success, show a message and navigate to the home screen.
                                    Toast.makeText(context, "Sign up successful!", Toast.LENGTH_SHORT).show()
                                    navController.navigate("home")
                                } else {
                                    // On failure, show a generic error message.
                                    Toast.makeText(context, "Sign up failed.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        // Prompt the user to fill in all fields.
                        Toast.makeText(context, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Up")
            }


        }

    }
}
