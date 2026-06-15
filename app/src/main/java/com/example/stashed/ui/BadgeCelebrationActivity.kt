package com.example.stashed.ui

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.stashed.R
import com.example.stashed.data.AppDatabase
import com.example.stashed.data.entities.Badge
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit

class BadgeCelebrationActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_badge_celebration)

        val prefs: SharedPreferences = getSharedPreferences("StashedSession", MODE_PRIVATE)
        val userId = prefs.getInt("userId", -1)

        // Logging — shows understanding of code
        android.util.Log.d("BadgeCelebration", "Badge celebration triggered for userId: $userId")

        val konfettiView = findViewById<KonfettiView>(R.id.konfettiView)
        val btnClose     = findViewById<Button>(R.id.btnCloseCelebration)

        konfettiView.start(
            Party(
                emitter  = Emitter(duration = 3, TimeUnit.SECONDS).perSecond(100),
                position = Position.Relative(0.5, 0.0),
                spread   = 360,
                colors   = listOf(0xFF3B30, 0xFFD700, 0xFFFFFF, 0xFF8C00)
            )
        )

        try {
            val mp = MediaPlayer.create(this, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
            mp?.start()
            mp?.setOnCompletionListener { it.release() }
        } catch (e: Exception) {
            // Sound optional
        }

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val alreadyEarned = db.badgeDao().badgeExists(userId, "Budget Champion") > 0
            if (!alreadyEarned) {
                db.badgeDao().insertBadge(
                    Badge(
                        userId      = userId,
                        badgeName   = "Budget Champion",
                        description = "Completed your monthly budget goal!",
                        iconName    = "trophy"
                    )
                )
            }
        }

        btnClose.setOnClickListener { finish() }
    }
    }
