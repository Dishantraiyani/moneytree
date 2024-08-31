package com.moneytree.app.ui.packageVoucher.packageList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.BackPressEvent
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.callbacks.NSPackageClickCallback
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.databinding.NsFragmentPackageListBinding
import com.moneytree.app.repository.network.responses.NSPackageData
import com.moneytree.app.ui.packageVoucher.packageDetail.PackageDetailActivity
import org.greenrobot.eventbus.EventBus

class NSPackageListFragment : NSFragment() {
    private val packageListModel: NSPackageListViewModel by lazy {
        ViewModelProvider(this).get(NSPackageListViewModel::class.java)
    }
    private var _binding: NsFragmentPackageListBinding? = null

    private val packageBinding get() = _binding!!
    private var packageListAdapter: NSPackageListRecycleAdapter? = null
    companion object {
        fun newInstance() = NSPackageListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentPackageListBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return packageBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(packageBinding) {
            HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.package_list))
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
                srlRefresh.setOnRefreshListener {
                    getPackageListData(false)
                }

                with(layoutHeader) {
                    clBack.setOnClickListener {
                        EventBus.getDefault().post(BackPressEvent())
                    }
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
                    NSPackageListRecycleAdapter(activity, object : NSPackageClickCallback {
						override fun onResponse(packageData: NSPackageData) {
							switchActivity(PackageDetailActivity::class.java, bundleOf(NSConstants.KEY_PACKAGE_DETAIL to Gson().toJson(packageData)))
						}
					})
                rvPackageList.adapter = packageListAdapter
                getPackageListData(true)
            }
        }
    }

    private fun bottomProgress(isShowProgress: Boolean) {
        with(packageBinding) {
            cvProgress.visibility = if (isShowProgress) View.VISIBLE else View.GONE
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

                isBottomProgressShowing.observe(
                    viewLifecycleOwner
                ) { isBottomProgressShowing ->
                    bottomProgress(isBottomProgressShowing)
                }

                isPackageDataAvailable.observe(
                    viewLifecycleOwner
                ) { isNotification ->
                    srlRefresh.isRefreshing = false
                    setPackageData(isNotification)
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
