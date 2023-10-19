package com.moneytree.app.ui.mycart.orders.history

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSConstants.Companion.isGridMode
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.callbacks.NSProductDetailCallback
import com.moneytree.app.common.callbacks.NSSearchCallback
import com.moneytree.app.common.callbacks.NSStockHistoryDetailCallback
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.NsFragmentRsHistoryBinding
import com.moneytree.app.repository.network.responses.ProductDataDTO
import com.moneytree.app.repository.network.responses.RepurchaseDataItem
import com.moneytree.app.ui.mycart.orders.detail.OrderDetailActivity
import com.moneytree.app.ui.mycart.stockDetail.StockDetailActivity
import com.moneytree.app.ui.productDetail.MTProductsDetailActivity

class OrderHistoryFragment : NSFragment(), NSSearchCallback {
    private val historyModel: OrderHistoryViewModel by lazy {
		ViewModelProvider(this)[OrderHistoryViewModel::class.java]
    }
    private var _binding: NsFragmentRsHistoryBinding? = null

    private val stockBinding get() = _binding!!
    private var stockListAdapter: OrderHistoryRecycleAdapter? = null


	companion object {
		fun newInstance(bundle: Bundle?) = OrderHistoryFragment().apply {
			arguments = bundle
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			with(historyModel) {
				isFromOrderTab = it.getBoolean(NSConstants.KEY_IS_FROM_ORDER)
			}
		}
	}

	override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentRsHistoryBinding.inflate(inflater, container, false)
		viewCreated()
		setListener()
        return stockBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(stockBinding) {
            with(historyModel) {
                HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle =  resources.getString(R.string.order_history), isSearch = true, searchCallback = this@OrderHistoryFragment)
                setVoucherAdapter()
            }
        }
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(historyModel) {
            with(stockBinding) {
                srlRefresh.setOnRefreshListener {
                    pageIndex = "1"
                    getProductListData(pageIndex, layoutHeader.etSearch.text.toString(), false, isBottomProgress = false)
                }

				with(layoutHeader) {
					ivClose.setOnClickListener {
						cardSearch.visibility = View.GONE
						etSearch.setText("")
						hideKeyboard(cardSearch)
						with(historyModel) {
							pageIndex = "1"
							if (tempProductList.isValidList()) {
								productList.clear()
								productList.addAll(tempProductList)
								tempProductList.clear()
								setVoucherData(productList.isValidList())
							}
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
        with(stockBinding) {
            with(historyModel) {
				rvHistoryList.layoutManager = LinearLayoutManager(activity)
                stockListAdapter =
                    OrderHistoryRecycleAdapter(activity, object : NSPageChangeCallback{
                        override fun onPageChange(pageNo: Int) {
                            if (productResponse!!.nextPage) {
                                val page: Int = productList.size/NSConstants.PAGINATION + 1
                                pageIndex = page.toString()
                                getProductListData(pageIndex,  layoutHeader.etSearch.text.toString(), true, isBottomProgress = true)
                            }
                        }
                    }) {
                        switchActivity(
                            OrderDetailActivity::class.java,
                            bundleOf(
                                NSConstants.ORDER_DETAIL_ID to it.directOrderId,
                                NSConstants.ORDER_DETAIL_ID_DETAIL to Gson().toJson(it)
                            )
                        )
                    }
                rvHistoryList.adapter = stockListAdapter
                pageIndex = "1"
                getProductListData(pageIndex, layoutHeader.etSearch.text.toString(), true, isBottomProgress = false)
            }
        }
    }

    private fun bottomProgress(isShowProgress: Boolean) {
        with(stockBinding) {
            cvProgress.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        }
    }

    /**
     * Set voucher data
     *
     * @param isVoucher when data available it's true
     */
    private fun setVoucherData(isVoucher: Boolean) {
        with(historyModel) {
            voucherDataManage(isVoucher)
            if (isVoucher) {
                stockListAdapter!!.clearData()
                stockListAdapter!!.updateData(productList)
            }
        }
    }

    /**
     * Voucher data manage
     *
     * @param isVoucherVisible when voucher available it's visible
     */
    private fun voucherDataManage(isVoucherVisible: Boolean) {
        with(stockBinding) {
            rvHistoryList.visibility = if (isVoucherVisible) View.VISIBLE else View.GONE
            clProductNotFound.visibility = if (isVoucherVisible) View.GONE else View.VISIBLE
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(historyModel) {
            with(stockBinding) {
                isProgressShowing.observe(
                    viewLifecycleOwner
                ) { shouldShowProgress ->
                    updateProgress(shouldShowProgress)
                }

                isBottomProgressShowing.observe(
                    viewLifecycleOwner
                ) { isBottomProgressShowing ->
                    bottomProgress(isBottomProgressShowing)
                }

                isProductsDataAvailable.observe(
                    viewLifecycleOwner
                ) { isProduct ->
                    srlRefresh.isRefreshing = false
                    setVoucherData(isProduct)
                }

                failureErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                    srlRefresh.isRefreshing = false
                    showAlertDialog(errorMessage)
                }

                apiErrors.observe(viewLifecycleOwner) { apiErrors ->
                    srlRefresh.isRefreshing = false
                    parseAndShowApiError(apiErrors)
                }

                noNetworkAlert.observe(viewLifecycleOwner) {
                    srlRefresh.isRefreshing = false
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

    override fun onSearch(search: String) {
        with(historyModel) {
            tempProductList.addAll(productList)
            getProductListData(
                pageIndex,
                search,
                true,
                isBottomProgress = false
            )
        }
    }
}
