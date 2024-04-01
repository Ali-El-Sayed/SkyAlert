package com.example.skyalert.view.screens.splash

import android.annotation.SuppressLint
import android.app.LocaleManager
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import com.example.skyalert.dataSource.local.sharedPref.SharedPreferenceImpl
import com.example.skyalert.databinding.ActivitySplashBinding
import com.example.skyalert.interfaces.Callback
import com.example.skyalert.view.screens.extensions.delay
import com.example.skyalert.view.screens.extensions.goScreen
import com.example.skyalert.view.screens.main.HomeScreenActivity
import com.example.skyalert.view.screens.settings.model.LOCAL
import kotlinx.coroutines.launch

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


        setLanguage()


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

    private fun setLanguage() {
        val lang = SharedPreferenceImpl.getInstance(applicationContext).getLanguage()
        lifecycleScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                if (lang != LOCAL.SYSTEM)
                    applicationContext.getSystemService(LocaleManager::class.java)
                        .applicationLocales = LocaleList.forLanguageTags(lang.value)
                else
                    applicationContext.getSystemService(LocaleManager::class.java)
                        .applicationLocales = LocaleList.getDefault()
            else {
                if (lang != LOCAL.SYSTEM)
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(lang.value))
                else
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.getDefault())
            }
        }

    }

}

