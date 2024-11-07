package com.example.chatapplication.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapplication.R
import com.example.chatapplication.adapters.MessagesAdapter
import com.example.chatapplication.databinding.ActivityChatBinding
import com.example.chatapplication.models.Chat
import com.example.chatapplication.models.Message
import com.example.chatapplication.models.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.util.UUID


class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatAdapter: MessagesAdapter
    private lateinit var chatId: String
    private lateinit var currentUserId: String
    private lateinit var chat: Chat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the chat data passed from the previous screen
        val chatJson = intent.getStringExtra("chat_data")
        chat = Gson().fromJson(chatJson, Chat::class.java)

        Log.d("ChatActivity", "Received chat data: $chat")
        currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Set up the toolbar with user information
        setupToolbar(chat)

        // Set up RecyclerView for displaying messages
        setupRecyclerView()

        // Set up the send button click listener
        binding.sendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun setupToolbar(chat: Chat) {
        // Use the other user's name for the toolbar title
        val otherUserName = if (chat.user1Id == currentUserId) chat.user2Name else chat.user1Name
        supportActionBar?.title = null
        binding.toolbarFullName.text = otherUserName
    }

    private fun setupRecyclerView() {
        // Fetch chat messages from Firestore
        val firestore = FirebaseFirestore.getInstance()
        val chatRef = firestore.collection("chats").document(chat.chatId)

        chatRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("ChatActivity", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                // Get the list of messages and convert them to Message objects
                val messages = snapshot.get("messages") as? List<Map<String, Any>> ?: emptyList()

                // Convert the list of maps to a list of Message objects
                val messageList = messages.map { messageData ->
                    Message(
                        messageId = messageData["messageId"] as? String ?: "",
                        senderId = messageData["senderId"] as? String ?: "",
                        receiverId = messageData["receiverId"] as? String ?: "",
                        messageText = messageData["messageText"] as? String ?: "",
                        timestamp = messageData["timestamp"] as? Timestamp ?: Timestamp.now()
                    )
                }

                // Pass the messageList to the adapter
                chatAdapter = MessagesAdapter(messageList, currentUserId)
                binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.chatRecyclerView.adapter = chatAdapter
            }
        }
    }

    private fun sendMessage() {
        val messageText = binding.messageEditText.text.toString()
        if (messageText.isNotEmpty()) {
            val firestore = FirebaseFirestore.getInstance()
            val chatRef = firestore.collection("chats").document(chat.chatId)

            // Create a new Message object
            val newMessage = Message(
                messageId = UUID.randomUUID().toString(),
                senderId = currentUserId,
                receiverId = if (chat.user1Id == currentUserId) chat.user2Id else chat.user1Id,
                messageText = messageText,
                timestamp = Timestamp.now()
            )

            // Start a Firestore transaction
            firestore.runTransaction { transaction ->
                // Fetch the chat document
                val snapshot = transaction.get(chatRef)
                if (snapshot.exists()) {
                    // Add the new message to the "messages" field
                    transaction.update(chatRef, "messages", FieldValue.arrayUnion(newMessage))

                    // Update the lastMessage and lastMessageTimestamp fields
                    transaction.update(chatRef, "lastMessage", messageText)
                    transaction.update(chatRef, "lastMessageTimestamp", Timestamp.now())
                }
                null
            }.addOnSuccessListener {
                binding.messageEditText.text.clear() // Clear input field after sending
            }.addOnFailureListener { e ->
                Log.w("ChatActivity", "Error sending message", e)
            }
        }
    }


}

