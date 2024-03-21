package com.example.skyalert.interfaces


/**
 *  Callback interface for the splash screen
 *  to be called when the delay is finished
 * */
interface Callback {

    /**
     * function to be called when the delay is finished
     * and the splash screen is ready to navigate to the home screen
     * */
    fun onFinished()
}