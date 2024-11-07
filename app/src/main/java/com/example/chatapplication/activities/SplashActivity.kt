package com.example.chatapplication.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatapplication.R
import com.example.chatapplication.databinding.ActivitySplashBinding
import com.example.chatapplication.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Check if a user is logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // If user is logged in, fetch and store their data
            fetchUserDataAndStoreInPrefs(currentUser.uid)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                // If no user is logged in, redirect to RegistrationActivity
                startActivity(Intent(this, RegisterActivity::class.java))
                finish()  // Close splash screen
            }, 2000)  // Optional delay for splash screen
        }
    }

    private fun fetchUserDataAndStoreInPrefs(userId: String) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val username = document.getString("userName") ?: ""
                    val fullName = document.getString("fullName") ?: ""
                    val bio = document.getString("bio") ?: ""
                    val email = document.getString("email") ?: ""

                    // Save the fetched data into SharedPreferences
                    val user = User(userId, email,username, fullName,  bio)
                    saveUserDataToPrefs(user)

                    // Proceed to the next screen after data is saved
                    goToHomeScreen()
                } else {
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    goToHomeScreen()  // Proceed anyway to home
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                goToHomeScreen()  // Proceed anyway if there's an error
            }
    }

    private fun saveUserDataToPrefs(user: User) {
        val sharedPref = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("userName", user.userName)
            putString("fullName", user.fullName)
            putString("uid", user.uid)
            putString("email", user.email)
            putString("bio", user.bio)
            apply()
        }
    }

    private fun goToHomeScreen() {
        // Navigate to HomeActivity after the splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()  // Close splash screen
        }, 1000)  // Optional delay for splash screen
    }
}
