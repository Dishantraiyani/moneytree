package com.moneytree.app.common.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spanned
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.constraintlayout.widget.Group
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.moneytree.app.R
import com.moneytree.app.common.NSLog
import java.text.DecimalFormat

/**
 * This is the file that contains the all the extensions functions.
 */

const val TAG: String = "NSExtensions"

/**
 * Used for switching from one activity to another with additional flags and bundle parameters
 *
 * @param destination destination screen to move the user
 * @param flags       flags needed to be set in the intent
 * @param bundle      additional information to be carried
 * @param <T>         represents the common template, able to get calling class using <T> format
 */
fun <T : Activity> Activity.switchActivity(
    destination: Class<T>, bundle: Bundle? = null, flags: IntArray? = null
) {
    startActivity(getIntent(destination, bundle, flags))
}

/**
 * Used for switching from one activity to another with additional flags and bundle parameters using request code to get results
 *
 * @param destination destination screen to move the user
 * @param flags       flags needed to be set in the intent
 * @param bundle      additional information to be carried
 * @param requestCode which is used to identify result
 * @param <T>         represents the common template, able to get calling class using <T> format
 */
fun <T : Activity> Activity.switchActivityForResult(
    destination: Class<T>, requestCode: Int, bundle: Bundle? = null, flags: IntArray? = null
) {
    startActivityForResult(getIntent(destination, bundle, flags), requestCode)
}

/**
 * To get the intent with the below parameter details attached to the intent
 *
 * @param destination destination screen to move the user
 * @param flags       flags needed to be set in the intent
 * @param bundle      additional information to be carried
 * @param <T>         represents the common template, able to get calling class using <T> format
 * @return the intent containing the parameter details
 */
fun <T : Activity> Activity.getIntent(
    destination: Class<T>, bundle: Bundle?, flags: IntArray?
): Intent? {
    return getIntent(this, destination, flags, bundle)
}

/**
 * To dismiss the keyboard
 */
