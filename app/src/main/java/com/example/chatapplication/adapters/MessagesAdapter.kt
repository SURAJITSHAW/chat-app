package com.example.chatapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.databinding.ChatMsgItemBinding
import com.example.chatapplication.databinding.ItemReceivedMessageBinding
import com.example.chatapplication.databinding.ItemSentMessageBinding
import com.example.chatapplication.models.Message
import java.text.SimpleDateFormat
import java.util.Locale
class MessagesAdapter(
    private val messagesList: List<Message>,
    private val currentUserId: String // This will help identify if the message is from the current user
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_SENDER = 1
        const val VIEW_TYPE_RECEIVER = 2
    }

    // ViewHolder for the sender's message
    inner class SenderViewHolder(private val binding: ItemSentMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.messageText.text = message.messageText
            val timestamp = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(message.timestamp.toDate())
            binding.timestamp.text = timestamp
        }
    }

    // ViewHolder for the receiver's message
    inner class ReceiverViewHolder(private val binding: ItemReceivedMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.messageText.text = message.messageText
            val timestamp = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(message.timestamp.toDate())
            binding.timestamp.text = timestamp
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENDER -> {
                val binding = ItemSentMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SenderViewHolder(binding)
            }
            VIEW_TYPE_RECEIVER -> {
                val binding = ItemReceivedMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ReceiverViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messagesList[position]
        when (holder) {
            is SenderViewHolder -> holder.bind(message)
            is ReceiverViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messagesList.size

    override fun getItemViewType(position: Int): Int {
        // Check if the message is from the current user or the receiver
        return if (messagesList[position].senderId == currentUserId) {
            VIEW_TYPE_SENDER
        } else {
            VIEW_TYPE_RECEIVER
        }
    }
}

