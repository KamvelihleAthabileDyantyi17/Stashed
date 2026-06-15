package com.example.stashed.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stashed.R
import com.example.stashed.data.entities.Badge

class BadgeAdapter(private var badges: List<Badge>) :
    RecyclerView.Adapter<BadgeAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView        = view.findViewById(R.id.tvBadgeName)
        val tvDescription: TextView = view.findViewById(R.id.tvBadgeDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_badge, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val badge = badges[position]
        holder.tvName.text        = badge.badgeName
        holder.tvDescription.text = badge.description
    }

    override fun getItemCount() = badges.size

    fun updateData(newBadges: List<Badge>) {
        badges = newBadges
        notifyDataSetChanged()
    }
}