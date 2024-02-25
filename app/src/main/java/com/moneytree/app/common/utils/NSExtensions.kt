package com.moneytree.app.common.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.moneytree.app.R
import com.moneytree.app.base.adapter.ViewBindingAdapter
import com.moneytree.app.base.clicks.DelayedClickListener
import com.moneytree.app.base.clicks.SafeClickListener
import com.moneytree.app.base.clicks.SingleClickListener
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSLog
import com.moneytree.app.databinding.LayoutSpinnerItemBinding
import com.moneytree.app.databinding.LayoutSpinnerItemDropDownBinding
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
): Intent {
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
fun <T : Activity> Fragment.switchResultActivity(
    launcher: ActivityResultLauncher<Intent?>,
    destination: Class<T>, bundle: Bundle? = null, flags: IntArray? = null
) {
    launcher.launch(getIntent(destination, bundle, flags))
}

fun <T : Activity> Activity.switchResultActivity(
    launcher: ActivityResultLauncher<Intent?>,
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
): Intent {
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
@Suppress("unused")
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

/**
 * Add text
 *
 * @param activity The activity's context
 * @param resource string file name
 * @param data data set
 * @return
 */
fun addText(activity: Activity, resource: Int, data: String): String {
    with(activity.resources) {
        return getString(resource, data)
    }
}

fun addAmount(activity: Activity, resource: Int, data: String): String {
    with(activity.resources) {
        return getString(resource, if (data.isNotEmpty()) (data.toDouble()/2).toString() else "0")
    }
}

fun setUserName(activity: Activity, userName: String): String {
    return userName.ifEmpty {
        activity.resources.getString(R.string.no_user_name)
    }
}

fun setMobile(activity: Activity, mobile: String): String {
    return mobile.ifEmpty {
        activity.resources.getString(R.string.no_mobile_added)
    }
}

fun setEmail(activity: Activity, email: String): String {
    return email.ifEmpty {
        activity.resources.getString(R.string.no_email_added)
    }
}

fun String.isInteger(): Boolean {
    try {
        this.toInt()
    } catch (e: java.lang.NumberFormatException) {
        return false
    } catch (e: NullPointerException) {
        return false
    }
    return true
}

fun <T : ViewBinding, D> RecyclerView.setupViewBindingAdapter(
    bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> T,
    onBind: (T, D) -> Unit,
    layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context),
    onLoadMore: (() -> Unit)? = null
): ViewBindingAdapter<T, D> {
    val adapter = ViewBindingAdapter(bindingInflater, onBind)
    this.adapter = adapter
    this.layoutManager = layoutManager

    // If pagination is enabled, add a scroll listener to load more data
    if (onLoadMore != null) {
        adapter.setOnLoadMoreListener(onLoadMore)
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) { // Scrolling downwards
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = (layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition() ?: 0

                    if (!adapter.isLoading && !adapter.isLastPage && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                        adapter.setLoadingState(true)
                        onLoadMore.invoke()
                    }
                }
            }
        })
    }

    return adapter
}

fun View.setSafeOnClickListener(onClickAction: () -> Unit) {
    setOnClickListener(SafeClickListener {
        onClickAction.invoke()
    })
}

fun View.setSingleClickListener(onClickAction: () -> Unit) {
    setOnClickListener(SingleClickListener {
        onClickAction.invoke()
    })
}

fun View.setDelayedOnClickListener(delay: Long, onClickAction: () -> Unit) {
    setOnClickListener(DelayedClickListener(delay) {
        onClickAction.invoke()
    })
}

fun EditText.addTextChangeListener(onTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged(s.toString())
        }

        override fun afterTextChanged(s: Editable?) {}
    })
}

@SuppressLint("NotifyDataSetChanged")
fun notifyAdapter(adapter: RecyclerView.Adapter<*>) {
    adapter.notifyDataSetChanged()
}

