package com.moneytree.app.ui.mycart.kyc.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.moneytree.app.R
import com.moneytree.app.base.fragment.BaseViewModelFragment
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.databinding.FragmentPersonalDetailBinding
import com.moneytree.app.ui.mycart.kyc.common.KycCommonViewModel


class PersonalDetailFragment : BaseViewModelFragment<KycCommonViewModel, FragmentPersonalDetailBinding>() {

    companion object {
        fun newInstance(bundle: Bundle?) = PersonalDetailFragment().apply {
            arguments = bundle
        }
    }

    override val viewModel: KycCommonViewModel by lazy {
        ViewModelProvider(this)[KycCommonViewModel::class.java]
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPersonalDetailBinding {
        return FragmentPersonalDetailBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        viewCreated()
        setListener()
    }

    /**
     * View created
     */
    private fun viewCreated() {
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(binding) {
            with(viewModel) {
                cardDob.setSafeOnClickListener {
                    NSUtilities.selectDateOfBirth(requireActivity()) {
                        etDob.text = it
                    }
                }

                btnSubmit.setOnClickListener(object : SingleClickListener() {
                    override fun performClick(v: View?) {
                        val fullName = binding.etFullName.text.toString()
                        val mobile = binding.etPhone.text.toString()
                        val email = binding.etEmail.text.toString()
                        val state = binding.etState.text.toString()
                        val district = binding.etDistrict.text.toString()
                        val city = binding.etCity.text.toString()
                        val dob = binding.etDob.text.toString()
                        val pinCode = binding.etPinCode.text.toString()
                        val address = binding.etAddress.text.toString()

                        if (fullName.isEmpty()) {
                            showAlertDialog(activity.resources.getString(R.string.please_enter_name))
                            return
                        } else if (mobile.isEmpty()) {
                            showAlertDialog(activity.resources.getString(R.string.please_enter_mobile_no))
                            return
                        } else if (mobile.length < 10) {
                            showAlertDialog(activity.resources.getString(R.string.please_enter_valid_mobile_no))
                            return
                        } else if (email.isEmpty()) {
                            showAlertDialog(activity.resources.getString(R.string.please_enter_email))
                            return
                        } else if (state.isEmpty()) {
                            showAlertDialog(activity.resources.getString(R.string.please_enter_state))
                            return
                        } else if (district.isEmpty()) {
                            showAlertDialog(activity.resources.getString(R.string.please_enter_district))
                            return
                        } else if (city.isEmpty()) {
                            showAlertDialog(activity.resources.getString(R.string.please_enter_city))
                            return
                        } else if (dob.isEmpty()) {
                            showAlertDialog(activity.resources.getString(R.string.please_enter_dob))
                            return
                        } else if (pinCode.isEmpty()) {
                            showAlertDialog(activity.resources.getString(R.string.please_enter_pincode))
                            return
                        } else if (address.isEmpty()) {
                            showAlertDialog(activity.resources.getString(R.string.please_enter_address))
                            return
                        } else {
                            val map: HashMap<String, String> = hashMapOf()
                            map["fullname"] = fullName
                            map["mobile"] = mobile
                            map["email"] = email
                            map["state_name"] = state
                            map["district_name"] = district
                            map["city_name"] = city
                            map["dob"] = dob
                            map["pincode"] = pinCode
                            map["address"] = address

                            updateProfile(true, map) {
                                binding.btnSubmit.gone()
                            }
                        }
                    }
                })
            }
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        with(binding) {
            with(viewModel) {
                isProgressShowing.observe(
                    viewLifecycleOwner
                ) { shouldShowProgress ->
                    updateProgress(shouldShowProgress)
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