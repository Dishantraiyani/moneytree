package com.moneytree.app.ui.packageVoucher.packageDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.utils.switchResultActivity
import com.moneytree.app.databinding.NsFragmentPackageDetailBinding
import com.moneytree.app.ui.coins.transfer.NSTransferActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSPackageDetailFragment : NSFragment() {
    private val packageListModel: NSPackageDetailViewModel by lazy {
        ViewModelProvider(this).get(NSPackageDetailViewModel::class.java)
    }
    private var _binding: NsFragmentPackageDetailBinding? = null

    private val packageBinding get() = _binding!!
    private var packageListAdapter: NSPackageDetailRecycleAdapter? = null

	companion object {
		fun newInstance(bundle: Bundle?) = NSPackageDetailFragment().apply {
			arguments = bundle
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			with(packageListModel) {
				strPackageDetail = it.getString(NSConstants.KEY_PACKAGE_DETAIL)
				getDetail()
			}
		}
	}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentPackageDetailBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return packageBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(packageBinding) {
            HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.package_detail))
            setPackageListAdapter()
        }
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(packageBinding) {
            with(packageListModel) {
                btnSubmit.setOnClickListener {
                    switchResultActivity(dataResult, NSTransferActivity::class.java, bundleOf(
                        NSConstants.KEY_IS_VOUCHER_FROM_TRANSFER to true,
                        NSConstants.KEY_IS_PACKAGE_ID to packageData!!.packageId,
                        NSConstants.KEY_IS_VOUCHER_QUANTITY to packageListModel.voucherQuantity
                    ))
                }
            }
        }
    }

    /**
     * To add data of register in list
     */
    private fun setPackageListAdapter() {
        with(packageBinding) {
            with(packageListModel) {
                rvPackageList.layoutManager = LinearLayoutManager(activity)
                packageListAdapter =
					NSPackageDetailRecycleAdapter()
				rvPackageList.adapter = packageListAdapter
            }
        }
    }

    /**
     * Set register data
     *
     * @param isPackage when data available it's true
     */
    private fun setPackageData(isPackage: Boolean) {
        with(packageListModel) {
            packageDataManage(isPackage)
            if (isPackage) {
                packageListAdapter!!.clearData()
                packageListAdapter!!.updateData(packageList)
            }
        }
    }

    /**
     * Register data manage
     *
     * @param isPackageVisible when register available it's visible
     */
    private fun packageDataManage(isPackageVisible: Boolean) {
        with(packageBinding) {
            rvPackageList.visibility = if (isPackageVisible) View.VISIBLE else View.GONE
            clPackageNotFound.visibility = if (isPackageVisible) View.GONE else View.VISIBLE
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(packageListModel) {
            with(packageBinding) {
                isProgressShowing.observe(
                    viewLifecycleOwner
                ) { shouldShowProgress ->
                    updateProgress(shouldShowProgress)
                }

				isDataAvailable.observe(
					viewLifecycleOwner
				) { isNotification ->
					if (isNotification) {
						getPackageListData(true)
						isDataAvailable.value = false
					}
				}

                isPackageDataAvailable.observe(
                    viewLifecycleOwner
                ) { isNotification ->
					tvVoucherQty.text = voucherQuantity?:""
                    setPackageData(isNotification)
                }

                failureErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                    showAlertDialog(errorMessage)
                }

                apiErrors.observe(viewLifecycleOwner) { apiErrors ->
                    parseAndShowApiError(apiErrors)
                }

                noNetworkAlert.observe(viewLifecycleOwner) {
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

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onResultEvent(event: NSActivityEvent) {
		if (event.resultCode == NSRequestCodes.REQUEST_WALLET_UPDATE_TRANSFER) {
			packageListModel.getPackageListData(true)
		}
	}
}
