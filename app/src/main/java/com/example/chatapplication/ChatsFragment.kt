package com.example.chatapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapplication.activities.ChatActivity
import com.example.chatapplication.adapters.ChatCardAdapter
import com.example.chatapplication.databinding.FragmentChatsBinding
import com.example.chatapplication.models.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson

class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatAdapter: ChatCardAdapter
    private val chatList = mutableListOf<Chat>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)

        fetchRecentChats()

        return binding.root

    }

    private fun fetchRecentChats() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val firestore = FirebaseFirestore.getInstance()

        Log.d("ChatListActivity", "Current User ID: $currentUserId")

        if (currentUserId != null) {
            // Query for chats where the current user is user1Id
            val chatRef1 = firestore.collection("chats")
                .whereEqualTo("user1Id", currentUserId)

            // Query for chats where the current user is user2Id
            val chatRef2 = firestore.collection("chats")
                .whereEqualTo("user2Id", currentUserId)

            // Fetch both queries asynchronously
            val chatList = mutableListOf<Chat>()

            // Fetch user1Id chats
            chatRef1.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val chat = document.toObject(Chat::class.java)
                        chatList.add(chat)
                    }
                    // After the first query finishes, fetch the second query
                    fetchUser2Chats(chatList, chatRef2)
                }
                .addOnFailureListener { e ->
                    Log.w("ChatListActivity", "Error getting chats for user1Id", e)
                }
        }
    }

    private fun fetchUser2Chats(chatList: MutableList<Chat>, chatRef2: Query) {
        chatRef2.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val chat = document.toObject(Chat::class.java)
                    chatList.add(chat)
                }
                Log.d("ChatListActivity", "Chat List: $chatList")
                // Update the RecyclerView with the merged chat list
                updateChatList(chatList)
            }
            .addOnFailureListener { e ->
                Log.w("ChatListActivity", "Error getting chats for user2Id", e)
            }
    }

    private fun updateChatList(chatList: List<Chat>) {
        binding.chatRecylerView.layoutManager = LinearLayoutManager(context)
        chatAdapter = ChatCardAdapter(chatList, { chat ->
                val gson = Gson()
                val chatJson = gson.toJson(chat) // Convert the Chat object to a JSON string

                val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                    putExtra("chat_data", chatJson)
                }
                startActivity(intent)
        }, requireContext())
        binding.chatRecylerView.adapter = chatAdapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
