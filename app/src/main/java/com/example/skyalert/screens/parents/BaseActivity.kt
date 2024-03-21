package com.example.skyalert.screens.parents

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.skyalert.interfaces.Callback


/**
 * This class is the parent class for all the activities in the app
 * It contains the common functions that are used in all the activities
 * */

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    /**
     *   This function is used to navigate to another screen
     *  @param to: the class of the screen to navigate to
     * */
    fun goScreen(to: Class<*>) {
        startActivity(Intent(this, to))
        finish()
    }

    /**
     *  This function is used to delay the screen for a certain duration
     *  @param duration: the duration to delay the screen
     * */
    fun delay(duration: Long, callBack: Callback) {
        Handler(Looper.getMainLooper()).postDelayed({
            callBack.onFinished()
        }, duration)
    }
}