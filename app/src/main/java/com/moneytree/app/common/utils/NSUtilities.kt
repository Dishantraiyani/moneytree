package com.moneytree.app.common.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.view.View
import com.moneytree.app.R


/**
 * The utility class that handles tasks that are common throughout the application
 */
object NSUtilities {

	val statesEnableDisable = arrayOf(
		intArrayOf(android.R.attr.state_enabled),
		intArrayOf(-android.R.attr.state_enabled)
	)

    /**
     * To parse api error list and get string message from resource id
     *
     * @param apiErrorList error list that received from api
     */
    fun parseApiErrorList(context: Context, apiErrorList: List<Any>): String {
        var errorMessage = ""
        for (apiError in apiErrorList) {
            if (apiError is Int) {
                errorMessage += """${context.getString(apiError)} """
            } else if (apiError is String) {
                errorMessage += "$apiError "
            }
        }
        return errorMessage.trim()
    }

    /**
     * Method call to open Browser
     */
    fun openBrowser(activity: Activity, link: String) {
        var linkValue = link
        if (linkValue == "") {
			linkValue = "https://www.google.com"
        }
        val packageName = "com.android.chrome"
        val isAppInstalled: Boolean = appInstalledOrNot(activity, packageName)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkValue))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (isAppInstalled) intent.setPackage("com.android.chrome")
        try {
            activity.startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            intent.setPackage(null)
            activity.startActivity(intent)
        }
    }

    /**
     * Method call to check App installed or not
     */
    private fun appInstalledOrNot(activity: Activity, uri: String?): Boolean {
        val pm = activity.packageManager
        try {
            pm.getPackageInfo(uri!!, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return false
    }

    fun shareAll(activity: Activity, link: String?) {
        val pm = activity.packageManager
        try {
            val waIntent = Intent(Intent.ACTION_SEND)
            waIntent.type = "text/plain"
            waIntent.putExtra(Intent.EXTRA_TEXT, link)
            activity.startActivity(Intent.createChooser(waIntent, "share"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

	fun setViewEnableDisableTint(view: View) {
		view.backgroundTintList = getViewEnableDisableState()
	}

	private fun getViewEnableDisableState(): ColorStateList {
		val colors = intArrayOf(
			R.color.orange,
			R.color.hint_color
		)
		return ColorStateList(statesEnableDisable, colors)
	}
}
