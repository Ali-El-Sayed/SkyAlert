package com.example.skyalert

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.skyalert.databinding.ActivitySplashBinding

class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.tvAppName.setOnClickListener {
            startActivity(Intent(this@SplashScreen, HomeScreen::class.java))
            finish()
        }

//        Handler(Looper.getMainLooper()).postDelayed({
//            startActivity(Intent(this@SplashScreen, HomeScreen::class.java))
//            finish()
//        }, 3000)

    }

}