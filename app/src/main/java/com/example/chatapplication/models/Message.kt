package com.example.chatapplication.models

import com.google.firebase.Timestamp

data class Message(
    val messageId: String = "",  // Unique ID for each message, can be generated with UUID
    val senderId: String = "",  // User ID of the sender
    val senderName: String = "",  // User ID of the sender
    val receiverId: String = "",  // User ID of the receiver
    val messageText: String = "",  // The text of the message
    val timestamp: Timestamp = Timestamp.now(),  // When the message was sent
    val isRead: Boolean = false  // Status to indicate if the message has been read
)

