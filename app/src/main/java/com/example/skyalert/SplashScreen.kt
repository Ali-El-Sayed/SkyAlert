package com.example.skyalert

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.skyalert.databinding.ActivityMainBinding
import com.example.skyalert.network.NetworkDataSourceImpl
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        lifecycleScope.launch {
            val res = NetworkDataSourceImpl.getForecast(27.2516, 33.8182)
            Log.d("MainActivity", "onCreate: ${res.city.name}")
            Log.d("MainActivity", "onCreate: ${res.list[0].weather[0].description}")
        }
    }

}