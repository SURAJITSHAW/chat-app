package com.example.chatapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.R
import com.example.chatapplication.databinding.ExploreItemBinding
import com.example.chatapplication.models.User

class ExploreAdapter(
    private val userList: List<User>,
    private val onMessageClick: (User) -> Unit // Lambda for handling message icon click
) : RecyclerView.Adapter<ExploreAdapter.ExploreViewHolder>() {

    // ViewHolder class that uses View Binding
    inner class ExploreViewHolder(val binding: ExploreItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.apply {
                fullName.text = user.fullName
                username.text = "@${user.userName}"
                bio.text = user.bio
                // Set a default or placeholder profile image, you may load from URL with an image library here
                profileImage.setImageResource(R.drawable.cartoon_avatar) // Use actual image if available
                messageIcon.setOnClickListener { onMessageClick(user) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreViewHolder {
        val binding = ExploreItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExploreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExploreViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int = userList.size
}
