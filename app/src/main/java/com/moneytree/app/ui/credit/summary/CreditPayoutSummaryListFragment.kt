package com.moneytree.app.ui.credit.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moneytree.app.R
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.callbacks.NSInfoSelectCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.callbacks.NSSearchCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.FragmentCreditPayoutSummaryListBinding
import com.moneytree.app.enums.getSummaryTypeList
import java.util.Locale


class CreditPayoutSummaryListFragment : NSFragment(), NSSearchCallback {
    private val viewModel: CreditPayoutSummaryListViewModel by lazy {
		ViewModelProvider(this)[CreditPayoutSummaryListViewModel::class.java]
    }
    
    private var _binding: FragmentCreditPayoutSummaryListBinding? = null
    private val binding get() = _binding!!
    private var listAdapter: CreditPayoutSummaryListRecycleAdapter? = null
    private var upCommingAdapter: UpComingReleaseDateRecycleAdapter? = null
    private var isExpanded: Boolean = false
    private var isReleasedExpanded: Boolean = false
    
    companion object {
        fun newInstance() = CreditPayoutSummaryListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreditPayoutSummaryListBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return binding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        HeaderUtils(binding.layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString( R.string.ov_credit_payout_summary), isSearch = true , searchCallback = this)
        setStatusSpinner()
        setCreditPayoutAdapter()
        observeViewModel()
    }

