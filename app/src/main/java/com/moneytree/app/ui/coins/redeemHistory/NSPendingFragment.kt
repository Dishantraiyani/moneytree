package com.moneytree.app.ui.coins.redeemHistory

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.callbacks.NSDateRangeCallback
import com.moneytree.app.common.callbacks.NSHeaderMainSearchCallback
import com.moneytree.app.common.callbacks.NSHeaderSearchCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.TAG
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.NsFragmentPendingBinding
import com.moneytree.app.databinding.NsFragmentRedeemBinding
import com.moneytree.app.ui.wallets.redeemHistory.NSRedeemFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSPendingFragment : NSFragment() {
    private val redeemListModel: NSPendingViewModel by lazy {
        ViewModelProvider(this)[NSPendingViewModel::class.java]
    }
    private var _binding: NsFragmentPendingBinding? = null

    private val redeemBinding get() = _binding!!
    private var redeemListAdapter: NSPendingRecycleAdapter? = null
    companion object {

        private var mainCallback: NSHeaderMainSearchCallback? = null
        fun newInstance() = NSPendingFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentPendingBinding.inflate(inflater, container, false)
		observeViewModel()
        getCallback()
        return redeemBinding.root
    }

    private fun getCallback() {
        mainCallback?.onHeader(object: NSHeaderSearchCallback {
            override fun onHeader(text: String, tabPosition: Int, isClose: Boolean) {
                if (isClose) {
                    if(tabPosition == 1) {
                        with(redeemListModel) {
                            pageIndex = "1"
                            if (tempRedeemList.isValidList()) {
                                redeemList.clear()
                                redeemList.addAll(tempRedeemList)
                                tempRedeemList.clear()
                                setRedeemData(redeemList.isValidList())
                            }
                        }
                    }
                } else {
                    if(tabPosition == 1) {
                        with(redeemListModel) {
                            tempRedeemList.addAll(redeemList)
                            getRedeemListData(pageIndex, text, true, isBottomProgress = false)
                        }
                    }
                }
            }
        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTabSelectEvent(event: NSRedemptionEventTab) {
        Log.d(TAG, "onTabSelectEvent: $event")
        viewCreated()
        setListener()
    }

    /**
     * View created
     */
    private fun viewCreated() {
        setRedeemAdapter()
    }

    fun loadFragment(callback: NSHeaderMainSearchCallback?) {
        mainCallback = callback
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(redeemBinding) {
            with(redeemListModel) {
                srlRefresh.setOnRefreshListener {
					callRedeemFirstPage(false)
                }
            }
        }
    }

	private fun callRedeemFirstPage(isShowProgress: Boolean) {
		redeemListModel.apply {
			pageIndex = "1"
			getRedeemListData(pageIndex, "", isShowProgress, isBottomProgress = false)
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onWalletUpdateData(event: NSRedeemWalletUpdateEvent) {
		with(redeemListModel) {
			callRedeemFirstPage(true)
		}
	}

    /**
     * To add data of redeem in list
     */
    private fun setRedeemAdapter() {
        with(redeemBinding) {
            with(redeemListModel) {
                rvRedeemList.layoutManager = LinearLayoutManager(activity)
                redeemListAdapter =
                    NSPendingRecycleAdapter(activity, object : NSPageChangeCallback{
                        override fun onPageChange(pageNo: Int) {
                            if (redeemResponse!!.nextPage) {
                                val page: Int = redeemList.size/NSConstants.PAGINATION + 1
                                pageIndex = page.toString()
                                getRedeemListData(pageIndex, "", true, isBottomProgress = true)
                            }
                        }
                    })
                rvRedeemList.adapter = redeemListAdapter
				callRedeemFirstPage(true)
            }
        }
    }

    private fun bottomProgress(isShowProgress: Boolean) {
        with(redeemBinding) {
            cvProgress.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        }
    }

    /**
     * Set redeem data
     *
     * @param isRedeem when data available it's true
     */
    private fun setRedeemData(isRedeem: Boolean) {
        with(redeemListModel) {
            redeemDataManage(isRedeem)
			var amount = "0"
			if (redeemResponse?.walletAmount.isValidList()) {
				amount = redeemResponse!!.walletAmount[0].amount ?:"0"
			}
			EventBus.getDefault().post(NSWalletAmount(amount))
            if (isRedeem) {
				redeemListAdapter!!.clearData()
                redeemListAdapter!!.updateData(redeemList)
            }
        }
    }

    /**
     * Redeem data manage
     *
     * @param isRedeemVisible when redeem available it's visible
     */
    private fun redeemDataManage(isRedeemVisible: Boolean) {
        with(redeemBinding) {
            rvRedeemList.visibility = if (isRedeemVisible) View.VISIBLE else View.GONE
            clRedeemNotFound.visibility = if (isRedeemVisible) View.GONE else View.VISIBLE
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(redeemListModel) {
            with(redeemBinding) {
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

                isRedeemDataAvailable.observe(
                    viewLifecycleOwner
                ) { isRedeem ->
                    srlRefresh.isRefreshing = false
                    setRedeemData(isRedeem)
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
