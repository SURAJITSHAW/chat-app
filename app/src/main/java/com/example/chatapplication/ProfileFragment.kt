package com.example.chatapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chatapplication.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

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

        binding.editButton.setOnClickListener {
            toggleEditSave()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
