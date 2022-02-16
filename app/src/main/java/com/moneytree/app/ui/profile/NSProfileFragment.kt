package com.moneytree.app.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.moneytree.app.BuildConfig
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.callbacks.NSProfileSelectCallback
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.databinding.NsFragmentProfileBinding
import com.moneytree.app.ui.login.NSLoginActivity
import com.moneytree.app.ui.main.NSMainActivity
import com.moneytree.app.ui.memberTree.MemberTreeActivity
import com.moneytree.app.ui.notification.NSNotificationFragment
import com.moneytree.app.ui.transaction.NSTransactionFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class NSProfileFragment : NSFragment() {
    private val profileModel: NSProfileViewModel by lazy {
        ViewModelProvider(this).get(NSProfileViewModel::class.java)
    }
    private var _binding: NsFragmentProfileBinding? = null

    private val profileBinding get() = _binding!!
    private var profileAdapter: ProfileRecycleAdapter? = null

    companion object {
        fun newInstance() = NSProfileFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = NsFragmentProfileBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return profileBinding.root
    }

    /**
     * View created
     *
     */
    private fun viewCreated() {
        observeViewModel()
        with(profileBinding) {
            with(profileModel) {
                with(layoutHeader) {
                    tvHeaderBack.text = resources.getString(R.string.profile_title)
                    clBack.visibility = View.VISIBLE
                }
                getUserDetail()
                //getWalletData()
            }
        }
        setProfileAdapter()
    }

    /**
     * Set listener
     *
     */
    private fun setListener() {
        with(profileModel) {
            with(profileBinding) {
                with(layoutHeader) {
                    clBack.setOnClickListener {
                        EventBus.getDefault().post(BackPressEvent())
                    }
                }
            }
        }
    }

    /**
     * Set profile adapter
     *
     */
    private fun setProfileAdapter() {
        with(profileBinding) {
            with(profileModel) {
                getProfileListData(activity)
                rvProfile.layoutManager = LinearLayoutManager(activity)
                profileAdapter =
                    ProfileRecycleAdapter(profileItemList, false, object : NSProfileSelectCallback {
                        override fun onPosition(position: Int) {
                            onClickProfile(position)
                        }
                    })
                rvProfile.adapter = profileAdapter
                rvProfile.isNestedScrollingEnabled = false
            }
        }
    }

    /**
     * On click profile
     *
     * @param position adapter position click
     */
    private fun onClickProfile(position: Int) {
        when (position) {
            0 -> {
                switchActivity(
                    MemberTreeActivity::class.java,
                    bundleOf(
                        NSConstants.MEMBER_TREE_ENABLE to true
                    )
                )
            }
            1 -> {
                switchActivity(
                    MemberTreeActivity::class.java,
                    bundleOf(
                        NSConstants.MEMBER_TREE_ENABLE to false
                    )
                )
            }
            /*0 -> {
                showDialogLanguageSelect()
            }*/
            3 -> {
                EventBus.getDefault().post(NSFragmentChange(NSNotificationFragment.newInstance()))
            }
            4 -> {

            }
            5 -> {
                EventBus.getDefault().post(NSFragmentChange(NSTransactionFragment.newInstance()))
            }
            6 -> {

            }
            2 -> {
                with(profileModel) {
                    with(activity.resources) {
                        showLogoutDialog(getString(R.string.logout), getString(R.string.logout_message), getString(R.string.no_title), getString(R.string.yes_title))
                    }
                }

            }
        }
    }

    private fun setWalletData(isWalletData: Boolean) {
        with(profileBinding) {
            with(profileModel) {
                if (isWalletData) {
                    apiValue = 0
                }
            }
        }
    }

    private fun setUserData(isUserData: Boolean) {
        with(profileBinding) {
            with(profileModel) {
                if (isUserData) {
                    with(nsUserData!!) {
                        tvUserName.text = userName
                        tvMobile.text = mobile
                        tvEmailId.text = email
                        tvIcon.text = email!!.substring(0, 1).uppercase()
                    }
                }
            }
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(profileModel) {
            with(profileBinding) {
                isProgressShowing.observe(
                    viewLifecycleOwner
                ) { shouldShowProgress ->
                    updateProgress(shouldShowProgress)
                }

                isLogout.observe(
                    viewLifecycleOwner
                ) { isLogout ->
                    NSLog.d(TAG, "observeViewModel: $isLogout")
                    NSApplication.getInstance().getPrefs().clearPrefData()
                    switchActivity(
                        NSLoginActivity::class.java,
                        flags = intArrayOf(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    )
                }

                isUserDataAvailable.observe(
                    viewLifecycleOwner
                ) { isUserData ->
                    setUserData(isUserData)
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
    fun onPositiveButtonClickEvent(event: NSAlertButtonClickEvent) {
        if (event.buttonType == NSConstants.KEY_ALERT_BUTTON_NEGATIVE && event.alertKey == NSConstants.LOGOUT_CLICK) {
           with(profileModel) {
               apiValue = 1
               logout()
           }
        }
    }
}