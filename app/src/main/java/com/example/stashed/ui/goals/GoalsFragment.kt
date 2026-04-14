package com.example.stashed.ui.goals

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stashed.R
import com.example.stashed.StashedApplication
import com.example.stashed.data.entities.Goal
import com.example.stashed.databinding.FragmentGoalsBinding
import com.example.stashed.ui.ViewModelFactory
import com.example.stashed.utils.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import android.view.inputmethod.EditorInfo
import java.util.Calendar

class GoalsFragment : Fragment() {

    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GoalsViewModel by viewModels {
        val app = requireActivity().application as StashedApplication
        val userId = SessionManager(requireContext()).getUserId()
        ViewModelFactory(app.repository, userId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = GoalAdapter(
            onContribute = { goal -> showContributeDialog(goal) },
            onDelete = { goal -> viewModel.deleteGoal(goal) }
        )
        binding.rvGoals.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGoals.adapter = adapter

        viewModel.goals.observe(viewLifecycleOwner) { goals ->
            adapter.submitList(goals)
            binding.tvEmpty.visibility = if (goals.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_goals_to_addGoal)
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    private fun showContributeDialog(goal: Goal) {
        val input = TextInputEditText(requireContext())
        input.hint = "Amount to add (R)"
        input.inputType = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add to \"${goal.goalName}\"")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val amount = input.text.toString().toDoubleOrNull() ?: return@setPositiveButton
                viewModel.contributeToGoal(goal, amount)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
