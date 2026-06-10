package com.example.stashed.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stashed.R
import com.example.stashed.data.AppDatabase
import com.example.stashed.ui.adapters.BadgeAdapter
import kotlinx.coroutines.launch

class BadgesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_badges)

        val prefs: SharedPreferences = getSharedPreferences("StashedSession", MODE_PRIVATE)
        val userId = prefs.getInt("userId", -1)

        val rvBadges   = findViewById<RecyclerView>(R.id.rvBadges)
        val tvNoBadges = findViewById<TextView>(R.id.tvNoBadges)
        val adapter    = BadgeAdapter(emptyList())

        rvBadges.layoutManager = LinearLayoutManager(this)
        rvBadges.adapter = adapter

        lifecycleScope.launch {
            val db     = AppDatabase.getDatabase(applicationContext)
            val badges = db.badgeDao().getBadgesForUser(userId)

            runOnUiThread {
                if (badges.isEmpty()) {
                    tvNoBadges.visibility = View.VISIBLE
                    rvBadges.visibility   = View.GONE
                } else {
                    tvNoBadges.visibility = View.GONE
                    rvBadges.visibility   = View.VISIBLE
                    adapter.updateData(badges)
                }
            }
        }
    }
}