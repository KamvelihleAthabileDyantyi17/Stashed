package com.example.stashed

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.stashed.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    // Destinations where the bottom nav should be hidden
    private val hiddenNavDestinations = setOf(
        R.id.nav_splash,
        R.id.nav_login,
        R.id.nav_register,
        R.id.nav_addExpense,
        R.id.nav_addCategory,
        R.id.nav_addGoal
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Wire bottom navigation
        binding.bottomNav.setupWithNavController(navController)

        // Show/hide bottom nav based on destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in hiddenNavDestinations) {
                binding.bottomNav.visibility = View.GONE
            } else {
                binding.bottomNav.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}