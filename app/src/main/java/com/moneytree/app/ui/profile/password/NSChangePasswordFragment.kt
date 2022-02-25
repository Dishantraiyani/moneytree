package com.moneytree.app.ui.profile.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.databinding.NsFragmentChangePasswordBinding
import com.moneytree.app.databinding.NsFragmentEditBinding


class NSChangePasswordFragment : NSFragment() {
    private val changePasswordModel: NSChangePasswordViewModel by lazy {
        ViewModelProvider(this).get(NSChangePasswordViewModel::class.java)
    }
    private var _binding: NsFragmentChangePasswordBinding? = null
    private val cpBinding get() = _binding!!

    companion object {
        fun newInstance(bundle: Bundle?) = NSChangePasswordFragment().apply {
            arguments = bundle
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            with(changePasswordModel) {
                isChangePassword = it.getBoolean(NSConstants.KEY_CHANGE_PASSWORD)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentChangePasswordBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return cpBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(cpBinding) {
            with(changePasswordModel) {
                with(layoutHeader) {
                    clBack.visibility = View.VISIBLE
                    tvHeaderBack.text = activity.resources.getString(if (isChangePassword) R.string.change_password else R.string.change_tran_password)
                    ivBack.visibility = View.VISIBLE
                }
            }
        }
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(cpBinding) {
            with(changePasswordModel) {
                with(layoutHeader) {
                    clBack.setOnClickListener {
                        onBackPress()
                    }

                    btnSubmit.setOnClickListener(object : SingleClickListener() {
                        override fun performClick(v: View?) {
                            strCurrentPassword = etPassword.text.toString()
                            strNewPassword = etNewPassword.text.toString()
                            if (isValid()) {
                                btnSubmit.isEnabled = false
                                if (isChangePassword) {
                                    changePassword()
                                } else {
                                    changeTransPassword()
                                }
                            } else {
                                showAlertDialog(activity.resources.getString(R.string.please_enter_password))
                            }
                        }

                    })
                }
            }

        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(cpBinding) {
            with(changePasswordModel) {
                isProgressShowing.observe(
                    viewLifecycleOwner
                ) { shouldShowProgress ->
                    updateProgress(shouldShowProgress)
                }

                isChangeDataAvailable.observe(viewLifecycleOwner) { isChange ->
                    if (isChange) {
                        btnSubmit.isEnabled = true
                        onBackPress()
                    }
                }

                failureErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                    btnSubmit.isEnabled = true
                    showAlertDialog(errorMessage)
                }

                apiErrors.observe(viewLifecycleOwner) { apiErrors ->
                    btnSubmit.isEnabled = true
                    parseAndShowApiError(apiErrors)
                }

                noNetworkAlert.observe(viewLifecycleOwner) {
                    btnSubmit.isEnabled = true
                    showNoNetworkAlertDialog(
                        getString(R.string.no_network_available), getString(
                            R.string.network_unreachable
                        )
                    )
                }

                validationErrorId.observe(viewLifecycleOwner) { errorId ->
                    btnSubmit.isEnabled = true
                    showAlertDialog(getString(errorId))
                }
            }
        }
    }
}