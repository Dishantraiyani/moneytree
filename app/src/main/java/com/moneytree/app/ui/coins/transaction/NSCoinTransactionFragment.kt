package com.moneytree.app.ui.coins.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.NSRedeemWalletUpdateTransferEvent
import com.moneytree.app.common.NSWalletAmount
import com.moneytree.app.common.callbacks.NSDateRangeCallback
import com.moneytree.app.common.callbacks.NSHeaderMainSearchCallback
import com.moneytree.app.common.callbacks.NSHeaderSearchCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.NsFragmentCoinTransactionBinding
import com.moneytree.app.databinding.NsFragmentTransactionBinding
import com.moneytree.app.ui.wallets.transaction.NSTransactionFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSCoinTransactionFragment : NSFragment() {
    private val transactionListModel: NSCoinTransactionViewModel by lazy {
        ViewModelProvider(this)[NSCoinTransactionViewModel::class.java]
    }
    private var _binding: NsFragmentCoinTransactionBinding? = null

    private val transactionBinding get() = _binding!!
    private var transactionListAdapter: NSCoinTransactionRecycleAdapter? = null
    companion object {

        private var mainCallback: NSHeaderMainSearchCallback? = null
        fun newInstance() = NSCoinTransactionFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentCoinTransactionBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return transactionBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        observeViewModel()
        with(transactionBinding) {
            with(transactionListModel) {
                mainCallback?.onHeader(object: NSHeaderSearchCallback {
                    override fun onHeader(text: String, tabPosition: Int, isClose: Boolean) {
                        if (isClose) {
                            if(tabPosition == 0) {
                                with(transactionListModel) {
                                    pageIndex = "1"
                                    if (tempTransactionList.isValidList()) {
                                        transactionList.clear()
                                        transactionList.addAll(tempTransactionList)
                                        tempTransactionList.clear()
                                        setTransactionData(transactionList.isValidList())
                                    }
                                }
                            }
                        } else {
                            if(tabPosition == 0) {
                                with(transactionListModel) {
                                    tempTransactionList.addAll(transactionList)
                                    getTransactionListData(pageIndex, text, true, isBottomProgress = false)
                                }
                            }
                        }
                    }
                })
                setTransactionAdapter()
            }
        }
    }

    fun loadFragment(callback: NSHeaderMainSearchCallback?) {
        mainCallback = callback
        updateBalance()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(transactionBinding) {
            with(transactionListModel) {
                srlRefresh.setOnRefreshListener {
					callTransactionFirstPage(false)
                }
            }
        }
    }

	private fun callTransactionFirstPage(isShowProgress: Boolean) {
		transactionListModel.apply {
			pageIndex = "1"
			getTransactionListData(pageIndex, "", isShowProgress, isBottomProgress = false)
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onWalletUpdateData(event: NSRedeemWalletUpdateTransferEvent) {
		with(transactionListModel) {
			callTransactionFirstPage(true)
		}
	}

    /**
     * To add data of vouchers in list
     */
    private fun setTransactionAdapter() {
        with(transactionBinding) {
            with(transactionListModel) {
                rvTransactions.layoutManager = LinearLayoutManager(activity)
                transactionListAdapter =
                    NSCoinTransactionRecycleAdapter(activity, object : NSPageChangeCallback{
                        override fun onPageChange(pageNo: Int) {
                            if (transactionResponse!!.nextPage) {
                                val page: Int = transactionList.size/NSConstants.PAGINATION + 1
                                pageIndex = page.toString()
                                getTransactionListData(pageIndex, "", false, isBottomProgress = true)
                            }
                        }
                    })
                rvTransactions.adapter = transactionListAdapter
                pageIndex = "1"
				NSUtilities.setDateRange(transactionBinding.layoutDateRange, activity.resources.getStringArray(R.array.transaction_filter), activity, true, object : NSDateRangeCallback {
					override fun onDateRangeSelect(startDate: String, endDate: String, type: String) {
						transactionListModel.apply {
							startingDate = startDate
							endingDate = endDate
							selectedType = type
						}
						callTransactionFirstPage(true)
					}

				})
                /*Handler(Looper.getMainLooper()).postDelayed({
					callTransactionFirstPage(true)
                }, 200)*/
            }
        }
    }

    private fun bottomProgress(isShowProgress: Boolean) {
        with(transactionBinding) {
            cvProgress.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        }
    }

    /**
     * Set transaction data
     *
     * @param isTransaction when data available it's true
     */
    private fun setTransactionData(isTransaction: Boolean) {
        with(transactionListModel) {
            transactionDataManage(isTransaction)
            updateBalance()
            if (isTransaction) {
                transactionListAdapter!!.clearData()
                transactionListAdapter!!.updateData(transactionList)
            }
        }
    }

    private fun updateBalance() {
        transactionBinding.apply {
            transactionListModel.apply {
                var amount = "0"
                if (transactionResponse?.walletAmount.isValidList()) {
                    amount = transactionResponse!!.walletAmount[0].amount ?: "0"
                }
                EventBus.getDefault().post(NSWalletAmount(amount))
            }
        }
    }

    /**
     * Transaction data manage
     *
     * @param isTransactionVisible when transaction available it's visible
     */
    private fun transactionDataManage(isTransactionVisible: Boolean) {
        with(transactionBinding) {
            rvTransactions.visibility = if (isTransactionVisible) View.VISIBLE else View.GONE
            clTransactionNotFound.visibility = if (isTransactionVisible) View.GONE else View.VISIBLE
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(transactionListModel) {
            with(transactionBinding) {
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

                isTransactionDataAvailable.observe(
                    viewLifecycleOwner
                ) { isTransaction ->
                    srlRefresh.isRefreshing = false
                    setTransactionData(isTransaction)
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