package com.moneytree.app.common.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.Base64
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.moneytree.app.R
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSDateTimeHelper
import com.moneytree.app.common.NSUserManager
import com.moneytree.app.common.callbacks.NSDateRangeCallback
import com.moneytree.app.databinding.DialogPopupHomeBinding
import com.moneytree.app.databinding.DialogUpdateBinding
import com.moneytree.app.databinding.LayoutDateRangeSelectBinding
import com.moneytree.app.repository.network.responses.CityResponseItem
import com.moneytree.app.repository.network.responses.StateResponseItem
import com.moneytree.app.ui.mycart.kyc.NSKycActivity
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringWriter
import java.io.UnsupportedEncodingException
import java.io.Writer
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


/**
 * The utility class that handles tasks that are common throughout the application
 */
object NSUtilities {

	fun isValidMobile(phone: String): Boolean {
		return Patterns.PHONE.matcher(phone).matches()
	}

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
		val versionName: String? = try {
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

	fun callCustomerCare(context: Context, call: String) {
		val intent = Intent(Intent.ACTION_DIAL)
		intent.data = Uri.parse("tel:${call}")
		context.startActivity(intent)
	}

	fun setDateRange(
		layoutDateRange: LayoutDateRangeSelectBinding, list: Array<String>,
		activity: Activity, isFilterVisible: Boolean = false, callback: NSDateRangeCallback) {
		layoutDateRange.clFilterType.setVisibility(isFilterVisible)
		var startingDate = NSDateTimeHelper.getCurrentDate()
		var endingDate = NSDateTimeHelper.getCurrentDate()

		layoutDateRange.tvStartDate.text = startingDate
		layoutDateRange.tvEndDate.text = endingDate

		var startDateList = if (layoutDateRange.tvStartDate.text.isNotEmpty())  layoutDateRange.tvStartDate.text.split("-") else NSDateTimeHelper.getCurrentDate().split("-")
		var endDateList = if (layoutDateRange.tvEndDate.text.isNotEmpty())  layoutDateRange.tvEndDate.text.split("-") else NSDateTimeHelper.getCurrentDate().split("-")
		val filterListType: MutableList<String> = arrayListOf()
		filterListType.addAll(list)
		var selectedFilterType = filterListType[0]

		layoutDateRange.tvStartDate.setOnClickListener {
			startDateList = if (layoutDateRange.tvStartDate.text.isNotEmpty())  layoutDateRange.tvStartDate.text.split("-") else NSDateTimeHelper.getCurrentDate().split("-")
			val dpd = DatePickerDialog(activity, R.style.DialogTheme, { view, year, monthOfYear, dayOfMonth ->
				startingDate = "${getDateZero(dayOfMonth)}-${getDateZero(monthOfYear + 1)}-$year"
				layoutDateRange.tvStartDate.text = startingDate
				checkDateRange(activity, startingDate, endingDate, selectedFilterType, callback)
			}, startDateList[2].toInt(), startDateList[1].toInt() - 1, startDateList[0].toInt())
			dpd.show()
		}

		layoutDateRange.tvEndDate.setOnClickListener {
			endDateList = if (layoutDateRange.tvEndDate.text.isNotEmpty())  layoutDateRange.tvEndDate.text.split("-") else NSDateTimeHelper.getCurrentDate().split("-")
			val endDate = DatePickerDialog(activity, R.style.DialogTheme, { view, year, monthOfYear, dayOfMonth ->
				endingDate = "${getDateZero(dayOfMonth)}-${getDateZero(monthOfYear + 1)}-$year"
				layoutDateRange.tvEndDate.text = endingDate
				checkDateRange(activity, startingDate, endingDate, selectedFilterType,callback)
			}, endDateList[2].toInt(), endDateList[1].toInt() - 1, endDateList[0].toInt())
			endDate.show()
		}


		if (isFilterVisible) {
			val adapter = ArrayAdapter(activity, R.layout.layout_spinner, filterListType)
			layoutDateRange.statusTypeSpinner.adapter = adapter
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
			layoutDateRange.statusTypeSpinner.onItemSelectedListener =
				object : AdapterView.OnItemSelectedListener {
					override fun onItemSelected(
						p0: AdapterView<*>?, view: View?, position: Int, id: Long
					) {
						selectedFilterType = filterListType[position]
						callback.onDateRangeSelect(
							startingDate,
							endingDate,
							filterListType[position]
						)
					}

					override fun onNothingSelected(p0: AdapterView<*>?) {
					}
				}
		} else {
			callback.onDateRangeSelect(startingDate, endingDate, "All")
		}
	}

	private fun getDateZero(value: Int): String {
		return if (value < 10) {
			if (value == 0) {
				"01"
			} else {
				"0$value"
			}
		} else {
			value.toString()
		}
	}

	private fun checkDateRange(activity: Activity, startingDate: String, endingDate: String, type: String, callback: NSDateRangeCallback) {
		val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
		val strDate: Date = sdf.parse(startingDate)?: Date()
		val endDate: Date = sdf.parse(endingDate)?: Date()
		if (strDate.before(endDate) || strDate == endDate) {
			callback.onDateRangeSelect(startingDate, endingDate, type)
		} else {
			Toast.makeText(activity, "Please Select Valid Date", Toast.LENGTH_SHORT).show()
		}
	}

	fun hideKeyboard(activity: Activity, currentFocus: View) {
		currentFocus.let { view ->
			val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
			imm?.hideSoftInputFromWindow(view.windowToken, 0)
		}
	}

	fun selectDateOfBirth(activity: Activity, callback: ((String) -> Unit)) {
		var startingDate: String
		val startDateList = NSDateTimeHelper.getCurrentDate().split("-")
		val dpd = DatePickerDialog(activity, R.style.DialogTheme, { view, year, monthOfYear, dayOfMonth ->
			startingDate = "${getDateZero(dayOfMonth)}/${getDateZero(monthOfYear + 1)}/$year"
			callback.invoke(startingDate)
		}, startDateList[2].toInt(), startDateList[1].toInt() - 1, startDateList[0].toInt())
		dpd.show()
	}

	fun selectMeetingDate(activity: Activity, callback: ((String) -> Unit)) {
		var startingDate: String
		val startDateList = NSDateTimeHelper.getCurrentDate().split("-")
		val dpd = DatePickerDialog(activity, R.style.DialogTheme, { view, year, monthOfYear, dayOfMonth ->
			startingDate = "$year-${getDateZero(monthOfYear + 1)}-${getDateZero(dayOfMonth)}"
			callback.invoke(startingDate)
		}, startDateList[2].toInt(), startDateList[1].toInt() - 1, startDateList[0].toInt())
		dpd.show()
	}

	fun selectTime(activity: Activity, callback: ((String) -> Unit)) {
		val currentTime = Calendar.getInstance()
		val hour = currentTime.get(Calendar.HOUR_OF_DAY)
		val minute = currentTime.get(Calendar.MINUTE)

		val timePickerDialog = TimePickerDialog(
			activity,
			R.style.DialogTheme,
			{ _, selectedHour, selectedMinute ->
				val formattedTime = "$selectedHour:$selectedMinute:00"
				callback.invoke(formattedTime)
			},
			hour,
			minute,
			true
		)

		timePickerDialog.show()
	}

	fun checkUserVerified(activity: Activity, callback: (Boolean) -> Unit) {
		callback.invoke(true)
		/*if (status.equals("pending")) {
			activity.startActivity(Intent(activity, NSKycVerifiedActivity::class.java))
			activity.finish()
		} else*/
		/*if (status.equals("verified") || status.equals("pending") || skip) {
			callback.invoke(true)
		} else {
			activity.startActivity(Intent(activity, NSKycActivity::class.java))
			activity.finish()
		}*/
	}

	fun isKycVerified(activity: Activity, isPending: Boolean = false): Boolean {
		val pref = NSApplication.getInstance().getPrefs()
		val status = pref.isKycVerified
		return if (status.equals("verified") || (status.equals("pending") && isPending)) {
			true
		} else {
			activity.startActivity(Intent(activity, NSKycActivity::class.java))
			false
		}
	}

	fun checkKycVerified(): Boolean {
		val pref = NSApplication.getInstance().getPrefs()
		val status = pref.isKycVerified
		return status.equals("verified") || (status.equals("pending"))
	}

	fun capitalizeEveryFirstLetter(input: String?): String {
		val regex = "\\b\\w".toRegex()
		return regex.replace(input?:"") { it.value.uppercase() }
	}

	fun getBoldTexts(originalText: String, textView: TextView) {
		val wordArrayList = arrayListOf("Talktime:", "Validity:", "Voice:", "SMS:", "SMS :", "Data:", "Validity :")
		val spannableStringBuilder = SpannableStringBuilder(originalText)

		// Iterate through the wordArrayList and set the style for each word
		for (word in wordArrayList) {
			val startIndex = originalText.indexOf(word)
			if (startIndex != -1) {
				val endIndex = startIndex + word.length
				spannableStringBuilder.setSpan(
					StyleSpan(Typeface.BOLD),
					startIndex,
					endIndex,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
				)
			}
		}
		textView.text = spannableStringBuilder
	}

	private fun commonJsonResponse(activity: Activity, rawFile: Int): String {
		val inputStream: InputStream = activity.resources.openRawResource(rawFile)
		val writer: Writer = StringWriter()
		val buffer = CharArray(1024)
		inputStream.use { inputStream ->
			val reader: Reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
			var n: Int
			while (reader.read(buffer).also { n = it } != -1) {
				writer.write(buffer, 0, n)
			}
		}

		return writer.toString()
	}

	fun getStateRowData(activity: Activity): MutableList<StateResponseItem> {
		val jsonString: String = commonJsonResponse(activity, R.raw.states)
		val type: Type = object : TypeToken<List<StateResponseItem?>?>() {}.type
		return Gson().fromJson(jsonString, type)
	}

	fun getCityRowData(activity: Activity): MutableList<CityResponseItem> {
		val jsonString: String = commonJsonResponse(activity, R.raw.cities)
		val type: Type = object : TypeToken<List<CityResponseItem?>?>() {}.type
		return Gson().fromJson(jsonString, type)
	}
}
