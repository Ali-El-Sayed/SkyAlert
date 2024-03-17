package com.example.skyalert

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.skyalert.databinding.ActivitySplashBinding
import com.example.skyalert.home.HomeScreenActivity

class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // pretty animation for the splash screen app name with object animator
        binding.tvAppName.alpha = 0f
        binding.tvAppName.animate().setDuration(1000).alpha(1f)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashScreen, HomeScreenActivity::class.java))
            finish()
        }, 2200)

    }

}