package com.example.chatapplication.models

data class user(
    val id: Int = 0, // Default value; manage incrementing logic separately
    val profileImageUrl: String, // URL for the image from Firebase
    val fullName: String,
    val userName: String,
    val bio: String,
    val email: String,
    val password: String
)
