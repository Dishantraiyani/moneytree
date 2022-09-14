package com.moneytree.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.callbacks.NSRechargeSelectCallback
import com.moneytree.app.common.utils.*
import com.moneytree.app.databinding.LayoutHeaderNavBinding
import com.moneytree.app.databinding.NsFragmentHomeBinding
import com.moneytree.app.repository.NSUserRepository.logout
import com.moneytree.app.repository.network.responses.GridModel
import com.moneytree.app.ui.activate.NSActivateActivity
import com.moneytree.app.ui.login.NSLoginActivity
import com.moneytree.app.ui.productCategory.NSProductsCategoryActivity
import com.moneytree.app.ui.recharge.NSRechargeActivity
import com.moneytree.app.ui.reports.NSReportsActivity
import com.moneytree.app.ui.slide.GridRecycleAdapter
import com.moneytree.app.ui.slots.NSSlotsActivity
import com.moneytree.app.ui.vouchers.NSVouchersActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList

class NSHomeFragment : NSFragment() {
    private val homeModel: NSHomeViewModel by lazy {
        ViewModelProvider(this)[NSHomeViewModel::class.java]
    }
    private var _binding: NsFragmentHomeBinding? = null
    private val homeBinding get() = _binding!!
    private var homeListModelClassArrayList1: ArrayList<GridModel>? = null
    private var bAdapterNS: GridRecycleAdapter? = null

