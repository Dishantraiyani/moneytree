package com.moneytree.app.common

import android.content.Intent
import android.view.MenuItem
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.moneytree.app.common.callbacks.NSProgressCallback
import com.moneytree.app.common.callbacks.NSReplaceFragmentCallback
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.ui.login.NSLoginActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * The base class for all activities which holds the members and methods common to all activities
 */
open class NSActivity : AppCompatActivity(), NSReplaceFragmentCallback, NSProgressCallback {
    protected val TAG: String = NSActivity::class.java.simpleName
    private var isProgressShowing = false
    private var allowBackPress = true
    private lateinit var rlLayout: RelativeLayout

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Invoked when the fragment needs to be replaced
     *
     * @param fragmentToReplace    The fragment which should replace the current one
     * @param shouldAddToBackStack The boolean specifying whether to add the fragment to backstack or not
     * @param containerId          The id of the layout which acts as a container for the fragments
     */
    override fun replaceCurrentFragment(
        fragmentToReplace: Fragment, shouldAddToBackStack: Boolean, containerId: Int
    ) {
        replaceFragment(fragmentToReplace, shouldAddToBackStack, containerId)
    }

    /**
     * Invoked when the progress should be updated
     *
     * @param shouldShowProgress The boolean to determine whether to show or hide the progress bar
     */
    override fun updateProgress(shouldShowProgress: Boolean) {
        if (shouldShowProgress) {
            showProgress()
        } else {
            hideProgress()
        }
    }

    /**
     * To replace the fragments
     *
     * @param fragmentToReplace The fragment to replace
     * @param addToBackStack    Whether to add the fragment to backstack or not
     * @param containerId       The frame layout containing the fragment
     */
    private fun replaceFragment(
        fragmentToReplace: Fragment, addToBackStack: Boolean, containerId: Int
    ) {
        val fragmentTransaction = this.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(containerId, fragmentToReplace)
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commit()
    }

    /**
     * To show the progress loading
     */
    open fun showProgress() {
        if (!isProgressShowing) {
            isProgressShowing = true
            allowBackPress = false // handling the back press while showing and dismiss the progress
            window?.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            rlLayout = RelativeLayout(this)
            val progressBar = ProgressBar(this).apply { //don't set style with material design
                isIndeterminate = true
            }
            val paramsForProgressBar = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            paramsForProgressBar.addRule(RelativeLayout.CENTER_IN_PARENT)
            rlLayout.addView(progressBar, paramsForProgressBar)
            rlLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            val paramsForRelativeLayout = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            ).apply {
                addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
            }
            addContentView(rlLayout, paramsForRelativeLayout)
        }
    }

    /**
     * To hide the progress loading
     */
    open fun hideProgress() {
        isProgressShowing = false
        allowBackPress = true
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if (::rlLayout.isInitialized) {
            val parent = rlLayout.parent as ViewGroup?
            parent?.removeView(rlLayout)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLogoutEvent(logoutEvent: NSLogoutEvent) {
        NSLog.d(TAG, "onLogoutEvent: $logoutEvent")
        val instance = NSApplication.getInstance()
        instance.getApiManager().cancelAllRequests()
        instance.getPrefs().clearPrefData()
        switchActivity(
            NSLoginActivity::class.java,
            flags = intArrayOf(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EventBus.getDefault().post(NSPermissionEvent(requestCode, permissions, grantResults))
    }
}