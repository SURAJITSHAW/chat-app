package com.example.chatapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.chatapplication.activities.LoginActivity
import com.example.chatapplication.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private var isEditing = false  // Flag to check editing state

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Fetch user data directly from Firestore
        fetchUserDataFromFirestore()

        // Set up button clicks
        binding.editButton.setOnClickListener {
            toggleEditSave()
        }

        binding.resetPasswordButton.setOnClickListener {
            Toast.makeText(requireContext(), "Reset Password not implemented yet :(", Toast.LENGTH_SHORT).show()
        }

        binding.logoutButton.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.logoutButton.visibility = View.GONE
            auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
    }

    private fun fetchUserDataFromFirestore() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("fullName")
                        val username = document.getString("userName")
                        val bio = document.getString("bio")
                        val email = document.getString("email")

                        // Set values to the fields
                        binding.nameEditText.setText(name)
                        binding.usernameEditText.setText(username)
                        binding.bioEditText.setText(bio)
                        binding.emailTextView.text = email
                    } else {
                        Toast.makeText(requireContext(), "No user data found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error fetching user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleEditSave() {
        if (isEditing) {
            // Save state - Disable editing
            binding.nameEditText.isEnabled = false
            binding.usernameEditText.isEnabled = false
            binding.bioEditText.isEnabled = false
            binding.editButton.setBackgroundColor(resources.getColor(R.color.mySecondary, null))
            binding.editButton.text = "Edit Details"

            // Save data to Firestore
            saveUserProfile()
        } else {
            // Edit state - Enable editing
            binding.nameEditText.isEnabled = true
            binding.usernameEditText.isEnabled = true
            binding.bioEditText.isEnabled = true
            binding.editButton.setBackgroundColor(resources.getColor(R.color.accentGreen, null))
            binding.editButton.text = "Save Details"
        }
        isEditing = !isEditing  // Toggle the editing state
    }

    private fun saveUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val name = binding.nameEditText.text.toString().trim()
            val username = binding.usernameEditText.text.toString().trim()
            val bio = binding.bioEditText.text.toString().trim()

            val user = hashMapOf<String, Any>(
                "fullName" to name,
                "userName" to username,
                "bio" to bio
            )

            firestore.collection("users").document(userId)
                .update(user)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
