package com.example.chatapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.R
import com.example.chatapplication.databinding.ChatItemBinding
import com.example.chatapplication.models.Chat
import java.text.SimpleDateFormat
import java.util.Locale

class ChatCardAdapter(
    private val chatList: List<Chat>,
    private val onChatClick: (Chat) -> Unit
) : RecyclerView.Adapter<ChatCardAdapter.ChatViewHolder>() {

    class ChatViewHolder(private val binding: ChatItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Chat, onChatClick: (Chat) -> Unit) {
            // For now, we'll use a placeholder image or load it from URL if available
            binding.profileImage.setImageResource(R.drawable.placeholder) // You can use an image loading library like Glide or Picasso here

            // Set the full name of the other user
            val otherUserName = if (chat.user1Name != "YourUserName") chat.user1Name else chat.user2Name
            binding.fullName.text = otherUserName

            // Set the last message text
            binding.lastMessage.text = if (chat.lastMessage.isNotEmpty()) chat.lastMessage else "No messages yet"

            // Format and display the timestamp of the last message
            // Format and display the timestamp of the last message
            val timestampFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            binding.timestamp.text = chat.lastMessageTimestamp?.toDate()?.let {
                timestampFormat.format(it)
            } ?: "Unknown time"


            // Show unread indicator if the message is unread
            binding.unreadIndicator.visibility = if (chat.isRead) View.GONE else View.VISIBLE

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
        holder.bind(chat, onChatClick)
    }

    override fun getItemCount() = chatList.size
}
