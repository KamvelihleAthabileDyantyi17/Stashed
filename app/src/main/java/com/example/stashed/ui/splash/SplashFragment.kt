package com.example.stashed.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.stashed.R
import com.example.stashed.StashedApplication
import com.example.stashed.utils.SessionManager

class SplashFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val session = SessionManager(requireContext())
        view.postDelayed({
            if (session.isLoggedIn()) {
                findNavController().navigate(R.id.action_splash_to_dashboard)
            } else {
                findNavController().navigate(R.id.action_splash_to_login)
            }
        }, 1200)
    }
}
