package com.example.skyalert.view.animation

import android.widget.ProgressBar
import android.widget.TextView

object NumberAnimation {

    fun fromZeroToValueText(value: Int, view: TextView) {
        val animator = android.animation.ValueAnimator.ofInt(0, value)
        animator.duration = 1000
        animator.addUpdateListener { animation ->
            view.text = animation.animatedValue.toString()
        }
        animator.start()
    }

    fun fromZeroToValueProgressBar(value: Int, view: ProgressBar) {
        val animator = android.animation.ValueAnimator.ofInt(0, value)
        animator.duration = 1000
        animator.addUpdateListener { animation ->
            view.progress = animation.animatedValue as Int
        }
        animator.start()
    }
}