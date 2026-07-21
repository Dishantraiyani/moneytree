package com.moneytree.app.ui.onlineorder.placeorder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.base.fragment.BaseViewModelFragment
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSAlertButtonClickEvent
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSRequestCodes
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.callbacks.NSPaymentDetailCallback
import com.moneytree.app.common.callbacks.NSPaymentFragmentCallback
import com.moneytree.app.common.rozerpay.RazorpayUtility
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.increaseByPercent
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.databinding.FragmentPlaceOrderAddressBinding
import com.moneytree.app.databinding.LayoutPlaceOrderOptionsBinding
import com.moneytree.app.repository.network.responses.NSAddressCreateResponse
import com.moneytree.app.repository.network.responses.NSErrorPaymentResponse
import com.moneytree.app.repository.network.responses.NSSuccessResponse
import com.moneytree.app.repository.network.responses.PlaceOrderAddressCreateResponse
import com.moneytree.app.repository.network.responses.RozerModel
import com.moneytree.app.ui.onlineorder.OnlineOrderHelper
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.plus


class PlaceOrderAddressFragment : BaseViewModelFragment<PlaceOrderAddressViewModel, FragmentPlaceOrderAddressBinding>() {

	override val viewModel: PlaceOrderAddressViewModel by lazy {
		ViewModelProvider(this)[PlaceOrderAddressViewModel::class.java]
	}
	private var selectedAddress: NSAddressCreateResponse? = null
	private var paymentOptionBottomSheet: BottomSheetDialog? = null
	private var selectedPaymentType: String = NSConstants.PAYMENT_WALLET
	
	companion object {
		private var callback: NSPaymentFragmentCallback? = null
		fun newInstance(bundle: Bundle?, paymentCallback: NSPaymentFragmentCallback?) = PlaceOrderAddressFragment().apply {
			arguments = bundle
			callback = paymentCallback
		}
	}

	override fun getFragmentBinding(
		inflater: LayoutInflater,
		container: ViewGroup?
	): FragmentPlaceOrderAddressBinding {
		return FragmentPlaceOrderAddressBinding.inflate(inflater, container, false)
	}

	override fun setupViews() {
		super.setupViews()
		setPaymentCallback()
		viewCreated()
		setListener()
	}
	
	override fun observeViewModel() {
		super.observeViewModel()
		baseObserveViewModel(viewModel)
		
		viewModel.apply {
			isProductSendDataAvailable.observe(
				viewLifecycleOwner
			) { isProductSend ->
				if (isProductSend) {
					if (successResponse?.status == true) {
						showSuccessDialog(
							activity.resources.getString(R.string.app_name),
							successResponse?.message,
							NSConstants.PRODUCT_ONLINE_ORDER_SEND_CLICK
						)
					} else {
						showSuccessDialog(
							activity.resources.getString(R.string.app_name),
							successResponse?.message,
							""
						)
					}
				}
			}
		}
	}
	
