package com.example.chatapplication.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatapplication.R
import com.example.chatapplication.databinding.ActivityProfileBinding
import com.example.chatapplication.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        showProgress(false)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val email = intent.getStringExtra("email")
        Toast.makeText(this, email, Toast.LENGTH_SHORT).show()

        binding.updateBtn.setOnClickListener {
            showProgress(true)

            val fullName = binding.fullName.text.toString().trim()
            val userName = binding.userName.text.toString().trim()

            if (email != null) {
                if (fullName.isEmpty() || userName.isEmpty() || email.isEmpty()) {
                    showProgress(false)

                    showToast("Please enter your details properly!")
                    return@setOnClickListener
                }
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }


            // Check if the userName is unique
            checkUsernameUnique(userName) { isUnique ->
                if (isUnique) {
                    saveUserProfile(fullName, userName, email!!)
                } else {
                    Toast.makeText(this, "Username already taken, try another.", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    // Function to check if the userName already exists
    private fun checkUsernameUnique(userName: String, callback: (Boolean) -> Unit) {
        firestore.collection("users")
            .whereEqualTo("userName", userName).get()
            .addOnSuccessListener { documents ->
                callback(documents.isEmpty) // True if no documents, hence unique
            }
            .addOnFailureListener { e ->
                Log.w("ProfileActivity", "Error checking userName: ", e)
                callback(false)
            }
    }
    // Function to save the user profile to Firestore
    private fun saveUserProfile(fullName: String, userName: String, email: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val user = User(
                email = email,
                userName = userName,
                fullName = fullName,
                uid = currentUser.uid
            )

            // Save user to Firestore
            firestore.collection("users").document(currentUser.uid)
                .set(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    showProgress(false)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()  // Go back to the previous activity
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error saving profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showProgress(show: Boolean) {
        binding.progress.visibility = if (show) View.VISIBLE else View.GONE
        binding.updateBtn.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}