package com.moneytree.app.common.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PackageInfoFlags
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Base64
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.moneytree.app.R
import com.moneytree.app.databinding.DialogPopupHomeBinding
import com.moneytree.app.databinding.DialogUpdateBinding
import java.io.UnsupportedEncodingException


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

	fun shareOnFacebook(activity: Activity, msg: String) {
		try {
			val packageName = "com.facebook.katana"
			if (appInstalledOrNot(activity, packageName)) {
				val mIntentFacebook = Intent()
				mIntentFacebook.setClassName(
					packageName,
					"com.facebook.composer.shareintent.ImplicitShareIntentHandlerDefaultAlias"
				)
				mIntentFacebook.action = Intent.ACTION_SEND
				mIntentFacebook.type = "text/plain"
				mIntentFacebook.putExtra(Intent.EXTRA_TEXT, msg)
				activity.startActivity(mIntentFacebook)
			} else {
				shareOnFaceBook(activity, msg)
			}
		} catch (e: java.lang.Exception) {
			e.printStackTrace()
			shareOnFaceBook(activity, msg)
		}
	}

	private fun shareOnFaceBook(activity: Activity, message: String) {
		var mIntentFacebookBrowser = Intent(Intent.ACTION_SEND)
		mIntentFacebookBrowser.type = "text/plain"
		val mStringURL = "https://www.facebook.com/sharer/sharer.php?u=$message"
		mIntentFacebookBrowser = Intent(Intent.ACTION_VIEW, Uri.parse(mStringURL))
		activity.startActivity(mIntentFacebookBrowser)
	}

	fun shareOnWhatsApp(activity: Activity, message: String?, check: Boolean) {
		try {
			val packageName = "com.whatsapp"
			if (appInstalledOrNot(activity, packageName)) {
				val waIntent = Intent(Intent.ACTION_SEND)
				waIntent.type = "text/plain"
				waIntent.setPackage(packageName)
				waIntent.putExtra(Intent.EXTRA_TEXT, message)
				if (check) {
					activity.startActivity(Intent.createChooser(waIntent, "share"))
				} else {
					activity.startActivityForResult(waIntent, 1)
				}
			} else {
				Toast.makeText(activity, "WhatsApp Not Installed", Toast.LENGTH_LONG).show()
			}
		} catch (e: java.lang.Exception) {
			e.printStackTrace()
			Toast.makeText(activity, "WhatsApp Not Installed", Toast.LENGTH_LONG).show()
		}
	}

	fun showUpdateDialog(activity: Activity, isSkip: Boolean,urlLink:String?) {
		val builder = AlertDialog.Builder(activity)
		val view: View = activity.layoutInflater.inflate(R.layout.dialog_update, null)
		builder.setView(view)
		val bind: DialogUpdateBinding = DialogUpdateBinding.bind(view)
		val dialog = builder.create()
		if (!isSkip) {
			dialog.setCancelable(false)
			dialog.setCanceledOnTouchOutside(false)
		}
		dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		bind.tvCancel.setOnClickListener {
			dialog.dismiss()
			if (!isSkip) {
				activity.finish()
			}
		}
		bind.tvOk.setOnClickListener { v ->
			dialog.dismiss()
			activity.startActivity(
				//todo change
				Intent(
					Intent.ACTION_VIEW,
					Uri.parse(urlLink ?: ("https://play.google.com/store/apps/details?id=" + activity.packageName))
				)
			)
		}
		dialog.show()
	}

	fun showPopUpHome(activity: Activity, url: String) {
		val builder = AlertDialog.Builder(activity)
		val view: View = activity.layoutInflater.inflate(R.layout.dialog_popup_home, null)
		builder.setView(view)
		val bind: DialogPopupHomeBinding = DialogPopupHomeBinding.bind(view)
		val dialog = builder.create()
		dialog.setCancelable(false)
		dialog.setCanceledOnTouchOutside(false)
		dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		bind.ivClose.setOnClickListener {
			dialog.dismiss()
		}

		Glide.with(activity).load(url).into(bind.ivPopupDialog)
		if (url.isNotEmpty()) {
			dialog.show()
		}
	}

	// endregion
	// --------------------------------------
	// region System
	// --------------------------------------
	fun getDeviceInfo(context: Context): String {
		val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
		val manufacturer = Build.MANUFACTURER
		val model = Build.MODEL
		val versionName: String = try {
			val manager = context.packageManager
			val info = manager.getPackageInfo(context.packageName, 0)
			info.versionName
		} catch (e: java.lang.Exception) {
			"0.0"
		}
		return "udid=$androidId||name=$manufacturer||model=$model||version=$versionName"
	}

	fun decrypt(coded: String): String {
		var coded = coded
		coded = encryption(coded)
		var valueDecoded: ByteArray? = ByteArray(0)
		try {
			valueDecoded = Base64.decode(coded.toByteArray(charset("UTF-8")), Base64.DEFAULT)
		} catch (e: UnsupportedEncodingException) {
		}
		return String(valueDecoded!!)
	}

	private fun encryption(coded: String): String {
		var valueDecoded: ByteArray? = ByteArray(0)
		try {
			valueDecoded = Base64.decode(coded.toByteArray(charset("UTF-8")), Base64.DEFAULT)
		} catch (e: UnsupportedEncodingException) {
		}
		return String(valueDecoded!!)
	}
}
