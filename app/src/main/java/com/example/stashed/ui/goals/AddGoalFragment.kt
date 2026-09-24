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
import com.example.stashed.StashedApplication
import com.example.stashed.databinding.FragmentAddGoalBinding
import com.example.stashed.ui.ViewModelFactory
import com.example.stashed.utils.DateUtils
import com.example.stashed.utils.SessionManager
import java.util.Calendar

class AddGoalFragment : Fragment() {

    private var _binding: FragmentAddGoalBinding? = null
    private val binding get() = _binding!!
    private var selectedDeadline: Long = 0L

    private val viewModel: GoalsViewModel by viewModels {
        val app = requireActivity().application as StashedApplication
        val userId = SessionManager(requireContext()).getUserId()
        ViewModelFactory(app.repository, userId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddGoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Updated ID to match the new UI's Date Picker field
        binding.etTargetDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val picked = Calendar.getInstance()
                    picked.set(year, month, day, 23, 59, 59)
                    selectedDeadline = picked.timeInMillis

                    // Display the selected date inside the input field
                    binding.etTargetDate.setText(DateUtils.formatDate(selectedDeadline))
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Top left 'X' button
        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }

        // Updated ID to match new UI button
        binding.btnSaveGoal.setOnClickListener {
            val name = binding.etGoalName.text.toString().trim()
            val targetStr = binding.etTargetAmount.text.toString().trim()
            val target = targetStr.toDoubleOrNull() ?: 0.0

            // You can also capture 'etAlreadySaved' here if you update your ViewModel later!

            if (name.isNotEmpty() && target > 0) {
                viewModel.addGoal(name, target, selectedDeadline)
            } else {
                Toast.makeText(requireContext(), "Please enter a valid name and amount", Toast.LENGTH_SHORT).show()
            }
        }

        // Updated ID to match new UI button
        binding.btnCancelGoal.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.saveResult.observe(viewLifecycleOwner) { saved ->
            if (saved == true) {
                Toast.makeText(requireContext(), "Goal created!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
                viewModel.clearSaveResult()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}