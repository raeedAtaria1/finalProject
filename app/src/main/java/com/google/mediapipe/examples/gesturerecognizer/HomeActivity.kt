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
import android.widget.ImageView
class HomeActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Accessing views using findViewById
        val imageViewMedalScores = findViewById<ImageView>(R.id.imageView2)
        imageViewMedalScores.setOnClickListener {
            // Launch ScoreActivity here
            val intent = Intent(this, scoreActivity::class.java)
            startActivity(intent)
        }
        val startButton: Button = findViewById(R.id.startButton)
        startButton.setOnClickListener {
            // Create an Intent to start MainActivity
            val intent = Intent(this@HomeActivity, MainActivity::class.java)
            startActivity(intent)
        }
        // Accessing views using findViewById
        val imageViewPeofile = findViewById<ImageView>(R.id.imageView3)
        imageViewPeofile.setOnClickListener {
            // Launch ScoreActivity here
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }


    }
}
