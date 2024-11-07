package com.example.chatapplication

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

        val intent = Intent(requireContext(), ChatActivity::class.java).apply {
            putExtra("user_data", userJson)
        }
        startActivity(intent)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
