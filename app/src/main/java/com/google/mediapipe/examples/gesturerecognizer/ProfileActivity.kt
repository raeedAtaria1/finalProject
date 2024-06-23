package com.google.mediapipe.examples.gesturerecognizer

import android.content.Intent
import android.content.SharedPreferences
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

class ProfileActivity : AppCompatActivity() {

    private lateinit var editTextFirstName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var textViewPoints: TextView
    private lateinit var textViewBestScore: TextView
    private lateinit var radioGroupHandPreference: RadioGroup
    private lateinit var radioButtonLeft: RadioButton
    private lateinit var radioButtonRight: RadioButton
    private lateinit var buttonSave: Button
    private lateinit var buttonLogout: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        // Accessing views using findViewById
        val imageViewPeofile = findViewById<ImageView>(R.id.imageView3)
        imageViewPeofile.setOnClickListener {
            // Launch ScoreActivity here
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        val imageViewMainPageButton1 = findViewById<ImageView>(R.id.imageView)
        imageViewMainPageButton1.setOnClickListener {
            // Launch ScoreActivity here
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        // Accessing views using findViewById
        val imageViewMedalScores = findViewById<ImageView>(R.id.imageView2)
        imageViewMedalScores.setOnClickListener {
            // Launch ScoreActivity here
            val intent = Intent(this, scoreActivity::class.java)
            startActivity(intent)
        }


        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize views
        editTextFirstName = findViewById(R.id.editTextFirstName)
        editTextLastName = findViewById(R.id.editTextLastName)
        textViewPoints = findViewById(R.id.textViewPoints)
        textViewBestScore = findViewById(R.id.textViewBestScore)
        radioGroupHandPreference = findViewById(R.id.radioGroupHandPreference)
        radioButtonLeft = findViewById(R.id.radioButtonLeft)
        radioButtonRight = findViewById(R.id.radioButtonRight)
        buttonSave = findViewById(R.id.buttonSave)
        buttonLogout = findViewById(R.id.buttonLogout)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE)

        // Load user data from SharedPreferences
        val firstName = sharedPreferences.getString("firstName", "")
        val lastName = sharedPreferences.getString("lastName", "")
        val handPreference = sharedPreferences.getString("handPreference", "")
        val points = sharedPreferences.getInt("points", 0)
        val bestScoreOfPoints = sharedPreferences.getInt("bestScoreOfPoints", 0)

        // Display user data
        editTextFirstName.setText(firstName)
        editTextLastName.setText(lastName)
        if (handPreference == "Left") {
            radioButtonLeft.isChecked = true
        } else {
            radioButtonRight.isChecked = true
        }
        textViewPoints.text = "Points: $points"
        textViewBestScore.text = "Best Score: $bestScoreOfPoints"

        // Set click listener for save button
        buttonSave.setOnClickListener {
            saveChanges()
        }

        // Set click listener for logout button
        buttonLogout.setOnClickListener {
            logout()
        }
    }

    private fun saveChanges() {
        // Get user input
        val newFirstName = editTextFirstName.text.toString()
        val newLastName = editTextLastName.text.toString()
        val newHandPreference = if (radioButtonLeft.isChecked) "Left" else "Right"

        // Update local SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("firstName", newFirstName)
        editor.putString("lastName", newLastName)
        editor.putString("handPreference", newHandPreference)
        editor.apply()

        // Update Firestore document
        val user = auth.currentUser
        user?.let { firebaseUser ->
            val userId = firebaseUser.uid
            val userDocRef = firestore.collection("users").document(userId)

            userDocRef.update(
                mapOf(
                    "firstName" to newFirstName,
                    "lastName" to newLastName,
                    "handPreference" to newHandPreference
                )
            ).addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to update profile: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logout() {
        // Clear user data from SharedPreferences
        sharedPreferences.edit().clear().apply()

        // Sign out from Firebase Authentication
        auth.signOut()

        // Navigate back to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Finish the ProfileActivity
    }
}
