package com.example.stashed.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.stashed.R
import com.example.stashed.StashedApplication
import com.example.stashed.databinding.FragmentRegisterBinding
import com.example.stashed.ui.ViewModelFactory
import com.example.stashed.utils.SessionManager

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels {
        val repo = (requireActivity().application as StashedApplication).repository
        ViewModelFactory(repo)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            val name = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            authViewModel.register(name, email, password)
        }

        binding.tvLogin.setOnClickListener {
            findNavController().popBackStack()
        }

        authViewModel.authResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AuthResult.Loading -> binding.btnRegister.isEnabled = false
                is AuthResult.Success -> {
                    val session = SessionManager(requireContext())
                    session.login(result.user.userId, result.user.fullName)
                    findNavController().navigate(R.id.action_register_to_dashboard)
                    authViewModel.resetResult()
                }
                is AuthResult.Error -> {
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                    authViewModel.resetResult()
                }
                null -> binding.btnRegister.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
