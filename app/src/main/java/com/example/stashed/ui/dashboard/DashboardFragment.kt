package com.example.stashed.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.stashed.data.AppDatabase

import com.example.stashed.data.repository.StashedRepository
import com.example.stashed.databinding.FragmentDashboardBinding
import com.example.stashed.utils.CurrencyUtils
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

// 1. Initialize Database, Repository, and ViewModel
        val database = AppDatabase.getDatabase(requireContext())
        val repository = StashedRepository(
            database.userDao(),
            database.expenseDao(),
            database.categoryDao(),
            database.goalDao()
        )

        // Hardcoding userId = 1 just for testing the UI
        val factory = DashboardViewModelFactory(repository, userId = 1)
        viewModel = ViewModelProvider(this, factory)[DashboardViewModel::class.java]

        // 2. Static Setup
        binding.tvUserName.text = "Kamvelihle"

        // 3. Click Listeners
        binding.fabAdd.setOnClickListener {
            Toast.makeText(requireContext(), "Add Expense Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.btnProfileIcon.setOnClickListener {
            Toast.makeText(requireContext(), "Syncing to Firebase...", Toast.LENGTH_SHORT).show()
            viewModel.triggerCloudSync()
        }

        // 4. Observe Database Changes and Update UI
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.totalSpend.collect { amount ->
                binding.tvMainBalance.text = CurrencyUtils.format(amount)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}