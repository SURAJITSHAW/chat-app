package com.example.chatapplication.models

import com.google.firebase.Timestamp


data class Chat(
    val chatId: String = "",
    val user1Name: String = "",
    val user2Name: String = "",
    val lastMessage: String = "",
    val lastMessageTimestamp: Timestamp? = null,
    val isRead: Boolean = false,
)

