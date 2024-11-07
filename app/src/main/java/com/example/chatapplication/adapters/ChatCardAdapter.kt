package com.example.chatapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.R
import com.example.chatapplication.databinding.ChatItemBinding
import com.example.chatapplication.models.Chat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatCardAdapter(
    private val chatList: List<Chat>,
    private val onChatClick: (Chat) -> Unit,
    private val context: Context // Pass context to access SharedPreferences
) : RecyclerView.Adapter<ChatCardAdapter.ChatViewHolder>() {

    class ChatViewHolder(private val binding: ChatItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Chat, onChatClick: (Chat) -> Unit, context: Context) {

            // Retrieve the username from SharedPreferences
            val fullName = context.getSharedPreferences("UserProfile", Context.MODE_PRIVATE).getString("fullName", "") ?: ""
            // Set the full name of the other user
            val otherUserName = if (chat.user1Name != fullName) chat.user1Name else chat.user2Name
            binding.fullName.text = otherUserName

            binding.profileImage.setImageResource(R.drawable.cartoon_avatar)

            // Set the last message text
            binding.lastMessage.text = if (chat.lastMessage.isNotEmpty()) chat.lastMessage else "No messages yet"

            // Format and display the timestamp of the last message
            val timestampFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

// Convert the Long timestamp to Date
            val date = chat.lastMessageTimestamp?.let { Date(it) }

// Format the Date
            binding.timestamp.text = date?.let { timestampFormat.format(it) } ?: "Unknown time"


            // Set click listener for the entire item
            binding.root.setOnClickListener { onChatClick(chat) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]
        holder.bind(chat, onChatClick, context)
    }

    override fun getItemCount() = chatList.size
}
