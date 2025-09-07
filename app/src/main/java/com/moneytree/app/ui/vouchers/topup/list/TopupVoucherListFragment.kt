package com.moneytree.app.ui.vouchers.topup.list

import android.app.Activity.RESULT_OK
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
import com.moneytree.app.common.callbacks.NSJoiningVoucherCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.common.utils.switchResultActivity
import com.moneytree.app.databinding.FragmentTopUpVoucherListBinding
import com.moneytree.app.ui.vouchers.topup.TopUpVoucherActivity

class TopupVoucherListFragment : NSFragment(), NSJoiningVoucherCallback {
    private val voucherListModel: TopUpVoucherListViewModel by lazy {
	    ViewModelProvider(this)[TopUpVoucherListViewModel::class.java]
    }
    private var _binding: FragmentTopUpVoucherListBinding? = null

    private val voucherBinding get() = _binding!!
    private var voucherListAdapter: TopUpVoucherListRecycleAdapter? = null
    
    private val topUpDataResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            if (resultCode == RESULT_OK) {
                voucherListModel.getVoucherListData(true, this@TopupVoucherListFragment)
            }
        }
    
    companion object {
        fun newInstance() = TopupVoucherListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopUpVoucherListBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return voucherBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        HeaderUtils(voucherBinding.layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.top_vouchers), isAddNew = true)
        
        setVoucherAdapter()
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(voucherListModel) {
            with(voucherBinding) {
                srlRefresh.setOnRefreshListener {
                    getVoucherListData(false, this@TopupVoucherListFragment)
                }
                
                layoutHeader.ivAddNew.setSafeOnClickListener {
                    switchResultActivity(topUpDataResult, TopUpVoucherActivity::class.java, bundleOf(NSConstants.KEY_IS_VOUCHER_FROM_TRANSFER to true))
                }
            }
        }
    }

    /**
     * To add data of vouchers in list
     */
    private fun setVoucherAdapter() {
        with(voucherBinding) {
            with(voucherListModel) {
                rvVoucherList.layoutManager = LinearLayoutManager(activity)
                voucherListAdapter =
                    TopUpVoucherListRecycleAdapter(activity)
                rvVoucherList.adapter = voucherListAdapter
                
                getVoucherListData(true, this@TopupVoucherListFragment)
            }
        }
    }

    private fun bottomProgress(isShowProgress: Boolean) {
        with(voucherBinding) {
            cvProgress.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        }
    }

    /**
     * Set voucher data
     *
     * @param isVoucher when data available it's true
     */
    private fun setVoucherData(isVoucher: Boolean) {
        with(voucherListModel) {
            voucherDataManage(isVoucher)
            if (isVoucher) {
                voucherListAdapter?.clearData()
                voucherListAdapter?.updateData(voucherList)
            }
        }
    }

    /**
     * Voucher data manage
     *
     * @param isVoucherVisible when voucher available it's visible
     */
    private fun voucherDataManage(isVoucherVisible: Boolean) {
        with(voucherBinding) {
            rvVoucherList.visibility = if (isVoucherVisible) View.VISIBLE else View.GONE
            clVoucherNotFound.visibility = if (isVoucherVisible) View.GONE else View.VISIBLE
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(voucherListModel) {
            with(voucherBinding) {
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

                /*isVoucherDataAvailable.observe(
                    viewLifecycleOwner
                ) { isVoucher ->
                    srlRefresh.isRefreshing = false
                    setVoucherData(isVoucher)
                }*/

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

    override fun onResponse(isAvailable: Boolean) {
       with(voucherBinding) {
           srlRefresh.isRefreshing = false
           setVoucherData(isAvailable)
       }
    }
}