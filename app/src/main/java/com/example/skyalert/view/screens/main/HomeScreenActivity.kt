package com.example.skyalert.view.screens.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.skyalert.R
import com.example.skyalert.dataSource.local.sharedPref.SharedPreferenceImpl
import com.example.skyalert.dataSource.remote.WeatherRemoteDatasource
import com.example.skyalert.databinding.ActivityHomeScreenBinding
import com.example.skyalert.network.RetrofitClient
import com.example.skyalert.repository.WeatherRepo
import com.example.skyalert.util.WeatherViewModelFactory
import com.example.skyalert.view.screens.main.viewModel.MainViewModel

class HomeScreenActivity : AppCompatActivity() {
    private val TAG = "HomeScreenActivity"
    private lateinit var binding: ActivityHomeScreenBinding
    private val viewModel: MainViewModel by lazy {
        val remoteDataSource = WeatherRemoteDatasource.getInstance(RetrofitClient.apiService)
        val repo = WeatherRepo.getInstance(
            remoteDataSource, SharedPreferenceImpl.getInstance(this)
        )
        val factory = WeatherViewModelFactory(repo)
        factory.create(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeScreenBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.nav_host_fragment)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


}