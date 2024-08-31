package com.moneytree.app.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.callbacks.NSNotificationCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.NsFragmentNotificationBinding
import com.moneytree.app.repository.network.responses.NSNotificationListData
import com.moneytree.app.ui.notificationDetail.NSNotificationDetailActivity


class NSNotificationFragment : NSFragment() {
    private val notificationModel: NSNotificationViewModel by lazy {
        ViewModelProvider(this)[NSNotificationViewModel::class.java]
    }
    private var _binding: NsFragmentNotificationBinding? = null

    private val notificationBinding get() = _binding!!
    private var notificationAdapter: NSNotificationRecycleAdapter? = null
    companion object {
        fun newInstance() = NSNotificationFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentNotificationBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return notificationBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
		notificationBinding.apply {
            HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.notification_title))
		}
        setNotificationAdapter()
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(notificationBinding) {
            with(notificationModel) {
                srlRefresh.setOnRefreshListener {
					pageIndex = "1"
					getNotificationData(pageIndex, false, isBottomProgress = false)
                }
            }
        }
    }

    /**
     * To add data of notification in list
     */
    private fun setNotificationAdapter() {
        with(notificationBinding) {
            with(notificationModel) {
                rvNotificationList.layoutManager = LinearLayoutManager(activity)
                notificationAdapter =
                    NSNotificationRecycleAdapter(object : NSNotificationCallback {
                        override fun onClick(data: NSNotificationListData) {
							if (data.type.equals(NSConstants.KEY_DEFAULT_TYPE)) {
								switchActivity(NSNotificationDetailActivity::class.java, bundleOf(NSConstants.KEY_NOTIFICATION_DETAIL to Gson().toJson(data)))
							} else {

							}
                        }
                    }, object : NSPageChangeCallback {
						override fun onPageChange(pageNo: Int) {
							if (notificationResponse!!.nextPage) {
								val page: Int = notificationList.size/ NSConstants.PAGINATION + 1
								pageIndex = page.toString()
								getNotificationData(pageIndex, true, isBottomProgress = true)
							}
						}

					})
                rvNotificationList.adapter = notificationAdapter
                pageIndex = "1"
                getNotificationData(pageIndex, true, isBottomProgress = false)
            }
        }
    }

    /**
     * Set notification data
     *
     * @param isNotification when data available it's true
     */
    private fun setNotificationData(isNotification: Boolean) {
        with(notificationModel) {
            notificationDataManage(isNotification)
            if (isNotification) {
                notificationAdapter!!.clearData()
                notificationAdapter!!.updateData(notificationList)
            }
        }
    }

    /**
     * Notification data manage
     *
     * @param isNotificationVisible when notification available it's visible
     */
    private fun notificationDataManage(isNotificationVisible: Boolean) {
        with(notificationBinding) {
            rvNotificationList.visibility = if (isNotificationVisible) View.VISIBLE else View.GONE
            clNotificationNotFound.visibility = if (isNotificationVisible) View.GONE else View.VISIBLE
        }
    }

	private fun bottomProgress(isShowProgress: Boolean) {
		with(notificationBinding) {
			cvProgress.visibility = if (isShowProgress) View.VISIBLE else View.GONE
		}
	}

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(notificationModel) {
            with(notificationBinding) {
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

                isNotificationDataAvailable.observe(
                    viewLifecycleOwner
                ) { isNotification ->
                    srlRefresh.isRefreshing = false
                    setNotificationData(isNotification)
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
