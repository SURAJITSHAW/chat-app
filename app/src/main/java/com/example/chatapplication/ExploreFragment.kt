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
import com.example.chatapplication.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

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
            // Check if a chat already exists
            val firestore = FirebaseFirestore.getInstance()
            val chatRef = firestore.collection("chats")

            val query1 = chatRef
                .whereEqualTo("user1Id", currentUserId)
                .whereEqualTo("user2Id", user.uid)

            val query2 = chatRef
                .whereEqualTo("user1Id", user.uid)
                .whereEqualTo("user2Id", currentUserId)

            // Get results from both queries
            val combinedQuery = query1.get().addOnSuccessListener { documents1 ->
                if (documents1.isEmpty) {
                    query2.get().addOnSuccessListener { documents2 ->
                        if (documents2.isEmpty) {
                            // No chat exists, so create a new chat document
                            createNewChatDocument(currentUserId, user)
                        }
                    }
                    }
                    // Now pass user data to the ChatActivity
                    val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                    putExtra("user_data", userJson)
                }
                startActivity(intent)
            }
        }
    }

    private fun createNewChatDocument(currentUserId: String, user: User) {
        val firestore = FirebaseFirestore.getInstance()

        val fullName = context?.getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
            ?.getString("fullName", "")
            ?: ""


        // Create a new chat document with initial empty messages or metadata
        val newChat = hashMapOf(
            "user1Id" to currentUserId,
            "user2Id" to user.uid,
            "user1Name" to fullName,
            "user2Name" to user.fullName,
            "lastMessage" to "Test last msg",
            "lastMessageTimestamp" to System.currentTimeMillis(),
            "messages" to mutableListOf<String>()  // This can hold the message IDs or a message list
        )

        // Add the new chat document to the "chats" collection
        firestore.collection("chats")
            .add(newChat)
            .addOnSuccessListener { documentReference ->
                Log.d("Chat", "Chat created successfully with ID: ${documentReference.id}")
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
