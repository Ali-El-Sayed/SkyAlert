package com.example.skyalert.home.weatherScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.skyalert.R
import com.example.skyalert.databinding.FragmentWeatherBinding

class WeatherFragment : Fragment() {
    private lateinit var binding: FragmentWeatherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeatherBinding.inflate(layoutInflater)
        val navController = findNavController()

        binding.toolbar.setupWithNavController(
            navController,
            AppBarConfiguration(navController.graph)
        )

        binding
        return binding.root
    }

}