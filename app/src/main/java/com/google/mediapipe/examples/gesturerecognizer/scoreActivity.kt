package com.google.mediapipe.examples.gesturerecognizer

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

data class UserData(
    val firstName: String,
    val lastName: String,
    val points: Int,
    val email: String
)

class scoreActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var listViewScores: ListView
    private lateinit var currentUserEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        firestore = FirebaseFirestore.getInstance()
        listViewScores = findViewById(R.id.listViewScores)

        val sharedPref = getSharedPreferences("userData", MODE_PRIVATE)
        currentUserEmail = sharedPref.getString("email", "Unknown") ?: "Unknown"

        fetchAllUserData()
    }

    private fun fetchAllUserData() {
        firestore.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                val userDataList = mutableListOf<UserData>()

                for (document in documents) {
                    val firstName = document.getString("firstName") ?: "Unknown"
                    val lastName = document.getString("lastName") ?: "Unknown"
                    val points = document.getLong("points")?.toInt() ?: 0
                    val email = document.getString("email") ?: "Unknown"

                    val userData = UserData(firstName, lastName, points, email)
                    userDataList.add(userData)
                }

                processUserData(userDataList)
            }
            .addOnFailureListener { exception ->
                Log.e("ScoreActivity", "Error fetching user data: $exception")
            }
    }

    private fun processUserData(userDataList: List<UserData>) {
        // Sort the userDataList by points in descending order
        val sortedUserDataList = userDataList.sortedByDescending { it.points }

        val adapter = UserDataAdapter(this, sortedUserDataList, currentUserEmail)
        listViewScores.adapter = adapter
    }

}
