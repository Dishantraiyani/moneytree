package com.moneytree.app.ui.mycart.purchaseComplete

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
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSAlertButtonClickEvent
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.NSRequestCodes
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.databinding.NsFragmentPurchaseCompleteBinding
import com.moneytree.app.enums.PaymentMode
import com.moneytree.app.enums.WalletTypeMode
import com.moneytree.app.enums.calculatePaymentAmount
import com.moneytree.app.enums.getPaymentModeList
import com.moneytree.app.enums.getWalletTypeList
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NSPurchaseFragment : NSFragment() {
	private val productModel: NSPurchaseViewModel by lazy {
		ViewModelProvider(this)[NSPurchaseViewModel::class.java]
	}
	private var _binding: NsFragmentPurchaseCompleteBinding? = null

	private val purchaseCompleteBinding get() = _binding!!

	companion object {
		fun newInstance() = NSPurchaseFragment()
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = NsFragmentPurchaseCompleteBinding.inflate(inflater, container, false)
		viewCreated()
		setListener()
		return purchaseCompleteBinding.root
	}

	/**
	 * View created
	 */
	private fun viewCreated() {
		with(purchaseCompleteBinding) {
			HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString( R.string.repurchase))
		}
		observeViewModel()
		setWalletTypes()
		setPaymentMods()
		setTotalAmount()
	}

	/**
	 * Set listener
	 */
	private fun setListener() {
		with(productModel) {
			with(purchaseCompleteBinding) {
				with(layoutHeader) {

					etMemberId.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							getMemberDetail(etMemberId.text.toString(), true)
							return@OnEditorActionListener true
						}
						false
					})

					btnSubmit.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							if (etMemberId.text.toString().isEmpty()) {
								etMemberId.error = "Enter Member Id"
								return
							} else if (selectedWalletType.isEmpty()) {
								Toast.makeText(activity, "Please Select Wallet Type", Toast.LENGTH_SHORT).show()
								return
							} else if (selectedPaymentMod.isEmpty()) {
								Toast.makeText(activity, "Please Select Payment Mode", Toast.LENGTH_SHORT).show()
								return
							} else if (etMemberName.text.toString().isEmpty()) {
								Toast.makeText(activity, "Please Enter Valid Member Id", Toast.LENGTH_SHORT).show()
								return
							}
							val memberId = etMemberId.text.toString()
							val remark = etRemark.text.toString()
							val productList = NSApplication.getInstance().getProductList()
							if (productList.isNotEmpty()) {
								saveMyCart(memberId, selectedWalletType, selectedPaymentMod, remark, Gson().toJson(productList), true)
							} else {
								Toast.makeText(activity, "Please Select Product", Toast.LENGTH_SHORT).show()
							}
						}
					})
				}
			}
		}
	}

	private fun setTotalAmount() {
		with(purchaseCompleteBinding) {
			var totalAmountValue = 0
			for (data in NSApplication.getInstance().getProductList()) {
				val amount1: Int = data.rate?.toInt() ?: 0
				val finalAmount1 = data.itemQty * amount1
				totalAmountValue += finalAmount1
			}
			tvProductTitle.text = "${NSApplication.getInstance().getProductList().size} Item Selected"
			
			tvProductQty.text = NSApplication.getInstance().getProductList().size.toString()
			tvTotalAmount.text = addText(activity, R.string.price_value, totalAmountValue.toString())
			productModel.finalAmount = totalAmountValue.toString()
		}
	}
	
	private fun setPaymentMode() {
		purchaseCompleteBinding.apply {
			llPaymentMode.setVisibility(productModel.selectedPaymentMod.isNotEmpty())
			tvPaymentMode.text = productModel.selectedPaymentMod
			setWalletAndCreditAmount()
		}
	}
	
	private fun setUsedFrom() {
		purchaseCompleteBinding.apply {
			llUsedFrom.setVisibility(productModel.selectedWalletType.isNotEmpty())
			tvUsedFrom.text = productModel.selectedWalletType
			setWalletAndCreditAmount()
		}
	}
	
	private fun setWalletAndCreditAmount() {
		purchaseCompleteBinding.apply {
			val selfBalance = NSConstants.WALLET_BALANCE.toDoubleOrNull()?.toInt()?:0
			val walletAmount = productModel.memberDetailModel?.walletAmount?.toDoubleOrNull()?.toInt()?:0
			val creditAmount = productModel.memberDetailModel?.ovCreditAmount?.toDoubleOrNull()?.toInt()?:0
			val productAmount = productModel.finalAmount.toDoubleOrNull()?.toInt()?:0
			
			val selectedType = PaymentMode.fromValue(productModel.selectedPaymentMod)
			if (selectedType != null) {
				val walletType = WalletTypeMode.fromValue(productModel.selectedWalletType)
				val finalWalletBalance = if (walletType == WalletTypeMode.SELF) selfBalance else walletAmount
				val paymentAmount = calculatePaymentAmount(productAmount, selectedType, finalWalletBalance, creditAmount)
				
				llWalletAmount.setVisibility(paymentAmount.walletAmount != 0 && selectedType == PaymentMode.WALLET)
				tvWalletAmount.text = addText(activity, R.string.price_value, paymentAmount.walletAmount.toString())
				
				llOvCreditAmount.setVisibility(paymentAmount.creditAmount != 0 && selectedType == PaymentMode.CREDIT)
				tvOvCreditAmount.text = addText(activity, R.string.price_value, paymentAmount.creditAmount.toString())
				
				llCash.setVisibility(paymentAmount.cashAmount != 0)
				tvCashAmount.text = addText(activity, R.string.price_value, paymentAmount.cashAmount.toString())
			} else {
				llWalletAmount.setVisibility(false)
				llOvCreditAmount.setVisibility(false)
				llOvCreditAmount.setVisibility(false)
			}
		}
	}

	private fun setWalletTypes() {
		with(productModel) {
			with(purchaseCompleteBinding) {
				selectedWalletType = ""
				val walletListType: MutableList<String> = arrayListOf()
				
				val selectedType = PaymentMode.fromValue(productModel.selectedPaymentMod)
				if (selectedType == PaymentMode.CREDIT) {
					walletListType.addAll(getWalletTypeList(true))
					selectedWalletType = WalletTypeMode.SELF.value
					setUsedFrom()
					setPaymentMode()
				} else {
					walletListType.add("Select Wallet Type")
					walletListType.addAll(getWalletTypeList(false))
				}
				
				val adapter = ArrayAdapter(activity, R.layout.layout_spinner, walletListType)
				walletTypeSpinner.adapter = adapter
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
				walletTypeSpinner.onItemSelectedListener =
					object : AdapterView.OnItemSelectedListener {
						override fun onItemSelected(
							p0: AdapterView<*>?, view: View?, position: Int, id: Long
						) {
							if (selectedType != PaymentMode.CREDIT) {
								selectedWalletType = if (position != 0) {
									walletListType[position]
								} else {
									""
								}
								setUsedFrom()
								setPaymentMode()
							}
						}

						override fun onNothingSelected(p0: AdapterView<*>?) {
						}
					}
			}
		}
	}
	
	private fun setPaymentMods() {
		with(productModel) {
			with(purchaseCompleteBinding) {
				val paymentTypes: MutableList<String> = arrayListOf()
				paymentTypes.addAll(getPaymentModeList())
				val adapter = ArrayAdapter(activity, R.layout.layout_spinner, paymentTypes)
				paymentTypeSpinner.adapter = adapter
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
				paymentTypeSpinner.onItemSelectedListener =
					object : AdapterView.OnItemSelectedListener {
						override fun onItemSelected(
							p0: AdapterView<*>?, view: View?, position: Int, id: Long
						) {
							selectedPaymentMod = if (position != 0) {
								paymentTypes[position]
							} else {
								""
							}
							
							//setPaymentMode()
							setWalletTypes()
						}
						
						override fun onNothingSelected(p0: AdapterView<*>?) {
						}
					}
			}
		}
	}

	/**
	 * Set voucher data
	 *
	 * @param isProduct when data available it's true
	 */
	private fun setMemberData(isProduct: Boolean) {
		with(purchaseCompleteBinding) {
			with(productModel) {
				if (isProduct) {
					etMemberName.text = memberDetailModel?.fullname
					etMobile.text = memberDetailModel?.mobile
					setWalletAndCreditAmount()
				}
			}
		}
	}

	/**
	 * To observe the view model for data changes
	 */
	private fun observeViewModel() {
		with(productModel) {
			with(purchaseCompleteBinding) {
				isProgressShowing.observe(
					viewLifecycleOwner
				) { shouldShowProgress ->
					updateProgress(shouldShowProgress)
				}

				isMemberDataAvailable.observe(
					viewLifecycleOwner
				) { isProduct ->
					setMemberData(isProduct)
				}

				isProductSendDataAvailable.observe(
					viewLifecycleOwner
				) { isProductSend ->
					if (isProductSend) {
						if (successResponse?.status == true) {
							showSuccessDialog(
								activity.resources.getString(R.string.app_name),
								successResponse?.message,
								NSConstants.PRODUCT_SEND_CLICK
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

				failureErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
					showAlertDialog(errorMessage)
				}

				apiErrors.observe(viewLifecycleOwner) { apiErrors ->
					parseAndShowApiError(apiErrors)
				}

				noNetworkAlert.observe(viewLifecycleOwner) {
					showNoNetworkAlertDialog(
						getString(R.string.no_network_available),
						getString(R.string.network_unreachable)
					)
				}

				validationErrorId.observe(viewLifecycleOwner) { errorId ->
					showAlertDialog(getString(errorId))
				}
			}
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onPositiveButtonClickEvent(event: NSAlertButtonClickEvent) {
		if (event.buttonType == NSConstants.KEY_ALERT_BUTTON_POSITIVE && event.alertKey == NSConstants.PRODUCT_SEND_CLICK) {
			val intent = Intent()
			activity.setResult(NSRequestCodes.REQUEST_PRODUCT_STOCK_UPDATE_DETAIL, intent)
			NSConstants.STOCK_UPDATE = NSRequestCodes.REQUEST_PRODUCT_STOCK_UPDATE_DETAIL
			finish()
		}
	}

}
