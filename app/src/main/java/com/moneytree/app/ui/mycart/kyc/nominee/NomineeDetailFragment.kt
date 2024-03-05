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
import com.moneytree.app.common.callbacks.NSDialogClickCallback
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.setPlaceholderAdapter
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.FragmentBankDetailBinding
import com.moneytree.app.databinding.FragmentNomineeDetailBinding
import com.moneytree.app.databinding.FragmentPersonalDetailBinding
import com.moneytree.app.ui.mycart.kyc.common.KycCommonViewModel


class NomineeDetailFragment :
    BaseViewModelFragment<KycCommonViewModel, FragmentNomineeDetailBinding>() {

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
        setPersonalDetail()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(binding) {
            with(viewModel) {
                val genderSelect = resources.getStringArray(R.array.age_filter)
                selectedGender = genderSelect[0]
                ageTypeSpinner.setPlaceholderAdapter(
                    resources.getStringArray(R.array.age_filter),
                    requireContext()
                ) {
                    if (it != null) {
                        selectedGender = it
                    }
                }

                spinnerRelation.setPlaceholderAdapter(
                    resources.getStringArray(R.array.relation_filter),
                    requireContext(), isHideFirstPosition = true, "Select Relation"
                ) {
                    etRelationOther.setText("")
                    if (it.equals("Select Relation")) {
                        cardRelationOther.gone()
                        selectedRelation = ""
                    } else if (it.equals("Other")) {
                        cardRelationOther.visible()
                    } else {
                        cardRelationOther.gone()
                        selectedRelation = it
                    }
                }
                spinnerRelation.prompt = "Select Relation"
//state_name district_name
                cardDob.setSafeOnClickListener {
                    NSUtilities.hideKeyboard(activity, clNomineeView)
                    NSUtilities.selectDateOfBirth(requireActivity()) {
                        etDob.text = it
                    }
                }

                btnSubmit.setOnClickListener(object : SingleClickListener() {
                    override fun performClick(v: View?) {
                        val fullName = binding.etName.text.toString()
                        val mobile = binding.etPhone.text.toString()
                        val dob = binding.etDob.text.toString()
                        var relation = selectedRelation ?: ""
                        if (relation?.isEmpty() == true && etRelationOther.text.toString()
                                .isNotEmpty()
                        ) {
                            relation = etRelationOther.text.toString()
                        }

                        if (fullName.isEmpty()) {
                            showAlertDialog(activity.resources.getString(R.string.please_enter_name))
                            return
                        } else if (mobile.isEmpty()) {
                            showAlertDialog(activity.resources.getString(R.string.please_enter_mobile_no))
                            return
                        } else if (mobile.length < 10) {
                            showAlertDialog(activity.resources.getString(R.string.please_enter_valid_mobile_no))
                            return
                        } else if ((selectedGender?:"").isEmpty()) {
                            showAlertDialog(activity.resources.getString(R.string.please_select_gender))
                            return
                        } else if (dob.isEmpty()) {
                            showAlertDialog(activity.resources.getString(R.string.please_enter_dob))
                            return
                        } else if (relation.isEmpty()) {
                            showAlertDialog(activity.resources.getString(R.string.please_select_relation))
                            return
                        } else {
                            val map: HashMap<String, String> = hashMapOf()
                            map["nominee_name"] = fullName
                            map["nominee_mobile"] = mobile
                            map["nominee_gender"] = selectedGender ?: ""
                            map["nominee_dob"] = dob
                            map["nominee_relationship"] = relation

                            updateProfile(true, map) { isSuccess, message ->
                                showAlertDialog(message, object : NSDialogClickCallback {
                                    override fun onClick(isOk: Boolean) {
                                        if (isOk && isSuccess) {
                                            binding.btnSubmit.gone()

                                            etName.isEnabled = false
                                            etPhone.isEnabled = false
                                            cardDob.isEnabled = false
                                            ageTypeSpinner.isEnabled = false
                                            spinnerRelation.isEnabled = false
                                            etRelationOther.isEnabled = false
                                        }
                                    }
                                })
                            }
                        }
                    }
                })
            }
        }
    }

    private fun setPersonalDetail() {
        binding.apply {
            viewModel.apply {
                getUserDetail {
                    it.apply {
                        binding.etName.setText(nomineeNameValue)
                        binding.etPhone.setText(nomineeMobileValue)
                        binding.etDob.text = nomineeDobValue

                        if (nomineeNameValue?.isNotEmpty() == true &&
                            nomineeMobileValue?.isNotEmpty() == true &&
                            nomineeDobValue?.isNotEmpty() == true &&
                            nomineeRelationshipValue?.isNotEmpty() == true &&
                            nomineeGenderValue?.isNotEmpty() == true
                        ) {
                            btnSubmit.gone()
                        } else {
                            btnSubmit.visible()
                        }

                        etName.isEnabled = nomineeNameValue == null || nomineeNameValue?.isEmpty() == true
                        etPhone.isEnabled = nomineeMobileValue == null || nomineeMobileValue?.isEmpty() == true
                        cardDob.isEnabled = nomineeDobValue == null || nomineeDobValue?.isEmpty() == true
                        ageTypeSpinner.isEnabled = nomineeGenderValue == null || nomineeGenderValue?.isEmpty() == true
                        spinnerRelation.isEnabled = nomineeRelationshipValue == null || nomineeRelationshipValue?.isEmpty() == true

                        val rFilter = resources.getStringArray(R.array.relation_filter)
                        val aFilter = resources.getStringArray(R.array.age_filter)

                        if (!rFilter.contains(nomineeRelationshipValue) && nomineeRelationshipValue?.isNotEmpty() == true) {
                            binding.etRelationOther.setText(nomineeRelationshipValue)
                            binding.cardRelationOther.visible()
                        } else {
                            val defaultPosition = rFilter.indexOf(nomineeRelationshipValue)
                            spinnerRelation.setSelection(defaultPosition)
                            cardRelationOther.gone()
                        }

                        val aPosition = aFilter.indexOf(nomineeDobValue)
                        ageTypeSpinner.setSelection(aPosition)
                    }
                }
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