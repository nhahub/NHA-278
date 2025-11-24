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
import androidx.compose.material3.TextButton
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
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(navController: NavController) {
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
            Text("Login", style = MaterialTheme.typography.headlineMedium)

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

            // Login button.
            Button(
                onClick = {
                    // Basic validation to ensure fields are not empty.
                    if (email.isNotBlank() && password.isNotBlank()) {
                        // Attempt to sign in a user with Firebase Auth.
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.trim(), password.trim())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // On success, show a message and navigate to the home screen.
                                    Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                                    // Navigate and clear the back stack to prevent going back to the login screen.
                                    navController.navigate("home") {
                                        popUpTo(navController.graph.startDestinationId) {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    }
                                } else {
                                    // On failure, show a more specific error message.
                                    Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    } else {
                        // Prompt the user to fill in all fields.
                        Toast.makeText(context, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log In")
            }

            Spacer(modifier = Modifier.padding(8.dp))

            // Text button to navigate to the sign-up screen.
            TextButton(onClick = { navController.navigate("signup") }) {
                Text("Don\'t have an account? Sign Up")
            }

            TextButton(onClick = { navController.navigate("anonymous") }) {
                Text("Continue as Guest")
            }
        }
    }
}
