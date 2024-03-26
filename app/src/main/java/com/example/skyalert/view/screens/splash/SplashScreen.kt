package com.example.skyalert.view.screens.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.skyalert.databinding.ActivitySplashBinding
import com.example.skyalert.interfaces.Callback
import com.example.skyalert.view.screens.extensions.delay
import com.example.skyalert.view.screens.extensions.goScreen
import com.example.skyalert.view.screens.main.HomeScreenActivity

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // pretty animation for the splash screen app name with object animator
        binding.tvAppName.alpha = 0f
        binding.tvAppName.animate().setDuration(1000).alpha(1f)


        /**
         * delay the splash screen for 2.2 seconds and navigate to the home screen
         * @param duration: 2200 milliseconds
         * */

        delay(2200, object : Callback {
            override fun onFinished() {
                goScreen(HomeScreenActivity::class.java)
            }
        })
    }


}

