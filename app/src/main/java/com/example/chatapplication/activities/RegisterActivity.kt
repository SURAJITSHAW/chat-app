package com.example.chatapplication.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatapplication.LoginActivity
import com.example.chatapplication.R
import com.example.chatapplication.databinding.ActivityMainBinding
import com.example.chatapplication.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showProgress(false)

        auth = FirebaseAuth.getInstance()

        // Inside your onCreate method
        binding.loginText.setOnClickListener {
            // Handle the login click, navigate to login screen
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


        // Register button click listener
        binding.register.setOnClickListener {

//            if (validateEmailPassword()) {
//                // Proceed with registration if validation passes
//                registerUser()
//            }

            // Add a delay of 3 seconds before validating and registering the user
            showProgress(true)
            Handler(mainLooper).postDelayed({
                // After 3 seconds, execute validation and registration logic

                // Validate email and password
                if (validateEmailPassword()) {
                    // Proceed with registration if validation passes
                    registerUser()
                } else {
                    // If validation fails, stop loading
                    showProgress(false)
                }
            }, 1000)  // Delay of 3 seconds (3000 milliseconds)
        }
    }

    // Function to validate password requirements and matching
    private fun validateEmailPassword(): Boolean {
        password = binding.pass.text.toString()
        val confirmPassword = binding.confirmPass.text.toString()
        email = binding.email.text.toString()

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.error = "Please enter a valid email address"
            return false
        } else {
            binding.email.error = null // Clear error if email is valid
        }


        // Check minimum length for password confirmPass
        if (password.length < 6 || confirmPassword.length < 6) {
            binding.pass.error = "Password adn Confirm Password must be at least 6 characters"
            return false
        } else {
            binding.pass.error = null // Clear error if password length is valid
        }

        // Check if passwords match
        if (password != confirmPassword) {
            binding.confirmPass.error = "Passwords do not match"
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        } else {
            binding.confirmPass.error = null // Clear error if passwords match
        }

        return true // Validation passed
    }

    private fun showProgress(show: Boolean) {
        binding.progress.visibility = if (show) View.VISIBLE else View.GONE
        binding.register.visibility = if (show) View.GONE else View.VISIBLE
    }


    // Dummy function to handle user registration
    private fun registerUser() {
        showProgress(true)

        // Add registration logic here
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    Log.d("user details", user.toString())
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("email", user?.email)
                    intent.putExtra("uid", user?.uid)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }

//        turn off loading
        showProgress(false)
    }

}