package com.moneytree.app.ui.lock

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.beautycoder.pflockscreen.PFFLockScreenConfiguration
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment
import com.beautycoder.pflockscreen.security.PFResult
import com.beautycoder.pflockscreen.viewmodels.PFPinCodeViewModel
import com.moneytree.app.R
import com.moneytree.app.common.NSActivity
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.PreferencesSettings
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.ActivityLockBinding
import com.moneytree.app.ui.main.NSMainActivity

class LockActivity : NSActivity() {

    private lateinit var activity: Activity
    private var readCode: Int = -1
    private lateinit var activityLockBinding: ActivityLockBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLockBinding = ActivityLockBinding.inflate(layoutInflater)
        setContentView(activityLockBinding.root)
        activity = this
        getIntentData()
        mainLockScreen()
        setListener()
    }

    private fun getIntentData() {
        readCode = intent.getIntExtra(NSConstants.KEY_LOCK_SCREEN, -1)
    }

    private fun mainLockScreen() {
        showLockScreenFragment()
    }

    private fun setListener() {
        activityLockBinding.apply {
            tvSkip.setOnClickListener(object : SingleClickListener() {
                override fun performClick(v: View?) {
                    checkValidationLogin()
                }
            })
        }
    }

    private val mCodeCreateListener: PFLockScreenFragment.OnPFLockScreenCodeCreateListener =
        object : PFLockScreenFragment.OnPFLockScreenCodeCreateListener {
            override fun onCodeCreated(encodedCode: String) {
                Toast.makeText(this@LockActivity, "Code created", Toast.LENGTH_SHORT).show()
                PreferencesSettings.saveToPref(activity, encodedCode)
                checkValidationLogin()
            }

            override fun onNewCodeValidationFailed() {
                Toast.makeText(activity, "Code validation error", Toast.LENGTH_SHORT).show()
            }
        }

    private val mLoginListener: PFLockScreenFragment.OnPFLockScreenLoginListener = object :
        PFLockScreenFragment.OnPFLockScreenLoginListener {
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
        if (!isPinExist) {
            activityLockBinding.tvSkip.visible()
        }
        val builder = PFFLockScreenConfiguration.Builder(this)
            .setTitle(if (isPinExist) "Unlock with your pin code or fingerprint" else "Create Code")
            .setCodeLength(4)
            .setLeftButton("")
            .setNewCodeValidation(true)
            .setNewCodeValidationTitle("Please input code again")
            .setUseFingerprint(true).setAutoShowFingerprint(true)
        val fragment = PFLockScreenFragment()
        fragment.setOnLeftButtonClickListener {
            /*Toast.makeText(
                activity,
                "Left button pressed",
                Toast.LENGTH_LONG
            ).show()*/
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

    private fun checkValidationLogin() {
        if (readCode == 1) {
            onBackPressed()
        } else {
            gotoDashboard()
        }
    }

    /**
     * To go to the dashboard screen
     */
    private fun gotoDashboard() {
        NSUtilities.checkUserVerified(activity) {
            switchActivity(
                NSMainActivity::class.java,
                flags = intArrayOf(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }
    }

}
