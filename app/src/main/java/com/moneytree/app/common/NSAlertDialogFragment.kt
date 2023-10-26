package com.moneytree.app.common

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.moneytree.app.R
import com.moneytree.app.common.callbacks.NSDialogClickCallback
import com.moneytree.app.databinding.LayoutCustomAlertDialogBinding
import org.greenrobot.eventbus.EventBus

/**
 * Dialog fragment responsible for showing the alert
 */
class NSAlertDialogFragment : DialogFragment() {
    private lateinit var mContext: Context

    companion object {
        private const val BUNDLE_KEY_TITLE = "title"
        private const val BUNDLE_KEY_MESSAGE = "message"
        private const val BUNDLE_KEY_POSITIVE_BUTTON_TEXT = "positiveButtonText"
        private const val BUNDLE_KEY_NEGATIVE_BUTTON_TEXT = "negativeButtonText"
        private const val BUNDLE_KEY_IS_CANCEL_NEEDED = "isCancelNeeded"
        private const val BUNDLE_KEY_ALERT_KEY = "alertKey"
        private var callback: NSDialogClickCallback? = null

        fun newInstance(
            title: String?,
            message: String,
            isCancelNeeded: Boolean,
            negativeButtonText: String?,
            positiveButtonText: String?,
            alertKey: String?,
            clickCallback: NSDialogClickCallback? = null
        ) = NSAlertDialogFragment().apply {
            callback = clickCallback
            arguments = bundleOf(
                BUNDLE_KEY_TITLE to title,
                BUNDLE_KEY_MESSAGE to message,
                BUNDLE_KEY_IS_CANCEL_NEEDED to isCancelNeeded,
                BUNDLE_KEY_NEGATIVE_BUTTON_TEXT to negativeButtonText,
                BUNDLE_KEY_POSITIVE_BUTTON_TEXT to positiveButtonText,
                BUNDLE_KEY_ALERT_KEY to alertKey
            )
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val view: View = requireActivity().layoutInflater.inflate(R.layout.layout_custom_alert_dialog, null)
        builder.setView(view)
        val bind: LayoutCustomAlertDialogBinding = LayoutCustomAlertDialogBinding.bind(view)
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val arguments = arguments
        arguments?.let {
            //title
            val title = arguments.getString(BUNDLE_KEY_TITLE, "")
            val message = arguments.getString(BUNDLE_KEY_MESSAGE, "")
            bind.tvTitle.text = title
            if (title.isNotEmpty()) {
                bind.tvTitle.visibility = View.VISIBLE
                if (title.equals(requireActivity().resources.getString(R.string.app_name))) {
                    bind.tvTitle.setTextColor(Color.parseColor("#eba94a"))
                }
            }

            //supporting text
            bind.tvSubTitle.text = message

            var positiveButtonText = arguments.getString(BUNDLE_KEY_POSITIVE_BUTTON_TEXT, "")
            if (positiveButtonText.isNullOrEmpty()) {
                positiveButtonText = getString(R.string.ok)
            }

            //positive button
            bind.tvOk.text = positiveButtonText
            bind.tvOk.setOnClickListener {
                dialog.dismiss()
                callback?.onClick(true)
                EventBus.getDefault().post(
                    NSAlertButtonClickEvent(
                        NSConstants.KEY_ALERT_BUTTON_POSITIVE,
                        arguments.getString(BUNDLE_KEY_ALERT_KEY, "")
                    )
                )
            }

            val isCancelButtonNeeded = arguments.getBoolean(BUNDLE_KEY_IS_CANCEL_NEEDED, false)
            if (isCancelButtonNeeded) {
                var negativeButtonText = arguments.getString(BUNDLE_KEY_NEGATIVE_BUTTON_TEXT, "")
                if (negativeButtonText.isNullOrEmpty()) {
                    negativeButtonText = getString(R.string.cancel)
                }

                //negative button
                bind.viewLine2.visibility = View.VISIBLE
                bind.tvCancel.visibility = View.VISIBLE
                bind.tvCancel.text = negativeButtonText
                bind.tvCancel.setOnClickListener {
                    dialog.dismiss()
                    callback?.onClick(false)
                    EventBus.getDefault().post(
                        NSAlertButtonClickEvent(
                            NSConstants.KEY_ALERT_BUTTON_NEGATIVE,
                            arguments.getString(BUNDLE_KEY_ALERT_KEY, "")
                        )
                    )
                }
            }
            isCancelable = false
        }
        return dialog
    }
}