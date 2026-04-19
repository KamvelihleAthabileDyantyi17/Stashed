package com.example.stashed.ui.goals

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stashed.data.entities.Goal
import com.example.stashed.databinding.ItemGoalBinding
import com.example.stashed.utils.CurrencyUtils
import com.example.stashed.utils.DateUtils

class GoalAdapter(
    private val onContribute: (Goal) -> Unit,
    private val onDelete: (Goal) -> Unit
) : ListAdapter<Goal, GoalAdapter.ViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Goal>() {
            override fun areItemsTheSame(a: Goal, b: Goal) = a.goalId == b.goalId
            override fun areContentsTheSame(a: Goal, b: Goal) = a == b
        }
    }

    inner class ViewHolder(private val binding: ItemGoalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(goal: Goal) {
            binding.tvGoalName.text = goal.goalName
            binding.tvSaved.text = CurrencyUtils.format(goal.savedAmount)
            binding.tvTarget.text = "/ ${CurrencyUtils.format(goal.targetAmount)}"
            binding.tvDeadline.text = "By ${DateUtils.formatDate(goal.deadline)}"

            val progress = if (goal.targetAmount > 0)
                ((goal.savedAmount / goal.targetAmount) * 100).coerceIn(0.0, 100.0).toInt()
            else 0
            binding.progressGoal.progress = progress
            binding.tvGoalPercent.text = "${progress}%"

            if (goal.isComplete) {
                binding.tvStatus.text = "COMPLETE"
                binding.btnContribute.isEnabled = false
            } else {
                binding.tvStatus.text = "IN PROGRESS"
                binding.btnContribute.isEnabled = true
            }

            binding.btnContribute.setOnClickListener { onContribute(goal) }
            binding.btnDelete.setOnClickListener { onDelete(goal) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
