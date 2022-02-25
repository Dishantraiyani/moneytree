package com.moneytree.app.ui.profile.edit

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.moneytree.app.R
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.databinding.NsFragmentAddRedeemBinding
import com.moneytree.app.databinding.NsFragmentEditBinding
import com.moneytree.app.databinding.NsFragmentTransferBinding
import com.moneytree.app.ui.login.NSLoginViewModel


class NSEditFragment : NSFragment() {
    private val editViewModel: NSEditViewModel by lazy {
        ViewModelProvider(this).get(NSEditViewModel::class.java)
    }
    private var _binding: NsFragmentEditBinding? = null
    private val adBinding get() = _binding!!

    companion object {
        fun newInstance() = NSEditFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentEditBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return adBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(adBinding) {
            with(editViewModel) {
                with(layoutHeader) {
                    clBack.visibility = View.VISIBLE
                    tvHeaderBack.text = activity.resources.getString(R.string.edit)
                    ivBack.visibility = View.VISIBLE
                }
                getUserDetail()
            }
        }
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(adBinding) {
            with(editViewModel) {
                with(layoutHeader) {
                    clBack.setOnClickListener {
                        onBackPress()
                    }

                    btnSubmit.setOnClickListener(object : SingleClickListener(){
                        override fun performClick(v: View?) {
                            btnSubmit.isEnabled = false
                            strFullName = etFullName.text.toString()
                            strAddress = etAddress.text.toString()
                            strEmail = etEmail.text.toString()
                            strMobile = etMobile.text.toString()
                            strPanNo = etPanNo.text.toString()
                            strBankIfsc = etBankIfsc.text.toString()
                            strBankName = etBankName.text.toString()
                            strAccNo = etAccountNo.text.toString()
                            updateProfile(true)
                        }
                    })
                }
            }
        }
    }

    private fun setUserDetail(isUserDetail: Boolean) {
        with(adBinding) {
            with(editViewModel) {
                with(nsUserData!!) {
                    etFullName.setText(fullName!!)
                    etAddress.setText(address)
                    etEmail.setText(email)
                    etMobile.setText(mobile)
                    etPanNo.setText(panno)
                    etBankIfsc.setText(ifscCode)
                    etBankName.setText(bankName)
                    etAccountNo.setText(acNo)

                    enableCard(etFullName, cardFullName, fullName.isNullOrEmpty())
                    enableCard(etAddress, cardAddress, address.isNullOrEmpty())
                    enableCard(etEmail, cardEmail, email.isNullOrEmpty())
                    enableCard(etMobile, cardMobile, mobile.isNullOrEmpty())
                    enableCard(etPanNo, cardPanNo, panno.isNullOrEmpty())
                    enableCard(etBankIfsc, cardBankIfsc, ifscCode.isNullOrEmpty())
                    enableCard(etBankName, cardBankName, bankName.isNullOrEmpty())
                    enableCard(etAccountNo, cardAccountNo, acNo.isNullOrEmpty())

                    btnSubmit.isEnabled = isUpdateProfileEnable
                }
            }
        }
    }

    private fun enableCard(tvText: EditText, card: CardView, isEnable: Boolean) {
        with(adBinding) {
            with(editViewModel) {
                if (isEnable) {
                    isUpdateProfileEnable = true
                }
                tvText.isEnabled = isEnable
                card.setCardBackgroundColor(Color.parseColor(if(isEnable) "#FFFFFF" else "#efefef"))
            }
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(adBinding) {
            with(editViewModel) {
                isProgressShowing.observe(
                    viewLifecycleOwner
                ) { shouldShowProgress ->
                    updateProgress(shouldShowProgress)
                }

                failureErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                    btnSubmit.isEnabled = isUpdateProfileEnable
                        showAlertDialog(errorMessage)
                }

                apiErrors.observe(viewLifecycleOwner) { apiErrors ->
                    btnSubmit.isEnabled = isUpdateProfileEnable
                    parseAndShowApiError(apiErrors)
                }

                isUserDataAvailable.observe(viewLifecycleOwner) { isUserData ->
                    setUserDetail(isUserData)
                }

                isProfileUpdated.observe(viewLifecycleOwner) {
                    if (it) {
                        finish()
                    }
                }

                noNetworkAlert.observe(viewLifecycleOwner) {
                    showNoNetworkAlertDialog(
                        getString(R.string.no_network_available),
                        getString(R.string.network_unreachable)
                    )
                }

                validationErrorId.observe(viewLifecycleOwner) { errorId ->
                    btnSubmit.isEnabled = isUpdateProfileEnable
                    showAlertDialog(getString(errorId))
                }
            }
        }
    }
}