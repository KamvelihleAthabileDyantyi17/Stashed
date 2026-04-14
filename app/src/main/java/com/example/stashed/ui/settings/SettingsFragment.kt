package com.example.stashed.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.stashed.R
import com.example.stashed.StashedApplication
import com.example.stashed.databinding.FragmentSettingsBinding
import com.example.stashed.ui.ViewModelFactory
import com.example.stashed.utils.DateUtils
import com.example.stashed.utils.SessionManager

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels {
        val app = requireActivity().application as StashedApplication
        val userId = SessionManager(requireContext()).getUserId()
        ViewModelFactory(app.repository, userId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.tvUserName.text = user.fullName
                binding.tvUserEmail.text = user.email
                binding.tvMemberSince.text = "Member since ${DateUtils.formatDate(user.dateRegistered)}"
            }
        }

        binding.btnLogout.setOnClickListener {
            val session = SessionManager(requireContext())
            session.logout()
            findNavController().navigate(R.id.action_settings_to_login)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}