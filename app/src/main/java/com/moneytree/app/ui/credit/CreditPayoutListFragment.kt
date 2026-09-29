package com.moneytree.app.ui.credit

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
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.FragmentCreditPayoutListBinding
import com.moneytree.app.enums.getPaymentStatusList


class CreditPayoutListFragment : NSFragment(), NSSearchCallback {
    private val viewModel: CreditPayoutListViewModel by lazy {
		ViewModelProvider(this)[CreditPayoutListViewModel::class.java]
    }
    
    private var _binding: FragmentCreditPayoutListBinding? = null
    private val binding get() = _binding!!
    private var listAdapter: CreditPayoutListRecycleAdapter? = null
    
    companion object {
        fun newInstance() = CreditPayoutListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreditPayoutListBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return binding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        HeaderUtils(binding.layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString( R.string.ov_credit_payout), isSearch = true , searchCallback = this)
        setStatusSpinner()
        setCreditPayoutAdapter()
        observeViewModel()
    }

    private fun setStatusSpinner() {
        with(binding) {
            with(viewModel) {
                paymentStatusSpinner.visible()
                val statusList = getPaymentStatusList()
                val adapter = ArrayAdapter(activity, R.layout.layout_spinner, statusList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                paymentStatusSpinner.adapter = adapter
	            paymentStatusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        pageIndex = "1"
                        selectedPaymentStatus = if (statusList[position] == "All") "" else statusList[position]
                        getPayoutListData(pageIndex, true, isBottomProgress = false)
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
                srlRefresh.setOnRefreshListener {
                    pageIndex = "1"
                    getPayoutListData(pageIndex, false, isBottomProgress = false)
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
                listAdapter = CreditPayoutListRecycleAdapter(activity, object : NSPageChangeCallback {
		                override fun onPageChange(pageNo: Int) {
			                if (payoutListResponse?.nextPage == true) {
				                val page: Int = payoutList.size / NSConstants.PAGINATION + 1
				                pageIndex = page.toString()
				                getPayoutListData(pageIndex, true, isBottomProgress = true)
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
                binding.tvTotalRequest.text = summary.totalCnt ?: "0"
                binding.tvTotalAmount.text = summary.totalAmount?.let { addText(requireActivity(), R.string.price_value, it) } ?: "₹0.00"
                binding.tvPendingAmount.text = summary.pendingAmount?.let { addText(requireActivity(), R.string.price_value, it) } ?: "₹0.00"
                binding.tvAcceptedAmount.text = summary.acceptedAmount?.let { addText(requireActivity(), R.string.price_value, it) } ?: "₹0.00"
            } ?: run {
                binding.cardCreditSummary.visibility = View.GONE
            }
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
			getPayoutListData(pageIndex,  true, isBottomProgress = false)
		}
	}
}
