package com.google.mediapipe.examples.gesturerecognizer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var textViewSignUp: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize views
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        textViewSignUp = findViewById(R.id.textViewSignUp)

        // Set click listener for the login button
        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()

            // Simple validation, you can add your own logic here
            if (username.isNotEmpty() && password.isNotEmpty()) {
                signIn(username, password)
            } else {
                // Show error message if username or password is empty
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener for the sign-up text view
        textViewSignUp.setOnClickListener {
            // Navigate to SignUpActivity
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, fetch user data from Firestore
                    val user = auth.currentUser
                    user?.let {
                        fetchUserData(it.uid)
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Email/password is wrong", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun fetchUserData(userId: String) {
        val userDocRef = firestore.collection("users").document(userId)
        userDocRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val firstName = document.getString("firstName")
                val lastName = document.getString("lastName")
                val email = document.getString("email")
                val handPreference = document.getString("handPreference")
                val points = document.getLong("points")?.toInt() ?: 0
                val bestScoreOfPoints = document.getLong("bestScoreOfPoints")?.toInt() ?: 0


                // Store user data locally
                val sharedPref = getSharedPreferences("userData", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("firstName", firstName)
                    putString("lastName", lastName)
                    putString("email", email)
                    putString("handPreference", handPreference)
                    putInt("points", points)
                    putInt("bestScoreOfPoints",bestScoreOfPoints)
                    apply()
                }

                // Navigate to MainActivity
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish() // Finish the LoginActivity to prevent user from going back
            } else {
                Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error fetching user data: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