	/**
	 * View created
	 */
	private fun viewCreated() {
		with(binding) {
			selectedAddress = NSApplication.getInstance().getSelectedAddress()//pref.placeOrderAddress//arguments?.getString(NSConstants.KEY_IS_SELECTED_ADDRESS)
			HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.checkout_details))
			setAddress()
			setTotalAmount()
		}
	}

	/**
	 * Set listener
	 */
	private fun setListener() {
		with(binding) {
			with(layoutHeader) {
				
				rbWallet.setOnCheckedChangeListener { button, isChecked ->
					selectedPaymentType = if(isChecked) NSConstants.PAYMENT_WALLET else NSConstants.PAYMENT_GATEWAY
					tvNotes.setVisibility(!isChecked && pref.onlineOrderChargePercentage != -1)
					tvWalletTitle.text = resources.getString(if (isChecked) R.string.available_wallet_amount else R.string.total_paid_amount)
					tvWalletAmount.text = if (isChecked) NSConstants.WALLET_BALANCE else viewModel.paymentAmountWithCharge.toString()
				}

				btnSubmit.setOnClickListener(object : SingleClickListener() {
					override fun performClick(v: View?) {
						if (selectedPaymentType == NSConstants.PAYMENT_WALLET) {
							placeOrder()
						} else if (selectedPaymentType == NSConstants.PAYMENT_GATEWAY) {
							val razorpayUtility = RazorpayUtility(requireActivity())
							val model = RozerModel()
							model.productName = "${OnlineOrderHelper.getOrderList().size} Products Selected"
							model.price = viewModel.paymentAmountWithCharge.toString()
							model.email = etEmail.text.toString()
							model.mobile = etMobile.text.toString()
							
							razorpayUtility.startPayment(model)
						}
					}
				})
			}
		}
	}

	private fun placeOrder(paymentId: String? = null, paymentData: String? = null) {
		binding.apply {
			val memberId = pref.userData?.data?.userName
			val fullName = etFullName.text.toString().trim()
			val mobile = etMobile.text.toString().trim()
			val email = etEmail.text.toString().trim()
			val address = etAddress.text.toString().trim()
			val pinCode = etPinCode.text.toString().trim()
			val city = etCity.text.toString().trim()
			val state = etState.text.toString().trim()
			val district = etDistrict.text.toString().trim()
			
			if (fullName.isEmpty()) {
				etFullName.error = "Enter Full Name"
				return
			} else if (mobile.isEmpty()) {
				etMobile.error = "Enter Mobile No."
				return
			} else if (mobile.isEmpty() || mobile.length < 10 || !NSUtilities.isValidMobile(mobile)) {
				etMobile.error = getString(R.string.please_enter_valid_mobile_no)
				return
			} else if (email.isEmpty()) {
				etEmail.error = "Enter Email Address."
				return
			} else if (address.isEmpty()) {
				etAddress.error = "Enter Address."
				return
			} else if (pinCode.isEmpty()) {
				etPinCode.error = "Enter PinCode"
				return
			} else if (city.isEmpty()) {
				etCity.error = "Enter City"
				return
			} else if (state.isEmpty()) {
				etState.error = "Enter State"
				return
			} else if (district.isEmpty()) {
				etDistrict.error = "Enter District"
				return
			}
			
			val map: HashMap<String, Any> = hashMapOf()
			map["member_id"] = memberId?:""
			map["full_name"] = fullName
			map["mobile"] = mobile
			map["email"] = email
			map["address"] = address
			map["pin_code"] = pinCode
			map["city"] = city
			map["district"] = district
			map["state"] = state
			
			if (!paymentId.isNullOrEmpty()) {
				map["order_id"] = paymentId
			}
			
			if (!paymentData.isNullOrEmpty()) {
				map["payment_data"] = paymentData
				map["payment_charge_percentage"] = pref.onlineOrderChargePercentage
				map["payment_with_charge_amount"] = viewModel.paymentAmountWithCharge.toString()
			}
			
			val productList = OnlineOrderHelper.getOrderList()
			if (productList.isNotEmpty()) {
				map["product_list"] = Gson().toJson(productList)
				viewModel.saveOnlineOrderCart(map, true)
			} else {
				Toast.makeText(activity, "Please Select Products", Toast.LENGTH_SHORT).show()
			}
		}
	}
	
	private fun setAddress() {
		binding.apply {
			val note = "Note: ${pref.onlineOrderChargePercentage}% Service charges will be deducted"
			tvNotes.text = note
			
			tvWalletAmount.text = NSConstants.WALLET_BALANCE
			tvMemberId.text = pref.userData?.data?.userName
			val userModel = pref.userData?.data
			if (selectedAddress != null && !selectedAddress?.fullName.isNullOrEmpty()) {
				val model: NSAddressCreateResponse? = selectedAddress
				
				val flatHouse = model?.flatHouse?.ifEmpty { "" }
				val addressStr =  if (!flatHouse.isNullOrEmpty()) flatHouse + ", " + model.area else model?.area?:""
				
				viewModel.selectedAddressModel = model
				etFullName.setText(model?.fullName?.ifEmpty { userModel?.fullName })
				etMobile.setText(model?.mobile?.ifEmpty { userModel?.mobile })
				etEmail.setText(userModel?.email?.ifEmpty { userModel.email })
				etAddress.setText(addressStr.ifEmpty { userModel?.address })
				etPinCode.setText(model?.pinCode?.ifEmpty { userModel?.pinCodeValue })
				etCity.setText(model?.city?.ifEmpty { userModel?.cityNameValue })
				etDistrict.setText(userModel?.districtNameValue?.ifEmpty { userModel.districtNameValue })
				etState.setText(model?.state?.ifEmpty { userModel?.stateNameValue })
				//etCountryName.setText(model?.country)
			} else if (pref.userData?.data != null) {
				val model = pref.userData?.data
				etFullName.setText(model?.fullName)
				etMobile.setText(model?.mobile)
				etEmail.setText(model?.email)
				etAddress.setText(model?.address)
				etPinCode.setText(model?.pinCodeValue)
				etCity.setText(model?.cityNameValue)
				etDistrict.setText(model?.districtNameValue)
				etState.setText(model?.stateNameValue)
			}
		}
	}
	
	private fun setTotalAmount() {
		binding.apply {
			var totalAmountValue = 0
			for (data in OnlineOrderHelper.getOrderList()) {
				val amount1: Int = data.rate?.toInt() ?: 0
				val finalAmount1 = data.itemQty * amount1
				totalAmountValue += finalAmount1
			}
			tvProductTitle.text = "${OnlineOrderHelper.getOrderList().size} Item Selected"
			tvAmount.text = addText(activity, R.string.price_value, totalAmountValue.toString())
			viewModel.finalPayoutAmount = totalAmountValue
			viewModel.paymentAmountWithCharge = totalAmountValue.increaseByPercent(if(pref.onlineOrderChargePercentage == -1) 0 else pref.onlineOrderChargePercentage)
		}
	}
	
	private fun showPaymentOption(callback: (String) -> Unit) {
		try {
			val sheetView: View = activity.layoutInflater
				.inflate(R.layout.layout_place_order_options, null)
			paymentOptionBottomSheet = BottomSheetDialog(activity, R.style.MyBottomSheetDialogTheme)
			paymentOptionBottomSheet?.setContentView(sheetView)
			paymentOptionBottomSheet?.setCanceledOnTouchOutside(false)
			paymentOptionBottomSheet?.show()
			val bind = LayoutPlaceOrderOptionsBinding.bind(sheetView)
			bind.btnMtCoin.gone()
			
			bind.tvCancel.setOnClickListener {
				paymentOptionBottomSheet?.dismiss()
			}
			
			bind.btnWallet.setOnClickListener {
				paymentOptionBottomSheet?.dismiss()
				callback.invoke(NSConstants.PAYMENT_WALLET)
			}
			
			bind.btnPaymentGateway.setOnClickListener {
				paymentOptionBottomSheet?.dismiss()
				callback.invoke(NSConstants.PAYMENT_GATEWAY)
			}
			
			bind.btnMtCoin.setOnClickListener {
				paymentOptionBottomSheet?.dismiss()
				callback.invoke(NSConstants.PAYMENT_MT_COIN)
			}
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
	
	private fun setPaymentCallback() {
		binding.apply {
			viewModel.apply {
				callback?.onResponse(object : NSPaymentDetailCallback {
					override fun onResponse(
						paymentId: String,
						paymentData: String,
						isSuccess: Boolean
					) {
						if (isSuccess) {
							placeOrder(paymentId, paymentData)
						} else {
							successResponse = if (paymentId.contains("description")) {
								val gson = Gson().fromJson(paymentId, NSErrorPaymentResponse::class.java)
								NSSuccessResponse(false, gson.error?.description)
							} else {
								NSSuccessResponse(false, paymentId)
							}
							
							if (!successResponse?.message.isNullOrEmpty()) {
								showError(successResponse?.message?:"")
							}
						}
					}
				})
			}
		}
	}
	
	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onPositiveButtonClickEvent(event: NSAlertButtonClickEvent) {
		if (event.buttonType == NSConstants.KEY_ALERT_BUTTON_POSITIVE && event.alertKey == NSConstants.PRODUCT_ONLINE_ORDER_SEND_CLICK) {
			val intent = Intent()
			activity.setResult(NSRequestCodes.REQUEST_PRODUCT_STOCK_UPDATE_DETAIL, intent)
			finish()
		}
	}
}