    private fun setStatusSpinner() {
        with(binding) {
            with(viewModel) {
                paymentStatusSpinner.visible()
                val statusList = getSummaryTypeList()
                val adapter = ArrayAdapter(activity, R.layout.layout_spinner, statusList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                paymentStatusSpinner.adapter = adapter
	            paymentStatusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        pageIndex = "1"
                        selectedSummaryType = statusList[position]
                        getPayoutSummaryListData(pageIndex, true, isBottomProgress = false)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
                
                clFilterPaymentStatus.setOnClickListener {
                    binding.paymentStatusSpinner.performClick()
                }
            }
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(binding) {
            with(viewModel) {
                llPaymentSummaryHeader.setOnClickListener {
                    isExpanded = !isExpanded
                    clPayoutSummaryDetail.visibility = if (isExpanded) View.VISIBLE else View.GONE
                    ivPaymentSummaryArrow.animate().rotation(if (isExpanded) 180f else 0f).setDuration(200).start()
                }
                
                llReleasedDateHeader.setOnClickListener {
                    isReleasedExpanded = !isReleasedExpanded
                    rlUpcomingReleaseDate.visibility = if (isReleasedExpanded) View.VISIBLE else View.GONE
                    ivReleasedArrow.animate().rotation(if (isReleasedExpanded) 180f else 0f).setDuration(200).start()
                }
                
                srlRefresh.setOnRefreshListener {
                    pageIndex = "1"
                    getPayoutSummaryListData(pageIndex, false, isBottomProgress = false)
                }
	            
	            layoutHeader.ivClose.setOnClickListener {
                    if (layoutHeader.etSearch.text.isNotEmpty()) {
                        layoutHeader.etSearch.setText("")
                    } else {
                        hideKeyboard(layoutHeader.etSearch)
                        layoutHeader.cardSearch.gone()
                        pageIndex = "1"
                        if (tempPayoutList.isValidList()) {
                            payoutList.clear()
                            payoutList.addAll(tempPayoutList)
                            tempPayoutList.clear()
                            setPayoutData(payoutList.isValidList())
                        }
                    }
	            }
            }
        }
    }

    /**
     * To add data of repurchase in list
     */
    private fun setCreditPayoutAdapter() {
        with(binding) {
            with(viewModel) {
                rvList.layoutManager = LinearLayoutManager(activity)
                listAdapter =
                    CreditPayoutSummaryListRecycleAdapter(activity, object : NSPageChangeCallback {
		                override fun onPageChange(pageNo: Int) {
			                if (payoutListResponse?.nextPage == true) {
				                val page: Int = payoutList.size / NSConstants.PAGINATION + 1
				                pageIndex = page.toString()
				                getPayoutSummaryListData(pageIndex, true, isBottomProgress = true)
			                }
		                }
	                }, object : NSInfoSelectCallback {
		                override fun onClick(position: Int) {
		                }
		                
	                })
                rvList.adapter = listAdapter
            }
        }
    }

    private fun bottomProgress(isShowProgress: Boolean) {
        with(binding) {
            cvProgress.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        }
    }
    
    private fun setPayoutData(isAvailable: Boolean) {
        with(viewModel) {
            payoutDataManage(isAvailable)
            if (isAvailable) {
                listAdapter?.clearData()
                listAdapter?.updateData(payoutList)
            }

            payoutSummary?.let { summary ->
                binding.cardCreditSummary.visibility = View.VISIBLE
                binding.tvTotalPayout.text = summary.netAmount?.let { addText(requireActivity(), R.string.price_value, it) } ?: "₹0.00"
                
                val totalCnt = (summary.totalCnt?:"0").toIntOrNull()?:0
                val adminCharge = "Admin Charge ₹${summary.adminAmount?:"0"}"
                val payoutValue = "$totalCnt payout(s)"
                val payoutDetail = "$payoutValue • $adminCharge"
                binding.tvPayoutDetail.text = payoutDetail
                
                
                binding.tvReleased.text = summary.releasedAmount?.let { addText(requireActivity(), R.string.price_value, it) } ?: "₹0.00"
                val totalReleasedCnt = (summary.releasedCnt?:"0").toIntOrNull()?:0
                val releasedValue = "$totalReleasedCnt payout(s)"
                binding.tvReleasedDetail.text = releasedValue
                
                binding.tvPendingAmount.text = summary.pendingAmount?.let { addText(requireActivity(), R.string.price_value, it) } ?: "₹0.00"
                val totalPendingCnt = (summary.pendingCnt?:"0").toIntOrNull()?:0
                val pendingValue = "$totalPendingCnt payout(s)"
                val pendingDetail = "$pendingValue • Next ${summary.nextReleaseDate}"
                binding.tvPendingDetail.text = pendingDetail
                
                binding.tvAcceptedAmount.text = summary.coinAmount?.let { addText(requireActivity(), R.string.price_value, String.format(Locale.ENGLISH, "%.2f", it.toDoubleOrNull()?:0.0)) } ?: "₹0.00"
                val releasedCoin = (summary.releasedCoin?:"0.00").toDoubleOrNull()?:0.00
                val pendingCoin = (summary.pendingCoin?:"0.00").toDoubleOrNull()?:0.00
                
                val ovCoinDetail = "Released $releasedCoin • Pending $pendingCoin"
                binding.tvOvCoinDetail.text = ovCoinDetail
                
            } ?: run {
                binding.cardCreditSummary.visibility = View.GONE
            }
            
            setUpCommingReleaseDate()
        }
    }
    
    private fun setUpCommingReleaseDate() {
        binding.apply {
            if (upCommingAdapter == null) {
                rlUpcomingReleaseDate.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                upCommingAdapter = UpComingReleaseDateRecycleAdapter(activity)
                rlUpcomingReleaseDate.adapter = upCommingAdapter
            }
            
            val list = viewModel.payoutListResponse?.releaseDates?: arrayListOf()
            upCommingAdapter?.clearData()
            upCommingAdapter?.updateData(list)
            cardUpcomingRelease.setVisibility(list.isValidList())
        }
    }
    
    private fun payoutDataManage(repurchaseData: Boolean) {
        with(binding) {
            rvList.visibility = if (repurchaseData) View.VISIBLE else View.GONE
            clNotFound.visibility = if (repurchaseData) View.GONE else View.VISIBLE
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(viewModel) {
            with(binding) {
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

                payoutDataAvailable.observe(
                    viewLifecycleOwner
                ) { isNotification ->
                    srlRefresh.isRefreshing = false
                    setPayoutData(isNotification)
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
		with(viewModel) {
			searchedText = search
			tempPayoutList.addAll(payoutList)
			getPayoutSummaryListData(pageIndex,  true, isBottomProgress = false)
		}
	}
}
