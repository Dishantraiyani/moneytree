package com.moneytree.app.ui.mycart.purchaseComplete

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.lifecycle.ViewModelProvider
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.NsFragmentPurchaseCompleteBinding


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
			tvProductTitle.text = "${NSApplication.getInstance().getProductList().size} Item Selected"
			tvAmount.text = addText(activity, R.string.price_value, totalAmountValue.toString())
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
					setTotalAmount()
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
}
