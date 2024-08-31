package com.moneytree.app.ui.mycart.history

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
import com.moneytree.app.ui.mycart.stockDetail.StockDetailActivity
import com.moneytree.app.ui.productDetail.MTProductsDetailActivity

class RSHistoryFragment : NSFragment(), NSSearchCallback {
    private val historyModel: RSHistoryViewModel by lazy {
		ViewModelProvider(this)[RSHistoryViewModel::class.java]
    }
    private var _binding: NsFragmentRsHistoryBinding? = null

    private val stockBinding get() = _binding!!
    private var stockListAdapter: RSHistoryRecycleAdapter? = null


	companion object {
		fun newInstance(bundle: Bundle?) = RSHistoryFragment().apply {
			arguments = bundle
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			with(historyModel) {
				stockType = it.getString(NSConstants.STOCK_HISTORY_LIST)
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
                HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(if (stockType.equals(NSConstants.SOCKET_HISTORY)) R.string.stock_transfer else R.string.repurchase_history), isSearch = true, searchCallback = this@RSHistoryFragment)
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
					RSHistoryRecycleAdapter(activity, stockType.equals(NSConstants.SOCKET_HISTORY), object : NSPageChangeCallback{
                        override fun onPageChange(pageNo: Int) {
                            if (productResponse!!.nextPage) {
                                val page: Int = productList.size/NSConstants.PAGINATION + 1
                                pageIndex = page.toString()
                                getProductListData(pageIndex,  layoutHeader.etSearch.text.toString(), true, isBottomProgress = true)
                            }
                        }
                    }, object : NSStockHistoryDetailCallback {
						override fun onResponse(item: RepurchaseDataItem) {
							switchActivity(StockDetailActivity::class.java, bundleOf(NSConstants.KEY_STOCK_TYPE to stockType.equals(NSConstants.SOCKET_HISTORY), NSConstants.STOCK_DETAIL_ID to if (stockType.equals(NSConstants.SOCKET_HISTORY)) item.stockTransferId else item.repurchaseId))
						}
					})
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
