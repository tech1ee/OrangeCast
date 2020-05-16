package com.example.orangecast.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.orangecast.R
import com.example.orangecast.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        initNavigation()
    }

    private fun initNavigation() {
        val bottomNavigationView = binding.bottomNavigationView
        val navHostFragment = nav_host_fragment ?: return
        NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.findNavController())
    }
}
