package com.moneytree.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.NSTabChange
import com.moneytree.app.common.ViewPagerMDAdapter
import com.moneytree.app.common.utils.addText
import com.moneytree.app.databinding.LayoutHeaderNavBinding
import com.moneytree.app.databinding.NsFragmentHomeBinding
import com.moneytree.app.repository.network.responses.GridModel
import com.moneytree.app.ui.slide.GridRecycleAdapter
import org.greenrobot.eventbus.EventBus

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
                    homeListModelClassArrayList1!!
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

    private fun navigationView() {
        with(homeBinding) {
            with(homeModel) {
                with(nsUserData!!) {
                    val navView: View = navView.getHeaderView(0)
                    val navBinding = LayoutHeaderNavBinding.bind(navView)
                    with(navBinding) {
                        tvUserName.text = userName
                        tvEmailId.text = email
                        tvIcon.text = email!!.substring(0, 1).uppercase()

                        //Click
                        llRegister.setOnClickListener {
                            EventBus.getDefault().post(NSTabChange(R.id.tb_register))
                        }

                        llVouchers.setOnClickListener {
                            EventBus.getDefault().post(NSTabChange(R.id.tb_vouchers))
                        }

                        llRePurchase.setOnClickListener {
                            EventBus.getDefault().post(NSTabChange(R.id.tb_offers))
                        }
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
}