    companion object {
        fun newInstance() = NSHomeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            with(homeModel) {
                strHomeData = it.getString(NSConstants.KEY_HOME_DETAIL)
                getHomeDetail()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentHomeBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return homeBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(homeBinding) {
            with(homeModel) {
                with(layoutHeader) {
                    clBack.visibility = View.VISIBLE
                    tvHeaderBack.text = activity.resources.getString(R.string.app_name)
                    ivBack.visibility = View.INVISIBLE
                    ivMenu.visibility = View.VISIBLE
                    tvAmountData.visibility = View.VISIBLE
                }
                getUserDetail()
                setFragmentData()
                getDashboardData(true)
                setupViewPager(viewPager)
                addRechargeItems()
            }
        }
        observeViewModel()
    }

    // Add Fragments to Tabs
    private fun setupViewPager(viewPager: ViewPager2) {
        with(homeModel) {
            with(homeBinding) {
                try {
                    val adapter = ViewPagerMDAdapter(requireActivity())
                    adapter.setFragment(mFragmentList)
                    viewPager.adapter = adapter
                    indicator.setViewPager(viewPager)
                    adapter.registerAdapterDataObserver(indicator.adapterDataObserver)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun addRechargeItems() {
        with(homeBinding) {
            with(homeModel) {
                val layoutManager = GridLayoutManager(activity, 4)
                recyclerView.layoutManager = layoutManager
                recyclerView.itemAnimator = DefaultItemAnimator()
                homeListModelClassArrayList1 = ArrayList()
                fieldName = resources.getStringArray(R.array.recharge_list)
                for (i in fieldName.indices) {
                    val gridModel = GridModel(fieldName[i], fieldImage[i])
                    homeListModelClassArrayList1!!.add(gridModel)
                }
                bAdapterNS = GridRecycleAdapter(
                    homeListModelClassArrayList1!!, object : NSRechargeSelectCallback{
                        override fun onClick(position: Int) {
                            switchActivity(NSRechargeActivity::class.java)
                        }
                    }
                )
                recyclerView.adapter = bAdapterNS
            }
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(homeBinding) {
            layoutHeader.ivMenu.setOnClickListener { drawer.openDrawer(GravityCompat.START) }
            drawer.closeDrawer(GravityCompat.START)

            clReports.setOnClickListener {
                switchActivity(
                    NSReportsActivity::class.java
                )
            }

            clVoucherBtn.setOnClickListener {
                switchActivity(
                    NSVouchersActivity::class.java
                )
            }

            clCoinBtn.setOnClickListener {
                /*switchActivity(
                    NSSlotsActivity::class.java,
                    bundleOf(
                        NSConstants.KEY_SLOTS_INFO to Gson().toJson(homeModel.dashboardData!!.data!!.slotList)
                    )
                )*/
            }

			tvUpdate.setOnClickListener(object : SingleClickListener() {
				override fun performClick(v: View?) {
					switchActivity(NSActivateActivity::class.java)
				}
			})
        }
    }

    private fun setUserData(isUserData: Boolean) {
        with(homeBinding) {
            with(homeModel) {
                if (isUserData) {
                    with(nsUserData!!) {
                        tvUserName.text = addText(activity, R.string.name, userName!!)
                        tvAccountNo.text = addText(activity, R.string.ac_no, acNo!!)
                        navigationView()
						tvActive.visible()

						if (nsUserData!!.isActive.equals("Y")) {
							pref.isActive = true
							tvActive.text = activity.resources.getString(R.string.active)
						} else {
							pref.isActive = false
							tvActive.text = activity.resources.getString(R.string.deActive)
						}
                    }
                }
            }
        }
    }

    private fun setDashboardData(isDashboardData: Boolean) {
        with(homeBinding) {
            with(homeModel) {
                if (isDashboardData) {
                    tvDownline.text = addText(activity, R.string.dashboard_data, setDownLine())
                    tvVoucher.text = addText(activity, R.string.dashboard_data, setVoucher())
                    tvJoinVoucher.text = addText(activity, R.string.dashboard_data, setJoinVoucher())
                    tvBalance.text = addText(activity, R.string.balance, setWallet())
					NSApplication.getInstance().setWalletBalance(setWallet())
                    //setBold(setRoyaltyStatus())
                    tvStatusRoyalty.text = addText(activity, R.string.status_royalty, setRoyaltyStatus())
                    layoutHeader.tvAmountData.text = addText(activity, R.string.my_earning, setEarningAmount())
                    //This is display Message slider
                    /*if (data!!.directRetailStatus.isNotEmpty() && data!!.colour.isNotEmpty()) {
                        tvMessage.setTextColor(Color.parseColor(data!!.colour))
                        tvMessage.text = data!!.directRetailStatus
                        tvMessage.visibility = View.VISIBLE
                        tvMessage.isSelected = true
                    } else {
                        tvMessage.visibility = View.GONE
                    }*/
                }
            }
        }
    }

    private fun setBold(value: String) {
        with(homeBinding) {
            val html = "Status: <b>${value.lowercase().replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }}</b>"
            tvStatusRoyalty.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
            tvStatusRoyalty.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun navigationView() {
        with(homeBinding) {
            with(homeModel) {
                with(nsUserData!!) {
                    val navView: View = navView.getHeaderView(0)
                    val navBinding = LayoutHeaderNavBinding.bind(navView)
                    with(navBinding) {
                        tvUserName.text = setUserName(activity, userName!!)
                        tvEmailId.text = setEmail(activity, email!!)
                        if (!email.isNullOrEmpty()) {
                            tvIcon.text = email!!.substring(0, 1).uppercase()
                        } else {
                            tvIcon.text = getString(R.string.app_first)
                        }

						llHome.setOnClickListener {
							drawer.closeDrawer(GravityCompat.START)
						}

                        //Click
                        llRegister.setOnClickListener {
                            EventBus.getDefault().post(NSTabChange(R.id.tb_register))
                        }

						llVouchers.setOnClickListener {
							drawer.closeDrawer(GravityCompat.START)
							switchActivity(
								NSVouchersActivity::class.java
							)
						}

                        llWallet.setOnClickListener {
                            EventBus.getDefault().post(NSTabChange(R.id.tb_wallets))
                        }

						llProducts.setOnClickListener {
							drawer.closeDrawer(GravityCompat.START)
							switchActivity(NSProductsCategoryActivity::class.java)
						}

						llActivate.setOnClickListener {
							drawer.closeDrawer(GravityCompat.START)
							switchActivity(NSActivateActivity::class.java)
						}

                        llRePurchase.setOnClickListener {
                            EventBus.getDefault().post(NSTabChange(R.id.tb_offers))
                        }

						llLogout.setOnClickListener(object : SingleClickListener() {
							override fun performClick(v: View?) {
								with(activity.resources) {
									showLogoutDialog(getString(R.string.logout), getString(R.string.logout_message), getString(R.string.no_title), getString(R.string.yes_title))
								}
							}
						})
                    }
                }
            }
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(homeModel) {
            isProgressShowing.observe(
                viewLifecycleOwner
            ) { shouldShowProgress ->
                updateProgress(shouldShowProgress)
            }

            isUserDataAvailable.observe(
                viewLifecycleOwner
            ) { isUserData ->
                setUserData(isUserData)
            }

            isDashboardDataAvailable.observe(
                viewLifecycleOwner
            ) { isDashboardDataAvailable ->
                setDashboardData(isDashboardDataAvailable)
            }

			isLogout.observe(
				viewLifecycleOwner
			) { isLogout ->
				NSLog.d(tags, "observeViewModel: $isLogout")
				NSApplication.getInstance().getPrefs().clearPrefData()
				switchActivity(
					NSLoginActivity::class.java,
					flags = intArrayOf(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
				)
			}

            failureErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                showAlertDialog(errorMessage)
            }

            apiErrors.observe(viewLifecycleOwner) { apiErrors ->
                parseAndShowApiError(apiErrors)
            }

            noNetworkAlert.observe(viewLifecycleOwner) {
                showNoNetworkAlertDialog(
                    getString(R.string.no_network_available), getString(
                        R.string.network_unreachable
                    )
                )
            }

            validationErrorId.observe(viewLifecycleOwner) { errorId ->
                showAlertDialog(getString(errorId))
            }
        }
    }

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onPositiveButtonClickEvent(event: NSAlertButtonClickEvent) {
		if (event.buttonType == NSConstants.KEY_ALERT_BUTTON_NEGATIVE && event.alertKey == NSConstants.LOGOUT_CLICK) {
			with(homeModel) {
				logout()
			}
		}
	}

	override fun onResume() {
		super.onResume()
	//	NSUtilities.showUpdateDialog(activity, false)
	}
}
