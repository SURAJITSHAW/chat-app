package com.example.chatapplication.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapplication.R
import com.example.chatapplication.adapters.MessagesAdapter
import com.example.chatapplication.databinding.ActivityChatBinding
import com.example.chatapplication.models.Message
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatId: String
    private lateinit var messagesAdapter: MessagesAdapter
    private val messagesList = mutableListOf<Message>()


    private lateinit var senderName: String
    private lateinit var senderId: String
    private lateinit var receiverId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the chatId from the Intent
        chatId = intent.getStringExtra("CHAT_ID") ?: ""
        senderName = intent.getStringExtra("SENDER_NAME").orEmpty()
        senderId = intent.getStringExtra("SENDER_ID").orEmpty()
        receiverId = intent.getStringExtra("RECEIVER_ID").orEmpty()

        // Initialize RecyclerView and Adapter
        messagesAdapter = MessagesAdapter(messagesList)
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = messagesAdapter

        // Fetch messages from Firestore
        fetchMessages()

        // Handle message send button click
        binding.sendButton.setOnClickListener {
            val messageText = binding.messageEditText.text.toString()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                binding.messageEditText.text.clear()  // Clear input after sending
            }
        }
    }

    private fun fetchMessages() {
        val db = FirebaseFirestore.getInstance()
        val messagesRef = db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")

        messagesRef.addSnapshotListener { snapshots, exception ->
            if (exception != null) {
                Toast.makeText(this, "Error fetching messages", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (snapshots != null) {
                messagesList.clear()  // Clear the previous list
                for (doc in snapshots) {
                    val message = doc.toObject(Message::class.java)
                    messagesList.add(message)
                }
                messagesAdapter.notifyDataSetChanged()  // Notify adapter to update UI
            }
        }
    }

    private fun sendMessage(messageText: String) {
        val db = FirebaseFirestore.getInstance()
        val newMessage = Message(
            messageId = UUID.randomUUID().toString(),
            senderId = senderId,  // Get the current user ID
            senderName = senderName,  // Get the current user name
            receiverId = receiverId,  // Get the receiver's user ID
            messageText = messageText,
            timestamp = Timestamp.now(),
            isRead = false
        )

        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .add(newMessage)
            .addOnSuccessListener {
                // Handle success, message sent
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
            }
    }
}
