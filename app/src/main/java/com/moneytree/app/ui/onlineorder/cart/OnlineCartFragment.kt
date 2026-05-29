package com.moneytree.app.ui.onlineorder.cart

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.callbacks.NSCartTotalAmountCallback
import com.moneytree.app.common.utils.*
import com.moneytree.app.databinding.NsFragmentMyCartBinding
import com.moneytree.app.ui.mycart.address.selectAddress.NSSelectAddressActivity
import com.moneytree.app.ui.mycart.cart.NSCartListRecycleAdapter
import com.moneytree.app.ui.mycart.cart.NSCartViewModel
import com.moneytree.app.ui.mycart.placeOrder.NSPlaceOrderActivity
import com.moneytree.app.ui.mycart.purchaseComplete.PurchaseCompleteActivity
import com.moneytree.app.ui.mycart.stockComplete.StockCompleteActivity
import com.moneytree.app.ui.onlineorder.OnlineOrderHelper
import com.moneytree.app.ui.onlineorder.placeorder.PlaceOrderAddressActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class OnlineCartFragment : NSFragment() {
	private val productModel: OnlineCartViewModel by lazy {
		ViewModelProvider(this)[OnlineCartViewModel::class.java]
	}
	private var _binding: NsFragmentMyCartBinding? = null
	
	private val productBinding get() = _binding!!
	private var productListAdapter: OnlineCartListRecycleAdapter? = null
	
	companion object {
		fun newInstance(bundle: Bundle?) = OnlineCartFragment().apply {
			arguments = bundle
		}
	}
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = NsFragmentMyCartBinding.inflate(inflater, container, false)
		viewCreated()
		setListener()
		return productBinding.root
	}
	
	/**
	 * View created
	 */
	private fun viewCreated() {
		with(productBinding) {
			HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString( R.string.my_cart))
			val intent = Intent()
			activity.setResult(NSRequestCodes.REQUEST_PRODUCT_CART_UPDATE, intent)
			setVoucherAdapter()
		}
		observeViewModel()
	}
	
	/**
	 * Set listener
	 */
	private fun setListener() {
		with(productModel) {
			with(productBinding) {
				with(layoutHeader) {
					
					proceed.setOnClickListener {
						if (productList.isValidList()) {
							switchResultActivity(dataResult, PlaceOrderAddressActivity::class.java)
						}
					}
				}
			}
		}
	}
	
	/**
	 * To add data of vouchers in list
	 */
	private fun setVoucherAdapter() {
		with(productBinding) {
			with(productModel) {
				rvCartItem.layoutManager = LinearLayoutManager(activity)
				productListAdapter =
					OnlineCartListRecycleAdapter(activity,  false, object : NSCartTotalAmountCallback {
						override fun onResponse() {
							setTotalAmount()
						}
					})
				rvCartItem.adapter = productListAdapter
				
				getProductListData()
			}
		}
	}
	
	private fun setTotalAmount() {
		with(productBinding) {
			with(productModel) {
				var totalAmountValue = 0
				for (data in OnlineOrderHelper.getOrderList()) {
					val amount1 : Int = data.rate?.toInt() ?: 0
					val finalAmount1 = data.itemQty * amount1
					totalAmountValue += finalAmount1
				}
				
				totalAmount.text = addText(activity, R.string.price_value, totalAmountValue.toString())
				if (totalAmountValue <= 0) {
					voucherDataManage(false)
				}
			}
		}
	}
	
	private fun setVoucherData(isVoucher: Boolean) {
		with(productModel) {
			voucherDataManage(isVoucher)
			if (isVoucher) {
				productListAdapter?.clearData()
				productListAdapter?.updateData(productList)
				setTotalAmount()
			}
		}
	}
	
	/**
	 * Voucher data manage
	 *
	 * @param isVoucherVisible when voucher available it's visible
	 */
	private fun voucherDataManage(isVoucherVisible: Boolean) {
		with(productBinding) {
			rvCartItem.visibility = if (isVoucherVisible) View.VISIBLE else View.GONE
			llItem.visibility = if (isVoucherVisible) View.VISIBLE else View.GONE
			emptyCart.visibility = if (isVoucherVisible) View.GONE else View.VISIBLE
		}
	}
	
	/**
	 * To observe the view model for data changes
	 */
	private fun observeViewModel() {
		with(productModel) {
			with(productBinding) {
				isProgressShowing.observe(
					viewLifecycleOwner
				) { shouldShowProgress ->
					updateProgress(shouldShowProgress)
				}
				
				isProductsDataAvailable.observe(
					viewLifecycleOwner
				) { isProduct ->
					setVoucherData(isProduct)
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
	
	@Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
	fun onResultEvent(event: NSActivityEvent) {
		if (event.resultCode == NSRequestCodes.REQUEST_PRODUCT_CART_UPDATE || event.resultCode == NSRequestCodes.REQUEST_PRODUCT_CART_UPDATE_DETAIL || event.resultCode == NSRequestCodes.REQUEST_PRODUCT_STOCK_UPDATE_DETAIL) {
			with(productModel) {
				if (productListAdapter != null) {
					getProductListData()
					setTotalAmount()
				}
			}
		}
	}
}