fun Activity.hideKeyboard() {
    try {
        val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // check if no view has focus:
        val currentFocusedView = this.currentFocus
        currentFocusedView?.let {
            inputManager.hideSoftInputFromWindow(
                currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    } catch (exception: Exception) {
        NSLog.e(TAG, "hideKeyboard: ${exception.message}")
    }
}

/**
 * To show toast with the message corresponding to the string resource.
 *
 * @param stringId the id of string
 */
fun Activity.showToast(stringId: Int) {
    Toast.makeText(this, getString(stringId), Toast.LENGTH_SHORT).show()
}

// Extension functions for Fragment
/**
 * Used for switching to an activity from the fragment with additional flags and bundle parameters
 *
 * @param destination destination screen to move the user
 * @param flags       flags needed to be set in the intent
 * @param bundle      additional information to be carried
 * @param <T>         represents the common template, able to get calling class using <T> format
 */
fun <T : Activity> Fragment.switchActivity(
    destination: Class<T>, bundle: Bundle? = null, flags: IntArray? = null
) {
    startActivity(getIntent(destination, bundle, flags))
}

/**
 * Used for switching to an activity from the fragment with additional flags and bundle parameters
 *
 * @param destination destination screen to move the user
 * @param flags       flags needed to be set in the intent
 * @param bundle      additional information to be carried
 * @param <T>         represents the common template, able to get calling class using <T> format
 */
fun <T : Activity> Fragment.switchResultActivity(launcher: ActivityResultLauncher<Intent?>,
    destination: Class<T>, bundle: Bundle? = null, flags: IntArray? = null
) {
    launcher.launch(getIntent(destination, bundle, flags))
}

/**
 * To get the intent with the below parameter details attached to the intent
 *
 * @param destination destination screen to move the user
 * @param flags       flags needed to be set in the intent
 * @param bundle      additional information to be carried
 * @param <T>         represents the common template, able to get calling class using <T> format
 * @return the intent containing the parameter details
 */
fun <T : Activity> Fragment.getIntent(
    destination: Class<T>, bundle: Bundle?, flags: IntArray?
): Intent? {
    return getIntent(context, destination, flags, bundle)
}

/**
 * To get the intent with the below parameter details attached to the intent
 *
 * @param T represents the common template, able to get calling class using <T> format
 * @param context The context
 * @param destination destination screen to move the user
 * @param flags flags needed to be set in the intent
 * @param bundle additional information to be carried
 * @return
 */
private fun <T : Activity> getIntent(
    context: Context?,
    destination: Class<T>,
    flags: IntArray?,
    bundle: Bundle?
): Intent {
    val launchIntent = Intent(context, destination)
    if (flags != null) {
        for (flag in flags) {
            launchIntent.addFlags(flag)
        }
    }
    if (bundle != null) {
        launchIntent.putExtras(bundle)
    }
    return launchIntent
}

//Extension functions for Textview
/**
 * To get the text of textview as string
 *
 * @return textview data as string
 */
fun TextView.getString(): String = this.text.toString()

//Extension functions for String class
/**
 * To compare two strings with ignore case
 *
 * @param text the text to compare with this
 * @return boolean comparison result of two strings
 */
fun String?.equalsIgnoreCase(text: String?): Boolean = this.equals(text, true)

//Extension functions for List class
/**
 * To check whether the list is valid or not
 *
 * @return boolean determining the list is valid or not based on the list content and size
 */
fun List<*>?.isValidList(): Boolean {
    return this != null && this.isNotEmpty()
}

/**
 * To set the view visibility as [View.VISIBLE]
 */
fun View.visible() {
    this.visibility = View.VISIBLE
}

/**
 * To set the view visibility as [View.INVISIBLE]
 */
fun View.invisible() {
    this.visibility = View.INVISIBLE
}

/**
 * To set the view visibility as [View.GONE]
 */
fun View.gone() {
    this.visibility = View.GONE
}

/**
 * To set the group visibility as [Group.VISIBLE]
 */
fun Group.visible() {
    this.visibility = Group.VISIBLE
}

/**
 * To set the group visibility as [Group.INVISIBLE]
 */
fun Group.invisible() {
    this.visibility = Group.INVISIBLE
}

/**
 * To set the group visibility as [Group.GONE]
 */
fun Group.gone() {
    this.visibility = Group.GONE
}

/**
 * To set the [View] visibility based on the input value. It won't set [View.INVISIBLE]
 *
 * @param isVisible Is the view visible or not
 */
fun View.setVisibility(isVisible: Boolean) {
    if (isVisible) {
        this.visible()
    } else {
        this.gone()
    }
}

/**
 * To set the [Group] visibility based on the input value. It won't set [Group.INVISIBLE]
 *
 * @param isVisible Is the group visible or not
 */
fun Group.setVisibility(isVisible: Boolean) {
    if (isVisible) {
        this.visible()
    } else {
        this.gone()
    }
}

/**
 * To round-off the given decimal value
 *
 * @return Rounded off value
 */
fun Double?.roundOff(): String {
    return if (this == null) {
        0.00.toString()
    } else {
        try {
            val decimalFormat = DecimalFormat("#0.00")
            decimalFormat.format(this)
        } catch (e: NumberFormatException) {
            NSLog.e(TAG, "roundOff: Caught exception: " + e.message, e)
            this.toString()
        }
    }
}

/**
 * To concatenate the given lists
 *
 * @param T     The type of list
 * @param lists All input list
 * @return      concatenated single list
 */
fun <T> concatenate(vararg lists: List<T>): List<T> {
    return mutableListOf(*lists).flatten()
}

/**
 * To set the alpha value for all components under single [Group]
 *
 * @param alpha The alpha value
 */
fun Group.setAlphaForAll(alpha: Float) = referencedIds.forEach {
    rootView.findViewById<View>(it).alpha = alpha
}

/**
 * To round off the number with given decimals
 *
 * @param decimals Round off decimal value
 */
fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()

fun addText(activity: Activity, resource: Int,  data: String) : String {
    with(activity.resources) {
        return getString(resource, data)
    }
}

