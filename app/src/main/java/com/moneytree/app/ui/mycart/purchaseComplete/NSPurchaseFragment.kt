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
import com.moneytree.app.common.*
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.NsFragmentPurchaseCompleteBinding
import com.moneytree.app.repository.network.responses.NSSuccessResponse
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
			with(productModel) {
				with(layoutHeader) {
					clBack.visible()
					tvHeaderBack.text = activity.resources.getString(R.string.repurchase)
					/*val intent = Intent()
					activity.setResult(NSRequestCodes.REQUEST_PRODUCT_CART_UPDATE, intent)*/
				}
			}
		}
		observeViewModel()
		setWalletTypes()
		setTotalAmount()
	}

	/**
	 * Set listener
	 */
	private fun setListener() {
		with(productModel) {
			with(purchaseCompleteBinding) {
				with(layoutHeader) {
					ivBack.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							onBackPress()
						}
					})

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
							} else if (etMemberName.text.toString().isEmpty()) {
								Toast.makeText(activity, "Please Enter Valid Member Id", Toast.LENGTH_SHORT).show()
								return
							}
							val memberId = etMemberId.text.toString()
							val remark = etRemark.text.toString()
							val productList = NSApplication.getInstance().getProductList()
							if (productList.size > 0) {
								saveMyCart(memberId, selectedWalletType, remark, Gson().toJson(productList), true)
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
				val amount1: Int = data.sdPrice?.toInt() ?: 0
				val finalAmount1 = data.itemQty * amount1
				totalAmountValue += finalAmount1
			}
			tvProductTitle.text =
				"${NSApplication.getInstance().getProductList().size} Item Selected"
			tvAmount.text = addText(activity, R.string.price_value, totalAmountValue.toString())
		}
	}

	private fun setWalletTypes() {
		with(productModel) {
			with(purchaseCompleteBinding) {
				val walletListType: MutableList<String> = arrayListOf()
				walletListType.addAll(resources.getStringArray(R.array.wallet_types))
				val adapter = ArrayAdapter(activity, R.layout.layout_spinner, walletListType)
				walletTypeSpinner.adapter = adapter
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
				walletTypeSpinner.onItemSelectedListener =
					object : AdapterView.OnItemSelectedListener {
						override fun onItemSelected(
							p0: AdapterView<*>?, view: View?, position: Int, id: Long
						) {
							selectedWalletType = walletListType[position]
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