fun RecyclerView.setupWithAdapter(adapter: RecyclerView.Adapter<*>) {
    this.adapter = adapter
    layoutManager = LinearLayoutManager(context)
}

fun RecyclerView.setupWithAdapterAndCustomLayoutManager(adapter: RecyclerView.Adapter<*>, layoutManager: RecyclerView.LayoutManager) {
    this.adapter = adapter
    this.layoutManager = layoutManager
}

fun ImageView.setCircleImage(resource: Int = 0, url: String? = null) {
    Glide.with(NSApplication.getInstance().applicationContext).load(url?:resource).circleCrop().into(this)
}
fun ImageView.setImage(resource: Int = 0, url: String? = null) {
    Glide.with(NSApplication.getInstance().applicationContext).load(url?:resource).error(resource).into(this)
}


fun Spinner.setPlaceholderAdapter(
    items: Array<String>,
    context: Context,
    onItemSelectedListener: ((String?) -> Unit)?
) {

    // Create a custom adapter with the items
    val adapter = ArrayAdapter(context, R.layout.layout_spinner_item, android.R.id.text1, items)

    // Set the adapter to the Spinner
    this.adapter = adapter

    // Set the OnItemSelectedListener to handle item selection
    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val selectedItem = if (position >= 0) items[position] else null
            onItemSelectedListener?.invoke(selectedItem)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            onItemSelectedListener?.invoke(null)
        }
    }
}

fun Spinner.setPlaceholderAdapter(
    items: Array<String>,
    context: Context,
    isHideFirstPosition: Boolean,
    placeholderName: String? = null,
    onItemSelectedListener: ((String?) -> Unit)?
) {
    if (isHideFirstPosition) {
        if (items.isNotEmpty()) {
            items[0] = placeholderName ?: ""
        } else {
            items.toMutableList().add(placeholderName?:"")
        }
    }
    // Create a custom adapter with the items
    val adapter = object : ArrayAdapter<String>(context, R.layout.layout_spinner_item, android.R.id.text1, items) {
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getDropDownView(position, convertView, parent)
            if (isHideFirstPosition) {
                val bind = LayoutSpinnerItemDropDownBinding.bind(view)
                if (position == 0) {
                    bind.text1.visibility = View.GONE
                } else {
                    bind.text1.visibility = View.VISIBLE
                }
            }
            return view
        }

        override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {
            val view = super.getView(position, convertView, parent)
            val bind = LayoutSpinnerItemBinding.bind(view)
            if (isHideFirstPosition && position == 0) {
                bind.text1.setTextColor(ContextCompat.getColor(context, R.color.hint_color))
            } else {
                bind.text1.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
            return view
        }
    }

    // Set the drop-down layout style
    adapter.setDropDownViewResource(R.layout.layout_spinner_item_drop_down)

    // Set the adapter to the Spinner
    this.adapter = adapter

    // Set a default selection to the first item (placeholder item) if needed
    if (!isHideFirstPosition && items.isNotEmpty()) {
        this.setSelection(1, false)
    } else {
        this.setSelection(0, false)
    }

    // Set the OnItemSelectedListener to handle item selection
    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val selectedItem = if (position >= 0) items[position] else null
            onItemSelectedListener?.invoke(selectedItem)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            onItemSelectedListener?.invoke(null)
        }
    }
}

fun <T : ViewBinding> buildAlertDialog(
    context: Context,
    viewBindingInflater: (LayoutInflater) -> T,
    dialogSetup: (AlertDialog, T) -> Unit
) {
    val inflater = LayoutInflater.from(context)
    val binding = viewBindingInflater.invoke(inflater)

    val builder = AlertDialog.Builder(context)
    builder.setView(binding.root)
    builder.setCancelable(false)
    val dialog = builder.create()
    dialogSetup.invoke(dialog, binding)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    if (!dialog.isShowing) {
        dialog.show()
    }
}

fun AppCompatActivity.isLocationPermissionGranted(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun AppCompatActivity.requestLocationPermissions(requestCode: Int) {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        requestCode
    )
}

