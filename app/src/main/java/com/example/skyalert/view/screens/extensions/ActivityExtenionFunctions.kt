package com.example.skyalert.view.screens.extensions

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.skyalert.interfaces.Callback
import com.example.skyalert.view.dialogs.AlertResultDialog
import com.example.skyalert.view.screens.main.HomeScreenActivity


/**
 *   This function is used to navigate to another screen
 *  @param to: the class of the screen to navigate to
 * */
fun AppCompatActivity.goScreen(to: Class<*>) {
    startActivity(Intent(this, to))
    finish()
}

/**
 *  This function is used to delay the screen for a certain duration
 *  @param duration: the duration to delay the screen
 * */
fun AppCompatActivity.delay(duration: Long, callBack: Callback) {
    Handler(Looper.getMainLooper()).postDelayed({
        callBack.onFinished()
    }, duration)
}

fun HomeScreenActivity.showAlertResultDialog(alertResultDialog: AlertResultDialog) {
    alertResultDialog.show(supportFragmentManager, "AlertResultDialog")
}
