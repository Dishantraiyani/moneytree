package com.moneytree.app.ui.mycart.kyc.nominee

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
import com.moneytree.app.common.utils.setPlaceholderAdapter
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.databinding.FragmentBankDetailBinding
import com.moneytree.app.databinding.FragmentNomineeDetailBinding
import com.moneytree.app.databinding.FragmentPersonalDetailBinding
import com.moneytree.app.ui.mycart.kyc.common.KycCommonViewModel


class NomineeDetailFragment : BaseViewModelFragment<KycCommonViewModel, FragmentNomineeDetailBinding>() {

    companion object {
        fun newInstance(bundle: Bundle?) = NomineeDetailFragment().apply {
            arguments = bundle
        }
    }

    override val viewModel: KycCommonViewModel by lazy {
        ViewModelProvider(this)[KycCommonViewModel::class.java]
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNomineeDetailBinding {
        return FragmentNomineeDetailBinding.inflate(inflater, container, false)
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
                ageTypeSpinner.setPlaceholderAdapter(resources.getStringArray(R.array.age_filter), requireContext()) {
                    selectedGender = it
                }

                cardDob.setSafeOnClickListener {
                    NSUtilities.selectDateOfBirth(requireActivity()) {
                        etDob.text = it
                    }
                }

                btnSubmit.setOnClickListener(object : SingleClickListener() {
                    override fun performClick(v: View?) {
                        val fullName = binding.etName.text.toString()
                        val mobile = binding.etPhone.text.toString()
                        val dob = binding.etDob.text.toString()

                        if (fullName.isEmpty()) {
                            showAlertDialog(activity.resources.getString(R.string.please_enter_name))
                            return
                        } else if (mobile.isEmpty()) {
                            showAlertDialog(activity.resources.getString(R.string.please_enter_mobile_no))
                            return
                        } else if (mobile.length < 10) {
                            showAlertDialog(activity.resources.getString(R.string.please_enter_valid_mobile_no))
                            return
                        } else if (dob.isEmpty()) {
                            showAlertDialog(activity.resources.getString(R.string.please_enter_email))
                            return
                        } else {
                            val map: HashMap<String, String> = hashMapOf()
                            map["nominee_name"] = fullName
                            map["nominee_mobile"] = mobile
                            map["nominee_gender"] = selectedGender?:""
                            map["nominee_dob"] = dob
                            map["nominee_relationship"] = ""

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