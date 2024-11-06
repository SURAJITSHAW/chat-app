package com.example.chatapplication.models

data class User(
    val uid: String = "",
    val email: String = "",
    val userName: String = "",
    val fullName: String = "",
    val bio: String = "Hey there! I am using Social App",
)
