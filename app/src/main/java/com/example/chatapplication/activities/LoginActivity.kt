package com.example.chatapplication.activities

import android.content.ContentValues.TAG
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
import com.example.chatapplication.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        showProgress(false)
        auth = FirebaseAuth.getInstance()

        // Inside your onCreate method
        binding.newUserText.setOnClickListener {
            // Handle the new user create account click, navigate to registration screen
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }


        // Register button click listener
        binding.login.setOnClickListener {
            showProgress(true)
            email = binding.email.text.toString()
            password = binding.pass.text.toString()
            if (validateEmailPassword()) {
                // Proceed with registration if validation passes
                login()
            } else {
                showProgress(false)
            }

        }
    }

    private fun login() {
        showProgress(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser

                    Toast.makeText(
                        baseContext,
                        "Logging in as ${user?.email}",
                        Toast.LENGTH_SHORT,
                    ).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun validateEmailPassword(): Boolean {
        password = binding.pass.text.toString()
        email = binding.email.text.toString()

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.error = "Please enter a valid email address"
            return false
        } else {
            binding.email.error = null // Clear error if email is valid
        }


        // Check minimum length for password confirmPass
        if (password.length < 6) {
            binding.pass.error = "Password must be at least 6 characters"
            return false
        } else {
            binding.pass.error = null // Clear error if password length is valid
        }

        return true // Validation passed
    }



    private fun showProgress(show: Boolean) {
        binding.progress.visibility = if (show) View.VISIBLE else View.GONE
        binding.login.visibility = if (show) View.GONE else View.VISIBLE
    }
}