package com.moneytree.app.common

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.moneytree.app.R
import com.moneytree.app.common.callbacks.NSProgressCallback
import com.moneytree.app.common.callbacks.NSReplaceFragmentCallback
import com.moneytree.app.common.utils.NSAlertUtils
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.hideKeyboard
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.ui.home.NSHomeFragment
import com.moneytree.app.ui.noNetwork.NoNetworkActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * The base class for all fragments which holds the members and methods common to all fragments
 */
open class NSFragment : Fragment() {
    protected val tags: String = this::class.java.simpleName
    private lateinit var mContext: Context
    protected lateinit var activity: Activity
    private var replaceFragmentCallback: NSReplaceFragmentCallback? = null
    private var progressCallback: NSProgressCallback? = null
    val pref = NSApplication.getInstance().getPrefs()
    val nsApp = NSApplication.getInstance()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        activity = requireActivity()
        try {
            progressCallback = context as NSProgressCallback
            replaceFragmentCallback = context as NSReplaceFragmentCallback
        } catch (exception: ClassCastException) {
            NSLog.e(tags, "onAttach: ClassCastException: " + exception.message)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        (mContext as FragmentActivity).hideKeyboard()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    /**
     * To replace the fragments
     *
     * @param fragmentToReplace The fragment to replace
     * @param addToBackStack    Whether to add the fragment to backstack or not
     * @param containerId       The frame layout containing the fragment
     */
    protected open fun replaceFragment(
        fragmentToReplace: Fragment, addToBackStack: Boolean, containerId: Int
    ) {
        replaceFragmentCallback?.replaceCurrentFragment(
            fragmentToReplace, addToBackStack, containerId
        )
    }

    /**
     * To show alert dialog
     *
     * @param message The message to show as alert message
     */
    protected fun showAlertDialog(message: String?) {
        val errorMessage: String = message ?: getString(R.string.something_went_wrong)
        NSAlertUtils.showAlertDialog(mContext as FragmentActivity, errorMessage)
    }

    /**
     * To show alert dialog
     *
     * @param message The message to show as alert message
     */
    protected fun showSuccessDialog(title: String?, message: String?, alertKey: String = NSConstants.POSITIVE_CLICK) {
        val errorMessage: String = message ?: getString(R.string.something_went_wrong)
        NSAlertUtils.showAlertDialog(mContext as FragmentActivity, errorMessage, title, alertKey = alertKey)
    }

    /**
     * To show logout alert dialog
     *
     * @param message The message to show as alert message
     */
    protected fun showLogoutDialog(title: String?, message: String?, positiveButton: String, negativeButton: String) {
        val errorMessage: String = message ?: getString(R.string.something_went_wrong)
        NSAlertUtils.showAlertDialog(mContext as FragmentActivity, errorMessage, title, alertKey = NSConstants.LOGOUT_CLICK, positiveButtonText = positiveButton, negativeButtonText = negativeButton, isCancelNeeded = true)
    }

    /**
     * To display the no network dialog
     */
    protected fun showNoNetworkAlertDialog(title: String?, message: String?) {
        val errorMessage: String = message ?: getString(R.string.something_went_wrong)
		switchActivity(
			NoNetworkActivity::class.java
		)
        //NSAlertUtils.showAlertDialog(mContext as FragmentActivity, errorMessage, title)
    }

    /**
     * To parse api error and show alert
     *
     * @param apiErrorList api error list
     */
    protected fun parseAndShowApiError(apiErrorList: List<Any>) {
        showAlertDialog(NSUtilities.parseApiErrorList(mContext, apiErrorList))
    }

    /**
     * To back press
     */
    protected fun onBackPress() {
        (mContext as FragmentActivity).onBackPressed()
    }

    /**
     * To finish
     */
    protected fun finish() {
        (mContext as FragmentActivity).finish()
    }

    /**
     * To update the progress bar by communicating with base activity
     *
     * @param shouldShowProgress The boolean to determine whether to show progress bar
     */
    protected open fun updateProgress(shouldShowProgress: Boolean) {
        progressCallback?.updateProgress(shouldShowProgress)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLogoutEvent(logoutEvent: NSLogoutEvent) {
        NSLog.d(tags, "onLogoutEvent: $logoutEvent")
    }

    fun hideKeyboard(currentFocus: View) {
        currentFocus.let { view ->
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

	val dataResult =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
			val resultCode = result.resultCode
			val data = result.data
			EventBus.getDefault().post(NSActivityEvent(resultCode, data))
		}
}
