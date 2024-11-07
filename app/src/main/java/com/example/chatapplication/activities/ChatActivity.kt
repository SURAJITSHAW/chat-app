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
import com.example.chatapplication.models.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.util.UUID

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatId: String
    private lateinit var messagesAdapter: MessagesAdapter
    private val messagesList = mutableListOf<Message>()

    private lateinit var user: User

    private lateinit var senderName: String
    private lateinit var senderId: String
    private lateinit var receiverId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

// Get the JSON string from the intent
        val userJson = intent.getStringExtra("user_data")

        if (userJson != null) {
            user = Gson().fromJson(userJson, User::class.java) // Convert JSON string back to User object
            setupToolbar()
        }

        // Initialize RecyclerView and Adapter
        messagesAdapter = MessagesAdapter(messagesList)
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = messagesAdapter

        // Fetch messages from Firestore
//        fetchMessages()
//
//        // Handle message send button click
//        binding.sendButton.setOnClickListener {
//            val messageText = binding.messageEditText.text.toString()
//            if (messageText.isNotEmpty()) {
//                sendMessage(messageText)
//                binding.messageEditText.text.clear()  // Clear input after sending
//            }
//        }
    }

    private fun setupToolbar() {
        // Set up the custom toolbar with the user details
        setSupportActionBar(binding.customToolbar)

        // Disable default title display in the toolbar
        supportActionBar?.title = null

        // Set the user's full name and username in the toolbar
        binding.toolbarFullName.text = user.fullName
        binding.toolbarUsername.text = user.userName

        // Handle the back button click
        binding.backButton.setOnClickListener {
            onBackPressed()  // Go back to the previous screen
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
