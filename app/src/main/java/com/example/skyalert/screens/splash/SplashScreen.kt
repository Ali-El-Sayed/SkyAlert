package com.example.skyalert.screens.splash

import android.annotation.SuppressLint
import android.os.Bundle
import com.example.skyalert.databinding.ActivitySplashBinding
import com.example.skyalert.interfaces.Callback
import com.example.skyalert.screens.home.HomeScreenActivity
import com.example.skyalert.screens.parents.BaseActivity

@SuppressLint("CustomSplashScreen")
class SplashScreen : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // pretty animation for the splash screen app name with object animator
        binding.tvAppName.alpha = 0f
        binding.tvAppName.animate().setDuration(1000).alpha(1f)


        /**
         *  delay the splash screen for 2.2 seconds
         *  then navigate to the home screen
         * */

        delay(2200, object : Callback {
            override fun onFinished() {
                goScreen(HomeScreenActivity::class.java)
            }
        })
    }

}

