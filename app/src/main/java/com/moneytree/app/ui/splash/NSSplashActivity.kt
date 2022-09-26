package com.moneytree.app.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.moneytree.app.common.NSActivity
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSUserManager
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.databinding.NsActivitySplashBinding
import com.moneytree.app.ui.login.NSLoginActivity
import com.moneytree.app.ui.main.NSMainActivity

class NSSplashActivity : NSActivity() {

    private lateinit var activitySplashBinding: NsActivitySplashBinding
    private val minSplashTime = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySplashBinding = NsActivitySplashBinding.inflate(layoutInflater)
        setContentView(activitySplashBinding.root)
    }

    override fun onStart() {
        super.onStart()
		NSApplication.getInstance().getPrefs().isPopupDisplay = true
        checkLoginStatus()
    }

    /**
     * To check the login and profile status
     */
    private fun checkLoginStatus() {
        if (!NSUserManager.isUserLoggedIn) {
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    switchActivity(
                        NSLoginActivity::class.java,
                        flags = intArrayOf(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    )
                    finish()
                }, minSplashTime
            )
        } else {
            gotoDashboard()
        }
    }

    /**
     * To go to the dashboard screen
     */
    private fun gotoDashboard() {
        Handler(Looper.getMainLooper()).postDelayed(
            {
                switchActivity(
                    NSMainActivity::class.java,
                    flags = intArrayOf(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
            }, minSplashTime)
    }
}
