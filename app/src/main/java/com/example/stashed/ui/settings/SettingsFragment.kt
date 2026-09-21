package com.example.stashed.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.stashed.databinding.FragmentSettingsBinding
import com.example.stashed.utils.SessionManager

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        // Display current session info
        val userId = sessionManager.getUserId()
        binding.tvUserEmail.text = "Active User ID: $userId\nCurrency: South African Rand (ZAR)"

        // Handle settings toggle
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) "Dark theme active" else "Standard theme active"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        // Handle logout
        binding.tvLogout.setOnClickListener {
            sessionManager.logout()
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}