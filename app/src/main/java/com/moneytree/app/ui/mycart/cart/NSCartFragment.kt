package com.moneytree.app.ui.mycart.cart

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
import com.moneytree.app.ui.mycart.address.NSAddressActivity
import com.moneytree.app.ui.mycart.placeOrder.NSPlaceOrderActivity
import com.moneytree.app.ui.mycart.purchaseComplete.PurchaseCompleteActivity
import com.moneytree.app.ui.mycart.stockComplete.StockCompleteActivity
import org.greenrobot.eventbus.EventBus

class NSCartFragment : NSFragment() {
    private val productModel: NSCartViewModel by lazy {
		ViewModelProvider(this)[NSCartViewModel::class.java]
    }
    private var _binding: NsFragmentMyCartBinding? = null

    private val productBinding get() = _binding!!
    private var productListAdapter: NSCartListRecycleAdapter? = null
    private val cartAddressResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            if (resultCode == RESULT_OK) {
                switchResultActivity(
                    dataResult,
                    NSPlaceOrderActivity::class.java,
                    bundleOf(NSConstants.KEY_IS_FROM_ORDER to productModel.isFromOrder)
                )
            }
        }

	companion object {
		fun newInstance(bundle: Bundle?) = NSCartFragment().apply {
            arguments = bundle
        }
	}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productModel.isFromOrder = arguments?.getBoolean(NSConstants.KEY_IS_FROM_ORDER)?:false
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
            NSConstants.STOCK_UPDATE = NSRequestCodes.REQUEST_PRODUCT_CART_UPDATE
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
                        if (isFromOrder) {
                            if (pref.selectedAddress != null) {
                                switchResultActivity(
                                    dataResult,
                                    NSPlaceOrderActivity::class.java,
                                    bundleOf(NSConstants.KEY_IS_FROM_ORDER to isFromOrder)
                                )
                            } else {
                                switchResultActivity(
                                    cartAddressResult,
                                    NSAddressActivity::class.java,
                                    bundleOf(NSConstants.KEY_IS_FROM_ORDER to isFromOrder, NSConstants.KEY_IS_ADD_ADDRESS to true)
                                )
                            }
                        } else {
                            if (NSConstants.SOCKET_TYPE.isNullOrEmpty()) {
                                switchResultActivity(
                                    dataResult,
                                    PurchaseCompleteActivity::class.java
                                )
                                finish()
                            } else {
                                if (NSConstants.SOCKET_TYPE.equals(NSConstants.SUPER_SOCKET_TYPE)) {
                                    clBottomSheet.visible()
                                } else {
                                    switchResultActivity(
                                        dataResult,
                                        PurchaseCompleteActivity::class.java
                                    )
                                    finish()
                                }
                            }
                        }
					}

					clBottomSheet.setOnClickListener {
						clBottomSheet.gone()
					}

					btnRepurchase.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							switchResultActivity(dataResult, PurchaseCompleteActivity::class.java)
							finish()
						}
					})

					btnStock.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							switchResultActivity(dataResult, StockCompleteActivity::class.java)
							finish()
						}
					})
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
					NSCartListRecycleAdapter(activity, isFromOrder, false, object : NSCartTotalAmountCallback {
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
                val instance = NSApplication.getInstance()
				for (data in if (isFromOrder) instance.getOrderList() else instance.getProductList()) {
					val amount1 : Int = data.sdPrice?.toInt() ?: 0
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

    /**
     * Set voucher data
     *
     * @param isVoucher when data available it's true
     */
    private fun setVoucherData(isVoucher: Boolean) {
        with(productModel) {
            voucherDataManage(isVoucher)
            if (isVoucher) {
                productListAdapter!!.clearData()
                productListAdapter!!.updateData(productList)
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
			llItem.visibility = if (isVoucherVisible) View.VISIBLE else View.VISIBLE
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
}
