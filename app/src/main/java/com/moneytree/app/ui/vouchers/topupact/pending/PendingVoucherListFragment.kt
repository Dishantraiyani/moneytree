package com.moneytree.app.ui.vouchers.topupact.pending

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.callbacks.NSDialogClickCallback
import com.moneytree.app.common.callbacks.NSJoiningVoucherCallback
import com.moneytree.app.common.utils.setupWithAdapter
import com.moneytree.app.common.utils.setupWithAdapterAndCustomLayoutManager
import com.moneytree.app.databinding.FragmentPendingVoucherListBinding
import com.moneytree.app.databinding.LayoutStatusOptionsBinding
import com.moneytree.app.repository.network.responses.PendingVoucherListData
import com.moneytree.app.repository.network.responses.StatusOptionModel

class PendingVoucherListFragment : NSFragment(), NSJoiningVoucherCallback {
    private val viewModel: PendingVoucherListViewModel by lazy {
	    ViewModelProvider(this)[PendingVoucherListViewModel::class.java]
    }
    private var _binding: FragmentPendingVoucherListBinding? = null

    private val voucherBinding get() = _binding!!
    private var voucherListAdapter: PendingVoucherListRecycleAdapter? = null
    private var statusOptionDialog: BottomSheetDialog? = null
    
    companion object {
        fun newInstance() = PendingVoucherListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPendingVoucherListBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return voucherBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        HeaderUtils(voucherBinding.layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.pending_voucher_title))
        
        setVoucherAdapter()
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(viewModel) {
            with(voucherBinding) {
                srlRefresh.setOnRefreshListener {
                    getVoucherListData(false, this@PendingVoucherListFragment)
                }
            }
        }
    }

    /**
     * To add data of vouchers in list
     */
    private fun setVoucherAdapter() {
        with(voucherBinding) {
            with(viewModel) {
                rvVoucherList.layoutManager = LinearLayoutManager(activity)
                voucherListAdapter = PendingVoucherListRecycleAdapter(activity) {
                    showDialogLanguageSelect(it)
                }
                rvVoucherList.adapter = voucherListAdapter
                
                getVoucherListData(true, this@PendingVoucherListFragment)
            }
        }
    }
    
    @SuppressLint("NotifyDataSetChanged")
    fun showDialogLanguageSelect(model: PendingVoucherListData) {
        try {
            val sheetView: View = layoutInflater.inflate(R.layout.layout_status_options, null)
            statusOptionDialog = BottomSheetDialog(requireActivity(), R.style.MyBottomSheetDialogTheme)
            statusOptionDialog?.setContentView(sheetView)
            statusOptionDialog?.setCanceledOnTouchOutside(false)
            statusOptionDialog?.show()
            val statusBinding = LayoutStatusOptionsBinding.bind(sheetView)
            var selectedLanguage = 0
            var selectedProductOption = 0
            
            val optionList: MutableList<StatusOptionModel> = arrayListOf()
            optionList.add(StatusOptionModel("Self/Store", true, true))
            optionList.add(StatusOptionModel("Door Delivery", false, false))
            
            val productOptionList: MutableList<StatusOptionModel> = arrayListOf()
            productOptionList.add(StatusOptionModel("Option 1", true, true))
            productOptionList.add(StatusOptionModel("Option 2", false, true))
            productOptionList.add(StatusOptionModel("Option 3", false, true))
            productOptionList.add(StatusOptionModel("Option 4", false, true))
            
            with(statusBinding) {
                statusOptionDialog?.setCancelable(false)
               
                var languageAdapter: StatusOptionRecycleAdapter? = null
                languageAdapter = StatusOptionRecycleAdapter {
                    languageAdapter?.getData()?.forEach {
                        it.isChecked = false
                    }
                    languageAdapter?.getData()?.get(it)?.isChecked = true
                    languageAdapter?.notifyDataSetChanged()
                    selectedLanguage = it
                }
                
                rvList.setupWithAdapter(languageAdapter)
                languageAdapter.setData(optionList)
                rvList.isNestedScrollingEnabled = false
                
                var productOptionAdapter: StatusOptionRecycleAdapter? = null
                productOptionAdapter = StatusOptionRecycleAdapter {
                    productOptionAdapter?.getData()?.forEach {
                        it.isChecked = false
                    }
                    productOptionAdapter?.getData()?.get(it)?.isChecked = true
                    productOptionAdapter?.notifyDataSetChanged()
                    selectedProductOption = it
                }
                
                rvProductOptions.setupWithAdapterAndCustomLayoutManager(productOptionAdapter, GridLayoutManager(requireContext(), 2))
                productOptionAdapter.setData(productOptionList)
                rvProductOptions.isNestedScrollingEnabled = false
                
                btnSubmit.setOnClickListener {
                    statusOptionDialog?.dismiss()
                    viewModel.topupVoucherClaim(true, model.topup28VoucherId, selectedLanguage.toString(), selectedProductOption.toString())
                }
                
                // dismiss dialog
                tvCancel.setOnClickListener {
                    statusOptionDialog?.dismiss()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
        with(viewModel) {
            voucherDataManage(isVoucher)
            if (isVoucher) {
                voucherListAdapter?.clearData()
                voucherListAdapter?.updateDataValue(pendingVoucherList)
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
        with(viewModel) {
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
                
                isPendingDataAvailable.observe(viewLifecycleOwner) {
                    if (it) {
                        if (successResponse?.status == true) {
                            showSuccessDialog(
                                activity.resources.getString(R.string.app_name),
                                successResponse?.message,
                                NSConstants.REDEEM_SAVE_CLICK, callback = object : NSDialogClickCallback {
                                    override fun onClick(isOk: Boolean) {
                                        getVoucherListData(true, this@PendingVoucherListFragment)
                                    }
                                }
                            )
                        }
                    }
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