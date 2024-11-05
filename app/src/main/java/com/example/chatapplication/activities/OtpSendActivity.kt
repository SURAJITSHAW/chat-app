package com.example.chatapplication.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapplication.databinding.ActivityOtpSendBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class OtpSendActivity : AppCompatActivity() {
        private lateinit var binding: ActivityOtpSendBinding
        private lateinit var auth: FirebaseAuth
        private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
        private lateinit var storedVerificationId: String
        private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
        private var mobile: String = ""

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityOtpSendBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Firebase Auth instance
            auth = FirebaseAuth.getInstance()

            // Setup Firebase callbacks
            setupCallbacks()
            showProgress(false)

            // Send OTP button click
            binding.sendotp.setOnClickListener {
                showProgress(true)
                val countryCode = binding.countryCode.selectedCountryCodeWithPlus
                mobile = countryCode + binding.mobileNumber.text.toString()

                if (!isValidMobile(mobile)) {
                    showToast("Enter a valid mobile number")
                    showProgress(false)
                    return@setOnClickListener
                }
                sendOtp(mobile)
            }
        }

        private fun isValidMobile(mobile: String): Boolean {
            return mobile.isNotEmpty() && mobile.length >= 10
        }

        private fun sendOtp(mobile: String) {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(mobile)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
            showToast("Sending OTP to $mobile")
            navigateToVerificationScreen()
        }

        private fun setupCallbacks() {
            callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.d("OtpSendActivity", "onVerificationCompleted:$credential")
                    // Use credential to automatically sign in if possible
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.w("OtpSendActivity", "onVerificationFailed", e)
                    showToast("Verification failed: ${e.message}")
                    showProgress(false)
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    Log.d("OtpSendActivity", "onCodeSent:$verificationId")
                    storedVerificationId = verificationId
                    resendToken = token
//                    navigateToVerificationScreen()
                }
            }
        }

        private fun navigateToVerificationScreen() {
            val intent = Intent(this, OtpVerificationActivity::class.java).apply {
//                putExtra("mobile", mobile)
//                putExtra("verificationId", storedVerificationId)
//                putExtra("token", resendToken)
            }
            startActivity(intent)
            showProgress(false)
            finish()
        }

        private fun showProgress(show: Boolean) {
            binding.progress.visibility = if (show) View.VISIBLE else View.GONE
            binding.sendotp.visibility = if (show) View.GONE else View.VISIBLE
        }

        private fun showToast(message: String) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
