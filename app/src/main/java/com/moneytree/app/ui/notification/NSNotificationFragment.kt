package com.moneytree.app.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moneytree.app.R
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.callbacks.NSNotificationCallback
import com.moneytree.app.databinding.NsFragmentNotificationBinding
import com.moneytree.app.repository.network.responses.NSNotificationListData


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
        setNotificationAdapter()
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(notificationBinding) {
            clBack.setOnClickListener {
                onBackPress()
            }

            with(notificationModel) {
                srlRefresh.setOnRefreshListener {
                    getNotificationData(false)
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
                    NSNotificationRecycleAdapter(activity, object : NSNotificationCallback {
                        override fun onClick(data: NSNotificationListData) {

                        }
                    })
                rvNotificationList.adapter = notificationAdapter
                getNotificationData(true)
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

                isRefreshComplete.observe(viewLifecycleOwner) {
                    getNotificationData(!srlRefresh.isRefreshing)
                }
            }
        }
    }

}