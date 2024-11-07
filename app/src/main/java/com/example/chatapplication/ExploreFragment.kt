package com.example.chatapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapplication.activities.ChatActivity
import com.example.chatapplication.adapters.ExploreAdapter
import com.example.chatapplication.databinding.FragmentExploreBinding
import com.example.chatapplication.models.Chat
import com.example.chatapplication.models.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.util.UUID

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    private lateinit var exploreAdapter: ExploreAdapter
    private val userList = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)

        setupRecyclerView()
        fetchUsersFromFirestore()

        return binding.root
    }

    private fun setupRecyclerView() {
        // Initialize the adapter with an empty list
        exploreAdapter = ExploreAdapter(userList) { user ->
            // Handle message icon click, e.g., open chat screen
            openChatScreen(user)
        }

        binding.chatRecylerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = exploreAdapter
        }
    }

    private fun fetchUsersFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users") // Make sure your collection name matches Firestore
            .get()
            .addOnSuccessListener { documents ->
                userList.clear()
                for (document in documents) {
                    val user = document.toObject(User::class.java)
                    if (user.uid == FirebaseAuth.getInstance().currentUser?.uid){
                        continue
                    }
                    userList.add(user)
                }
                exploreAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("ExploreFragment", "Error getting documents: ", exception)
            }
    }

    private fun openChatScreen(user: User) {
        val gson = Gson()
        val userJson = gson.toJson(user) // Convert the User object to a JSON string

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            val firestore = FirebaseFirestore.getInstance()
            val chatRef = firestore.collection("chats")

            val query1 = chatRef
                .whereEqualTo("user1Id", currentUserId)
                .whereEqualTo("user2Id", user.uid)

            val query2 = chatRef
                .whereEqualTo("user1Id", user.uid)
                .whereEqualTo("user2Id", currentUserId)

            Log.d("openChatScreen", "Current user ID: $currentUserId, Target user ID: ${user.uid}")

            query1.get().addOnSuccessListener { documents1 ->
                Log.d("openChatScreen", "Query 1 (user1Id: $currentUserId, user2Id: ${user.uid}) returned ${documents1.size()} documents.")
                if (documents1.isEmpty) {
                    Log.d("openChatScreen", "No existing chat found with the current user as user1, checking with user2.")
                    query2.get().addOnSuccessListener { documents2 ->
                        Log.d("openChatScreen", "Query 2 (user1Id: ${user.uid}, user2Id: $currentUserId) returned ${documents2.size()} documents.")
                        if (documents2.isEmpty) {
                            Log.d("openChatScreen", "No existing chat found with the current user as user2, creating a new chat.")
                            // No chat exists, so create a new chat document
                            createNewChatDocument(currentUserId, user)
                        } else {
                            Log.d("openChatScreen", "Chat found in Query 2, opening existing chat.")
                            // Chat exists, pass the existing chat to ChatActivity
                            val existingChat = documents2.documents[0].toObject(Chat::class.java)
                            openChatActivity(existingChat)
                        }
                    }
                } else {
                    Log.d("openChatScreen", "Chat found in Query 1, opening existing chat.")
                    // Chat exists, pass the existing chat to ChatActivity
                    val existingChat = documents1.documents[0].toObject(Chat::class.java)
                    openChatActivity(existingChat)
                }
            }
        } else {
            Log.w("openChatScreen", "Current user ID is null, cannot open chat.")
        }
    }

    private fun openChatActivity(chat: Chat?) {
        val gson = Gson()
        val chatJson = gson.toJson(chat) // Convert the Chat object to a JSON string

        val intent = Intent(requireContext(), ChatActivity::class.java).apply {
            putExtra("chat_data", chatJson) // Pass chat data to the activity
        }
        startActivity(intent)
    }

    private fun createNewChatDocument(currentUserId: String, user: User) {
        val firestore = FirebaseFirestore.getInstance()

        val fullName = context?.getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
            ?.getString("fullName", "")
            ?: ""

        // Generate custom chatId using UUID
        val chatId = UUID.randomUUID().toString()


// Use System.currentTimeMillis() to get the current timestamp
        val currentTimestamp = System.currentTimeMillis()

        // Create a new chat document with initial empty messages or metadata
        val newChat = hashMapOf(
            "chatId" to chatId,
            "user1Id" to currentUserId,
            "user2Id" to user.uid,
            "user1Name" to fullName,
            "user2Name" to user.fullName,
            "lastMessage" to "Test last msg",
            "lastMessageTimestamp" to java.sql.Timestamp(currentTimestamp),
            "messages" to mutableListOf<String>()  // This can hold the message IDs or a message list
        )

        // Add the new chat document to the "chats" collection with custom document ID
        firestore.collection("chats")
            .document(chatId)  // Use the generated chatId as the document ID
            .set(newChat)  // Use .set() instead of .add() to ensure you use custom ID
            .addOnSuccessListener {


                // Open the newly created chat screen after the document is successfully created
                val newChatObject = Chat(chatId, currentUserId, user.uid, fullName, user.fullName)
                openChatActivity(newChatObject)
                Log.d("Chat", "Chat created successfully with ID: $chatId")
            }
            .addOnFailureListener { e ->
                Log.w("Chat", "Error adding chat document", e)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
