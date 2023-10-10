package com.moneytree.app.ui.splash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.lifecycle.Observer
import com.beautycoder.pflockscreen.PFFLockScreenConfiguration
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment.OnPFLockScreenCodeCreateListener
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment.OnPFLockScreenLoginListener
import com.beautycoder.pflockscreen.security.PFResult
import com.beautycoder.pflockscreen.viewmodels.PFPinCodeViewModel
import com.moneytree.app.R
import com.moneytree.app.common.NSActivity
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSUserManager
import com.moneytree.app.common.NSWelcomePreferences
import com.moneytree.app.common.PreferencesSettings
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.databinding.NsActivitySplashBinding
import com.moneytree.app.ui.lock.LockActivity
import com.moneytree.app.ui.login.NSLoginActivity
import com.moneytree.app.ui.main.NSMainActivity
import com.moneytree.app.ui.welcome.WelcomeActivity


class NSSplashActivity : NSActivity() {

	private lateinit var activitySplashBinding: NsActivitySplashBinding
	private val minSplashTime = 3000L
	private lateinit var activity: Activity

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activitySplashBinding = NsActivitySplashBinding.inflate(layoutInflater)
		setContentView(activitySplashBinding.root)
		activity = this
	}

	override fun onStart() {
		super.onStart()
		NSApplication.getInstance().getPrefs().isPopupDisplay = true
		checkLoginStatus()
		//mainLockScreen()
	}

	/**
	 * To check the login and profile status
	 */
	private fun checkLoginStatus() {
		Handler(Looper.getMainLooper()).postDelayed({
				checkValidationLogin()
			}, minSplashTime
		)
	}

	private fun checkValidationLogin() {
		if (NSWelcomePreferences(this).isWelcome) {
			if (!NSUserManager.isUserLoggedIn) {
				switchActivity(
					NSLoginActivity::class.java,
					flags = intArrayOf(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
				)
				finish()
			} else {
				gotoDashboard()
			}
		} else {
			switchActivity(
				WelcomeActivity::class.java,
				flags = intArrayOf(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
			)
			finish()
		}
	}

	/**
	 * To go to the dashboard screen
	 */
	private fun gotoDashboard() {
		switchActivity(
			LockActivity::class.java,
			flags = intArrayOf(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
		)
	}


	private fun mainLockScreen() {
		showLockScreenFragment()
    }

	private val mCodeCreateListener: OnPFLockScreenCodeCreateListener =
		object : OnPFLockScreenCodeCreateListener {
			override fun onCodeCreated(encodedCode: String) {
				Toast.makeText(this@NSSplashActivity, "Code created", Toast.LENGTH_SHORT).show()
				PreferencesSettings.saveToPref(activity, encodedCode)
				checkValidationLogin()
			}

			override fun onNewCodeValidationFailed() {
				Toast.makeText(activity, "Code validation error", Toast.LENGTH_SHORT).show()
			}
		}

	private val mLoginListener: OnPFLockScreenLoginListener = object : OnPFLockScreenLoginListener {
		override fun onCodeInputSuccessful() {
			checkValidationLogin()
		}

		override fun onFingerprintSuccessful() {
			checkValidationLogin()
		}

		override fun onPinLoginFailed() {
			Toast.makeText(activity, "Pin failed", Toast.LENGTH_SHORT).show()
		}

		override fun onFingerprintLoginFailed() {
			Toast.makeText(activity, "Fingerprint failed", Toast.LENGTH_SHORT).show()
		}
	}

	private fun showLockScreenFragment() {
		PFPinCodeViewModel().isPinCodeEncryptionKeyExist.observe(
			this,
			object : Observer<PFResult<Boolean?>?> {
				override fun onChanged(result: PFResult<Boolean?>?) {
					if (result == null) {
						return
					}
					if (result.error != null) {
						Toast.makeText(
							activity,
							"Can not get pin code info",
							Toast.LENGTH_SHORT
						).show()
						return
					}
					result.result?.let { showLockScreenFragment(it) }
				}
			}
		)
	}

	private fun showLockScreenFragment(isPinExist: Boolean) {
		val builder = PFFLockScreenConfiguration.Builder(this)
			.setTitle(if (isPinExist) "Unlock with your pin code or fingerprint" else "Create Code")
			.setCodeLength(4)
			.setLeftButton("Can't remeber")
			.setNewCodeValidation(true)
			.setNewCodeValidationTitle("Please input code again")
			.setUseFingerprint(true).setAutoShowFingerprint(true)
		val fragment = PFLockScreenFragment()
		fragment.setOnLeftButtonClickListener {
			Toast.makeText(
				activity,
				"Left button pressed",
				Toast.LENGTH_LONG
			).show()
		}
		builder.setMode(if (isPinExist) PFFLockScreenConfiguration.MODE_AUTH else PFFLockScreenConfiguration.MODE_CREATE)
		if (isPinExist) {
			fragment.setEncodedPinCode(PreferencesSettings.getCode(this))
			fragment.setLoginListener(mLoginListener)
		}
		fragment.setConfiguration(builder.build())
		fragment.setCodeCreateListener(mCodeCreateListener)
		supportFragmentManager.beginTransaction()
			.replace(R.id.frame_container, fragment).commit()
	}

}
