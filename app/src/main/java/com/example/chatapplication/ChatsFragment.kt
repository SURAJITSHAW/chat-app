package com.example.chatapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.adapters.ChatAdapter
import com.example.chatapplication.databinding.FragmentChatsBinding
import com.example.chatapplication.models.Chat

class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatAdapter: ChatAdapter
    private val chatList = listOf(
        Chat(R.drawable.my_dp, "Surajit Shaw"),
        Chat(R.drawable.tepi, "tepi"),
        Chat(R.drawable.janu, "Janu💜")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)

        // Set up the RecyclerView
        binding.chatRecylerView.layoutManager = LinearLayoutManager(context)
        chatAdapter = ChatAdapter(chatList) { chat ->
            // Handle chat click here
        }
        binding.chatRecylerView.adapter = chatAdapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}