package com.google.mediapipe.examples.gesturerecognizer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var editTextFirstName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var radioGroupHandPreference: RadioGroup
    private lateinit var buttonSignUp: Button
    private lateinit var textViewLogin: TextView
    private lateinit var logo: ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize views
        editTextFirstName = findViewById(R.id.editTextFirstName)
        editTextLastName = findViewById(R.id.editTextLastName)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        radioGroupGender = findViewById(R.id.radioGroupGender)
        radioGroupHandPreference = findViewById(R.id.radioGroupHandPreference)
        buttonSignUp = findViewById(R.id.buttonSignUp)
        textViewLogin = findViewById(R.id.textViewLogin)

        // Set click listener for the sign up button
        buttonSignUp.setOnClickListener {
            val firstName = editTextFirstName.text.toString()
            val lastName = editTextLastName.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val selectedGenderId = radioGroupGender.checkedRadioButtonId
            val selectedHandPreferenceId = radioGroupHandPreference.checkedRadioButtonId
            val gender = findViewById<RadioButton>(selectedGenderId)?.text.toString()
            val handPreference = findViewById<RadioButton>(selectedHandPreferenceId)?.text.toString()

            // Simple validation, you can add your own logic here
            if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && selectedGenderId != -1 && selectedHandPreferenceId != -1) {
                signUp(firstName, lastName, email, password, gender, handPreference)
            } else {
                // Show error message if any field is empty
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener for the login text view to go back to the login page
        textViewLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signUp(firstName: String, lastName: String, email: String, password: String, gender: String, handPreference: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success, add user information to Firestore
                    val user = auth.currentUser
                    val userId = user?.uid
                    val userMap = hashMapOf(
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "email" to email,
                        "gender" to gender,
                        "handPreference" to handPreference,
                        "points" to 0,
                        "bestScoreOfPoints" to 0
                    )
                    // Store user data locally
                    val sharedPref = getSharedPreferences("userData", MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("firstName", firstName)
                        putString("lastName", lastName)
                        putString("email", email)
                        putString("handPreference", handPreference)
                        putInt("points", 0)
                        putInt("bestScoreOfPoints", 0)
                        apply()
                    }

                    if (userId != null) {
                        db.collection("users").document(userId).set(userMap)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
                                // Navigate to MainActivity
                                val intent = Intent(this, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error saving user information: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // If sign up fails, display a message to the user.
                    Toast.makeText(this, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
