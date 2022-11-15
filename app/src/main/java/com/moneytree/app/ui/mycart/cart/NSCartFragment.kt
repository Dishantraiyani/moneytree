package com.moneytree.app.ui.mycart.cart

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.NSConstants.Companion.isGridMode
import com.moneytree.app.common.callbacks.NSCartTotalAmountCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.callbacks.NSProductDetailCallback
import com.moneytree.app.common.utils.*
import com.moneytree.app.databinding.NsFragmentMyCartBinding
import com.moneytree.app.repository.network.responses.ProductDataDTO
import com.moneytree.app.ui.mycart.productDetail.NSProductsDetailActivity
import com.moneytree.app.ui.mycart.purchaseComplete.PurchaseCompleteActivity
import com.moneytree.app.ui.mycart.stockComplete.StockCompleteActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSCartFragment : NSFragment() {
    private val productModel: NSCartViewModel by lazy {
		ViewModelProvider(this)[NSCartViewModel::class.java]
    }
    private var _binding: NsFragmentMyCartBinding? = null

    private val productBinding get() = _binding!!
    private var productListAdapter: NSCartListRecycleAdapter? = null

	companion object {
		fun newInstance() = NSCartFragment()
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
            with(productModel) {
				with(layoutHeader) {
					clBack.visible()
					tvHeaderBack.text = activity.resources.getString(R.string.my_cart)
					val intent = Intent()
					activity.setResult(NSRequestCodes.REQUEST_PRODUCT_CART_UPDATE, intent)
					NSConstants.STOCK_UPDATE = NSRequestCodes.REQUEST_PRODUCT_CART_UPDATE
				}
                setVoucherAdapter()
            }
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
					ivBack.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							onBackPress()
						}
					})

					proceed.setOnClickListener {
						if (NSConstants.SOCKET_TYPE.isNullOrEmpty()) {
							switchResultActivity(
								dataResult,
								PurchaseCompleteActivity::class.java
							)
							finish()
						} else {
							if (NSConstants.SOCKET_TYPE.equals(NSConstants.SUPER_SOCKET_TYPE) || NSConstants.SOCKET_TYPE.equals(
									NSConstants.NORMAL_SOCKET_TYPE
								)
							) {
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
					NSCartListRecycleAdapter(activity, false, object : NSCartTotalAmountCallback {
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
				for (data in NSApplication.getInstance().getProductList()) {
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
}
