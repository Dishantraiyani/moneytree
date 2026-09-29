package com.moneytree.app.ui.credit.wallet

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
import com.moneytree.app.common.callbacks.NSDateRangeCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.callbacks.NSSearchCallback
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.databinding.FragmentCreditWalletListBinding
import com.moneytree.app.enums.getTransactionTypeList
import com.moneytree.app.ui.credit.CreditPayoutActivity


class CreditWalletListFragment : NSFragment(), NSSearchCallback {
    private val viewModel: CreditWalletListViewModel by lazy {
		ViewModelProvider(this)[CreditWalletListViewModel::class.java]
    }
    
    private var _binding: FragmentCreditWalletListBinding? = null
    private val binding get() = _binding!!
    private var listAdapter: CreditWalletListRecycleAdapter? = null
    
    companion object {
        fun newInstance() = CreditWalletListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreditWalletListBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return binding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        HeaderUtils(binding.layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString( R.string.ov_credit), isSearch = true , searchCallback = this)
        setStatusSpinner()
        setCreditPayoutAdapter()
        observeViewModel()
    }

    private fun setStatusSpinner() {
        with(binding) {
            with(viewModel) {
                NSUtilities.setDateRange(binding.layoutDateRange, getTransactionTypeList(), activity, true, object : NSDateRangeCallback {
                    override fun onDateRangeSelect(startDate: String, endDate: String, type: String) {
                        startingDate = startDate
                        endingDate = endDate
                        selectedType = type
                        callTransactionFirstPage(true)
                    }
                    
                })
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
                    getCreditWalletListData(pageIndex, false, isBottomProgress = false)
                }
	            
	            layoutHeader.ivClose.setOnClickListener {
                    if (layoutHeader.etSearch.text.isNotEmpty()) {
                        layoutHeader.etSearch.setText("")
                    } else {
                        hideKeyboard(layoutHeader.etSearch)
                        layoutHeader.cardSearch.gone()
                        pageIndex = "1"
                        if (tempWalletList.isValidList()) {
                            walletList.clear()
                            walletList.addAll(tempWalletList)
                            tempWalletList.clear()
                            setPayoutData(walletList.isValidList())
                        }
                    }
	            }
                
                btnOpenOvCredit.setOnClickListener {
                    switchActivity(CreditPayoutActivity::class.java)
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
                listAdapter = CreditWalletListRecycleAdapter(activity, object : NSPageChangeCallback {
		                override fun onPageChange(pageNo: Int) {
			                if (payoutListResponse?.nextPage == true) {
				                val page: Int = walletList.size / NSConstants.PAGINATION + 1
				                pageIndex = page.toString()
				                getCreditWalletListData(pageIndex, true, isBottomProgress = true)
			                }
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
                listAdapter?.updateData(walletList)
            }
            walletBalance?.let {
                binding.tvWalletBalance.text = addText(requireActivity(), R.string.price_value, it)
            } ?: run {
                binding.tvWalletBalance.text = addText(requireActivity(), R.string.price_value, "0")
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

                walletDataAvailable.observe(
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
			tempWalletList.addAll(walletList)
			getCreditWalletListData(pageIndex,  true, isBottomProgress = false)
		}
	}
    
    private fun callTransactionFirstPage(isShowProgress: Boolean) {
        viewModel.apply {
            pageIndex = "1"
            getCreditWalletListData(pageIndex,  isShowProgress, isBottomProgress = false)
        }
    }
}
