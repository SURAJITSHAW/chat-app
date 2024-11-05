package com.example.chatapplication.adapters
// ChatAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.databinding.ChatItemBinding
import com.example.chatapplication.models.Chat

class ChatAdapter(private val chatList: List<Chat>, private val onChatClick: (Chat) -> Unit) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(private val binding: ChatItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat, onChatClick: (Chat) -> Unit) {
            binding.profileImage.setImageResource(chat.imageResId)
            binding.fullName.text = chat.fullName
            binding.tapToChat.setOnClickListener { onChatClick(chat) }
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
