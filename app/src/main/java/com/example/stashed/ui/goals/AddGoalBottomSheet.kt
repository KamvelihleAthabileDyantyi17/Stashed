package com.example.stashed.ui.goals

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.stashed.R
import com.example.stashed.StashedApplication
import com.example.stashed.ui.ViewModelFactory
import com.example.stashed.utils.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class AddGoalBottomSheet : BottomSheetDialogFragment() {

    private lateinit var sessionManager: SessionManager
    private var selectedDeadlineMillis: Long = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000) // Default 30 days from now

    private val viewModel: GoalsViewModel by viewModels {
        ViewModelFactory(
            (requireActivity().application as StashedApplication).repository,
            sessionManager.getUserId()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_goal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etName = view.findViewById<TextInputEditText>(R.id.etGoalName)
        val etAmount = view.findViewById<TextInputEditText>(R.id.etTargetAmount)
        val btnDeadline = view.findViewById<MaterialButton>(R.id.btnSelectDeadline)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSaveGoal)

        // Date Picker for Deadline
        btnDeadline.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    selectedDeadlineMillis = calendar.timeInMillis
                    btnDeadline.text = "Target Date: $dayOfMonth/${month + 1}/$year"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Save Goal Action
        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val target = etAmount.text.toString().toDoubleOrNull() ?: 0.0

            viewModel.addGoal(name, target, selectedDeadlineMillis)
        }

        // Observe results from ViewModel
        viewModel.saveResult.observe(viewLifecycleOwner) { success ->
            if (success == true) {
                Toast.makeText(requireContext(), "Savings pocket created!", Toast.LENGTH_SHORT).show()
                viewModel.clearSaveResult()
                dismiss()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg != null) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }
}