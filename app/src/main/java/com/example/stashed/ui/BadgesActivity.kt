package com.example.stashed.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.stashed.databinding.ActivityBadgesBinding

class BadgesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBadgesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBadgesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
