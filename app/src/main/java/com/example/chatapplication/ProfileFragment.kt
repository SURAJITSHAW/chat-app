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
        fetchUserDataFromFirestoreAndSave()

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
            clearUserDataFromPrefs()
            auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun fetchUserData() {
        val sharedPreferences = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE)

        // Check if user data already exists in SharedPreferences
        val name = sharedPreferences.getString("fullName", null)
        val username = sharedPreferences.getString("userName", null)
        val bio = sharedPreferences.getString("bio", null)
        val email = sharedPreferences.getString("email", null)

        if (name != null && username != null && bio != null && email != null) {
            // Data exists in SharedPreferences, use it directly
            binding.nameEditText.setText(name)
            binding.usernameEditText.setText(username)
            binding.bioEditText.setText(bio)
            binding.emailTextView.text = email
        } else {
            // Data does not exist in SharedPreferences, fetch from Firestore
            fetchUserDataFromFirestoreAndSave()
        }
    }

    private fun fetchUserDataFromFirestoreAndSave() {
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

                        // Save data to SharedPreferences for future sessions
                        saveUserDataToPrefs(name, username, bio, email)
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

//
//    private fun fetchUserDataFromFirestore() {
//        val userId = auth.currentUser?.uid
//        if (userId != null) {
//            firestore.collection("users").document(userId).get()
//                .addOnSuccessListener { document ->
//                    if (document.exists()) {
//                        val name = document.getString("fullName")
//                        val username = document.getString("userName")
//                        val bio = document.getString("bio")
//                        val email = document.getString("email")
//
//                        // Set values to the fields
//                        binding.nameEditText.setText(name)
//                        binding.usernameEditText.setText(username)
//                        binding.bioEditText.setText(bio)
//                        binding.emailTextView.text = email
//
//
//                    } else {
//                        Toast.makeText(requireContext(), "No user data found", Toast.LENGTH_SHORT).show()
//                    }
//                }
//                .addOnFailureListener { e ->
//                    Toast.makeText(requireContext(), "Error fetching user data: ${e.message}", Toast.LENGTH_SHORT).show()
//                }
//        } else {
//            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
//        }
//    }

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

                    // Save to SharedPreferences for future sessions
                    saveUserDataToPrefs(name, username, bio)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveUserDataToPrefs(name: String?, username: String?, bio: String?, email: String? = null) {
        val sharedPreferences = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("fullName", name)
            putString("userName", username)
            putString("bio", bio)

            // Only update email if it's provided
            email?.let {
                putString("email", it)
            }

            apply()
        }
    }


    private fun clearUserDataFromPrefs() {
        val sharedPreferences = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()  // Clear all data in SharedPreferences
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
