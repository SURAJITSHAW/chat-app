package com.example.chatapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.databinding.ChatMsgItemBinding
import com.example.chatapplication.models.Message
import java.text.SimpleDateFormat
import java.util.Locale

class MessagesAdapter(private val messagesList: List<Message>) :
    RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(private val binding: ChatMsgItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.senderName.text = message.senderName  // Display sender's name
            binding.messageText.text = message.messageText  // Display the message text

            // Format the timestamp to show the time
            val timestamp = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(message.timestamp.toDate())

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ChatMsgItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messagesList[position]
        holder.bind(message)
    }

    override fun getItemCount() = messagesList.size
}
