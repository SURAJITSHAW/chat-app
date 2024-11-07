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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

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

        // Set up RecyclerView
        binding.chatRecylerView.layoutManager = LinearLayoutManager(context)
        chatAdapter = ChatCardAdapter(chatList) { chat ->
            // Handle chat click
            Toast.makeText(context, "Clicked on ${chat.user1Name}", Toast.LENGTH_SHORT).show()
            val chatIds = chat.chatId.split("_")

            val intent = Intent(activity, ChatActivity::class.java)
            intent.putExtra("CHAT_ID", chat.chatId)
            intent.putExtra("SENDER_NAME", chat.user1Name)
            intent.putExtra("SENDER_ID", chatIds[1])
            intent.putExtra("RECEIVER_ID", chatIds[0])
            startActivity(intent)
        }
        binding.chatRecylerView.adapter = chatAdapter

        // Fetch chats from Firestore
        fetchChatsFromFirestore()

        return binding.root
    }

    private fun fetchChatsFromFirestore() {
        db.collection("chats")
            .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                chatList.clear()
                for (document in documents) {
                    val chat = document.toObject(Chat::class.java)
                    chatList.add(chat)
                }
                chatAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("ChatsFragment", "Error fetching chats", e)
                Toast.makeText(context, "Failed to load chats", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
