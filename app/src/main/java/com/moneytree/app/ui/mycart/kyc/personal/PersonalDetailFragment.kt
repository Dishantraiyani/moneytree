package com.moneytree.app.ui.mycart.kyc.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.moneytree.app.R
import com.moneytree.app.base.fragment.BaseViewModelFragment
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.callbacks.NSDialogClickCallback
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.addTextChangeListener
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.common.utils.visible
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
        setPersonalDetail()
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
                        val aadharNO = binding.etAadharNo.text.toString()
                        val mobile = binding.etPhone.text.toString()
                        val email = binding.etEmail.text.toString()
                        val state = selectedState
                        val district = selectedDistrict
                        val city = binding.etCity.text.toString()
                        val dob = binding.etDob.text.toString()
                        val pinCode = binding.etPinCode.text.toString()
                        val address = binding.etAddress.text.toString()

                        if (fullName.isEmpty()) {
                            showAlertDialog(activity.resources.getString(R.string.please_enter_name))
                            return
                        } else if (aadharNO.isEmpty()) {
                            showAlertDialog(activity.resources.getString(R.string.please_enter_aadhar_no))
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
                            map["addhar_no"] = aadharNO

                            updateProfile(true, map) { isSuccess, message ->
                                showAlertDialog(message, object : NSDialogClickCallback {
                                    override fun onClick(isOk: Boolean) {
                                        if (isOk && isSuccess) {
                                            binding.btnSubmit.gone()
                                            etFullName.isEnabled = false
                                            etPhone.isEnabled = false
                                            etEmail.isEnabled = false
                                            stateSpinner.isEnabled = false
                                            districtSpinner.isEnabled = false
                                            etCity.isEnabled = false
                                            etPinCode.isEnabled = false
                                            etAddress.isEnabled = false
                                            cardDob.isEnabled = false
                                            etAadharNo.isEnabled = false
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


    private fun showSuggestions(suggestionsStr: MutableList<String>) {
        val adapter = ArrayAdapter(
            requireActivity(),
            R.layout.layout_spinner_item,
            suggestionsStr
        )
        val spinner = binding.districtSpinner

        spinner.setAdapter(adapter)
        /*if (suggestionsStr.isValidList()) {
            spinner.showDropDown()
        } else {
            spinner.dismissDropDown()
        }*/
    }

    private fun setPersonalDetail() {
        binding.apply {
            viewModel.apply {
                getDistrictList { districtList ->

                   /* stateSpinner.setPlaceholderAdapter(stateList.toTypedArray(), requireContext(), isHideFirstPosition = true, "Select State") {
                        selectedState = it?:""
                    }*/

                    /*districtSpinner.setPlaceholderAdapter(districtList.toTypedArray(), requireContext(), isHideFirstPosition = true, "Select District") {
                        selectedDistrict = it?:""
                    }*/

                    getUserDetail {
                        it.apply {
                            binding.etFullName.setText(fullName)
                            binding.etPhone.setText(mobile)
                            binding.etEmail.setText(email)
                            binding.etCity.setText(cityNameValue)
                            binding.etDob.text = dobValue
                            binding.etPinCode.setText(pinCodeValue)
                            binding.etAddress.setText(address)
                            binding.stateSpinner.text = stateNameValue
                            binding.districtSpinner.setText(districtNameValue)
                            binding.etAadharNo.setText(aadharNo)

                            viewModel.selectedDistrict = districtNameValue?:""
                            viewModel.selectedState = stateNameValue?:""
                            viewModel.selectedStateCode = districtList.find { names -> names.stateName == stateNameValue }?.stateName?:""

                            if (fullName?.isNotEmpty() == true &&
                                mobile?.isNotEmpty() == true &&
                                email?.isNotEmpty() == true &&
                                stateNameValue?.isNotEmpty() == true &&
                                districtNameValue?.isNotEmpty() == true &&
                                cityNameValue?.isNotEmpty() == true &&
                                dobValue?.isNotEmpty() == true &&
                                pinCodeValue?.isNotEmpty() == true &&
                                address?.isNotEmpty() == true &&
                                aadharNo?.isNotEmpty() == true
                            ) {
                                btnSubmit.gone()
                            } else {
                                btnSubmit.visible()
                            }

                            etFullName.isEnabled = fullName== null || fullName?.isEmpty() == true
                            etPhone.isEnabled = mobile == null || mobile?.isEmpty() == true
                            etEmail.isEnabled = email == null || email?.isEmpty() == true
                            stateSpinner.isEnabled = stateNameValue == null || stateNameValue?.isEmpty() == true
                            districtSpinner.isEnabled = districtNameValue == null || districtNameValue?.isEmpty() == true
                            etCity.isEnabled = cityNameValue == null || cityNameValue?.isEmpty() == true
                            etPinCode.isEnabled = pinCodeValue == null || pinCodeValue?.isEmpty() == true
                            etAddress.isEnabled = address == null || address?.isEmpty() == true
                            cardDob.isEnabled = dobValue == null || dobValue?.isEmpty() == true

                           /* val aPosition = stateList.indexOf(stateNameValue)
                            stateSpinner.setSelection(aPosition)*/

                           /* val dPosition = districtList.indexOf(districtNameValue)
                            districtSpinner.setSelection(dPosition)*/

                            /* cardFullName.isEnabled = fullName?.isEmpty() == true
                             cardMobile.isEnabled = mobile?.isEmpty() == true
                             cardEmail.isEnabled = email?.isEmpty() == true
                             cardState.isEnabled = stateNameValue?.isEmpty() == true
                             cardDistrict.isEnabled = districtNameValue?.isEmpty() == true
                             cardCity.isEnabled = cityNameValue?.isEmpty() == true
                             cardPinCode.isEnabled = pinCodeValue?.isEmpty() == true
                             cardAddress.isEnabled = address?.isEmpty() == true*/

                            /*stateSpinner.addTextChangeListener { searchText ->
                                val stateNameList = districtList.map { names -> names.stateName }
                                val set: Set<String> = HashSet<String>(stateNameList)
                                val list: MutableList<String> = arrayListOf()
                                list.addAll(set)
                                viewModel.selectedState = searchText
                                showSuggestions(list, true)
                            }*/

                            districtSpinner.addTextChangeListener { searchText ->
                                if (searchText.length > 2) {
                                    viewModel.selectedDistrict = searchText
                                    viewModel.selectedState = ""
                                    val list = districtList.map { names -> names.districtName }.filter { names -> names.startsWith(searchText, ignoreCase = true) }
                                    val set: Set<String> = HashSet<String>(list)
                                    val finalList: MutableList<String> = arrayListOf()
                                    finalList.addAll(set)
                                    showSuggestions(finalList)
                                }
                            }

                            districtSpinner.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
                                val newList = districtList.find { names -> names.districtName == viewModel.selectedDistrict }
                                binding.stateSpinner.text = newList?.stateName
                                viewModel.selectedState = newList?.stateName?:""
                                districtSpinner.dismissDropDown()
                            }
                        }
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