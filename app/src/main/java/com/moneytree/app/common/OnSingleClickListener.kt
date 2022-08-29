package com.moneytree.app.common

import android.os.Handler
import android.os.SystemClock
import android.view.View
import com.moneytree.app.common.OnSingleClickListener

abstract class OnSingleClickListener : View.OnClickListener {
    private var mLastClickTime: Long = 0
    abstract fun onSingleClick(v: View?)
    override fun onClick(v: View) {
        val currentClickTime = SystemClock.uptimeMillis()
        val elapsedTime = currentClickTime - mLastClickTime
        mLastClickTime = currentClickTime
        if (elapsedTime <= MIN_CLICK_INTERVAL) return
        if (!isViewClicked) {
            isViewClicked = true
            startTimer()
        } else {
            return
        }
        onSingleClick(v)
    }

    /**
     * This method delays simultaneous touch events of multiple views.
     */
    private fun startTimer() {
        val handler = Handler()
        handler.postDelayed({ isViewClicked = false }, 600)
    }

    companion object {
        private const val MIN_CLICK_INTERVAL: Long = 600
        var isViewClicked = false
    }
}
