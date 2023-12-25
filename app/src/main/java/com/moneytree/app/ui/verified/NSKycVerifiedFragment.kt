package com.moneytree.app.ui.verified

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.utils.buildAlertDialog
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.databinding.LayoutCustomAlertDialogBinding
import com.moneytree.app.databinding.NsFragmentKycVerifiedBinding
import com.moneytree.app.ui.mycart.kyc.NSKycActivity

class NSKycVerifiedFragment : NSFragment() {
    private val viewModel: NSKycViewModel by lazy {
        ViewModelProvider(this)[NSKycViewModel::class.java]
    }
    private var _binding: NsFragmentKycVerifiedBinding? = null

    private val voucherBinding get() = _binding!!
    companion object {
        fun newInstance() = NSKycVerifiedFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentKycVerifiedBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return voucherBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(voucherBinding) {

        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(voucherBinding) {
            with(viewModel) {
                btnSubmit.setSafeOnClickListener {
                    checkKycStatus { s, b ->
                        if (b) {
                            activity.startActivity(Intent(activity, NSKycActivity::class.java))
                            activity.finish()
                        } else {
                            showAlertDialog(activity.resources.getString(R.string.kyc_verification), s)
                        }
                    }
                }
            }
        }
    }

    private fun showAlertDialog(title: String, message: String) {
        buildAlertDialog(activity, LayoutCustomAlertDialogBinding::inflate) { dialog, binding ->
            binding.apply {
                tvTitle.text = title
                tvSubTitle.text = message
                tvOk.text = activity.resources.getString(R.string.ok)
                tvCancel.text = activity.resources.getString(R.string.cancel)

                tvOk.setOnClickListener {
                    dialog.dismiss()
                }

                tvCancel.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
    }
}
