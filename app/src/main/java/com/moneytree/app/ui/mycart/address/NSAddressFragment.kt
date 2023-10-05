package com.moneytree.app.ui.mycart.address

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.addText
import com.moneytree.app.databinding.NsFragmentAddressBinding
import com.moneytree.app.repository.network.responses.NSAddressCreateResponse
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NSAddressFragment : NSFragment() {
	private var _binding: NsFragmentAddressBinding? = null
	private val binding get() = _binding!!
	private var isAddAddress = false
	private var selectedAddress: String? = null

	companion object {
		fun newInstance(bundle: Bundle?) = NSAddressFragment().apply {
			arguments = bundle
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = NsFragmentAddressBinding.inflate(inflater, container, false)
		viewCreated()
		setListener()
		return binding.root
	}

	/**
	 * View created
	 */
	private fun viewCreated() {
		with(binding) {
			isAddAddress = arguments?.getBoolean(NSConstants.KEY_IS_ADD_ADDRESS)?:false
			selectedAddress = arguments?.getString(NSConstants.KEY_IS_SELECTED_ADDRESS)
			HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(if (isAddAddress) R.string.add_address else R.string.edit_address))
			if (!isAddAddress) {
				setAddress()
			}
		}
	}

	/**
	 * Set listener
	 */
	private fun setListener() {
		with(binding) {
			with(layoutHeader) {

				btnSubmit.setOnClickListener(object : SingleClickListener() {
					override fun performClick(v: View?) {
						val fullName = etFullName.text.toString()
						val mobile = etMobile.text.toString()
						val flatHouse = etFlatHouse.text.toString()
						val area = etArea.text.toString()
						val landMark = etLandMark.text.toString()
						val pinCode = etPinCode.text.toString()
						val city = etCity.text.toString()
						val state = etState.text.toString()
						val country = etCountryName.text.toString()

						if (fullName.isEmpty()) {
							etFullName.error = "Enter Full Name"
							return
						} else if (mobile.isEmpty()) {
							etMobile.error = "Enter Mobile No."
							return
						} else if (mobile.isEmpty() || mobile.length < 10 || !NSUtilities.isValidMobile(mobile)) {
							etMobile.error = getString(R.string.please_enter_valid_mobile_no)
							return
						} else if (flatHouse.isEmpty()) {
							etFlatHouse.error = "Enter Flat No."
							return
						} else if (pinCode.isEmpty()) {
							etPinCode.error = "Enter PinCode"
							return
						} else if (country.isEmpty()) {
							etCountryName.error = "Enter Country"
							return
						} else if (state.isEmpty()) {
							etState.error = "Enter State"
							return
						} else if (city.isEmpty()) {
							etCity.error = "Enter City"
							return
						}

						val model = NSAddressCreateResponse(fullName, mobile, flatHouse, area, landMark, pinCode, city, state, country)
						if (cbChecked.isChecked || !isAddAddress) {
							pref.selectedAddress = model
						}
						NSApplication.getInstance().addSelectedAddress(model)
						requireActivity().setResult(RESULT_OK)
						finish()
					}
				})
			}
		}
	}

	private fun setAddress() {
		binding.apply {
			if (selectedAddress != null && selectedAddress?.isNotEmpty() == true) {
				val model: NSAddressCreateResponse = Gson().fromJson(selectedAddress, NSAddressCreateResponse::class.java)
				etFullName.setText(model.fullName)
				etMobile.setText(model.mobile)
				etCountryName.setText(model.country)
				etArea.setText(model.area)
				etCity.setText(model.city)
				etFlatHouse.setText(model.flatHouse)
				etLandMark.setText(model.landMark)
				etPinCode.setText(model.pinCode)
				etState.setText(model.state)
			}
		}
	}
}
