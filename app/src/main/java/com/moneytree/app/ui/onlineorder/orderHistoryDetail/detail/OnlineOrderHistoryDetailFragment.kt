package com.moneytree.app.ui.onlineorder.orderHistoryDetail.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moneytree.app.R
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.expandCollapse
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.FragmentOnlineOrderDetailInfoBinding

class OnlineOrderHistoryDetailFragment : NSFragment() {
    private val historyModel: OnlineOrderInfoViewModel by lazy {
		ViewModelProvider(this)[OnlineOrderInfoViewModel::class.java]
    }
    private var _binding: FragmentOnlineOrderDetailInfoBinding? = null

    private val stockBinding get() = _binding!!
    private var stockListAdapter: OnlineOrderHistoryDetailAdapter? = null
    private var isExpand = true

	companion object {
		fun newInstance(bundle: Bundle?) = OnlineOrderHistoryDetailFragment().apply {
			arguments = bundle
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			with(historyModel) {
                orderDirectId = it.getString(NSConstants.ORDER_DETAIL_ID)
                orderDirectDetail = it.getString(NSConstants.ORDER_DETAIL_ID_DETAIL)
			}
		}
	}

	override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnlineOrderDetailInfoBinding.inflate(inflater, container, false)
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
                HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle =  resources.getString(R.string.online_order_info))
                setVoucherAdapter()
            }
            
            //
            //                        tvAddress.text = addressStr
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
                    getProductListData(false)
                }
                
                ivExpandCollapse.setOnClickListener {
                    
                    isExpand = !isExpand
                    
                    clExpandCollapse.expandCollapse(isExpand)
                    
                    ivExpandCollapse.animate()
                        .rotation(if (!isExpand) 180f else 0f)
                        .setDuration(300)
                        .start()
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
                    OnlineOrderHistoryDetailAdapter(activity)
                rvHistoryList.adapter = stockListAdapter
                getProductListData(true)
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
            historyModel.orderHistoryDataItem?.apply {
                val addressStr = address1 + ", " + city + ", " + district + ", " + state + if(country.isNullOrEmpty()) "" else ", $country"
                
                stockBinding.apply {
                    tvAddress.text = addressStr
                    tvOrderId.text = directOrderId
                    tvOrderNo.text = orderNo
                    tvMemberId.text = memberid
                    tvDate.text = createdAt
                    tvOrderStatus.text = orderStatus
                    tvPaymentType.text = paymentType
                    tvTotal.text = total
                    tvFullName.text = fullName
                    tvMobile.text = mobileNo
                    tvEmail.text = email
                    llPaymentType.setVisibility(!paymentType.isNullOrEmpty())
                    llDate.setVisibility(!createdAt.isNullOrEmpty())
                    llOrderStatus.setVisibility(!orderStatus.isNullOrEmpty())
                    llFullName.setVisibility(!fullName.isNullOrEmpty())
                    llMobile.setVisibility(!mobileNo.isNullOrEmpty())
                    llEmail.setVisibility(!email.isNullOrEmpty())
                    llPaymentCharges.setVisibility(!paymentChargePercentage.isNullOrEmpty())
                    
                    if (!paymentChargePercentage.isNullOrEmpty()) {
                        val charge = "$paymentChargePercentage%"
                        tvPaymentCharge.text = charge
                    }
                    
                    if (mtCoinStatus?.isNotEmpty() == true) {
                        llMtCoin.visible()
                        tvMtCoin.text = mtCoinStatus
                    }
                    if (mtCoinTotal?.isNotEmpty() == true) {
                        llMtCoinTotal.visible()
                        tvMtCoinTotal.text = mtCoinTotal.let { addText(activity, R.string.price_value, it) }
                    }
                }
            }
            
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
}
