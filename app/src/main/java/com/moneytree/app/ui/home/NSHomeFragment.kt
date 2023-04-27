package com.moneytree.app.ui.home

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.moneytree.app.BuildConfig
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.callbacks.NSRechargeSelectCallback
import com.moneytree.app.common.utils.*
import com.moneytree.app.common.utils.NSUtilities.showPopUpHome
import com.moneytree.app.common.utils.NSUtilities.showUpdateDialog
import com.moneytree.app.databinding.LayoutHeaderNavBinding
import com.moneytree.app.databinding.NsFragmentHomeBinding
import com.moneytree.app.repository.network.responses.GridModel
import com.moneytree.app.repository.network.responses.NSCheckVersionResponse
import com.moneytree.app.ui.activate.NSActivateActivity
import com.moneytree.app.ui.downloads.NSDownloadPlansActivity
import com.moneytree.app.ui.login.NSLoginActivity
import com.moneytree.app.ui.notification.NSNotificationActivity
import com.moneytree.app.ui.offers.OffersActivity
import com.moneytree.app.ui.productCategory.MTProductsCategoryActivity
import com.moneytree.app.ui.qrCode.QRCodeActivity
import com.moneytree.app.ui.recharge.NSRechargeActivity
import com.moneytree.app.ui.register.NSAddRegisterFragment
import com.moneytree.app.ui.reports.NSReportsActivity
import com.moneytree.app.ui.slide.GridRecycleAdapter
import com.moneytree.app.ui.vouchers.NSVouchersActivity
import com.moneytree.app.ui.wallets.NSWalletFragment
import com.moneytree.app.ui.wallets.redeemForm.NSAddRedeemActivity
import com.moneytree.app.ui.wallets.transfer.NSTransferActivity
import com.moneytree.app.ui.youtube.YoutubeActivity
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import maulik.barcodescanner.OnScannerResponse
import maulik.barcodescanner.ui.BarcodeScanningActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


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
					NSConstants.tabName = this@NSHomeFragment.javaClass
                    clBack.visibility = View.VISIBLE
                    tvHeaderBack.text = activity.resources.getString(R.string.app_name)
                    ivBack.visibility = View.INVISIBLE
                    ivMenu.visibility = View.VISIBLE
                    tvAmountData.visibility = View.VISIBLE
                }
                getUserDetail()
                checkVersion()
                getDashboardData(true)
                addRechargeItems()
				setRechargeLayout()
            }
        }
        observeViewModel()
    }

	private fun showPopup(fileName: String) {
		with(homeBinding) {
			if (pref.isPopupDisplay) {
				pref.isPopupDisplay = false
				showPopUpHome(activity, fileName)
			}
		}
	}

    // Add Fragments to Tabs
    private fun setupViewPager(viewPager: SliderView) {
        with(homeModel) {
            with(homeBinding) {
                try {
					rlBanner.setVisibility(mFragmentList.isValidList())
					val pagerAdapter = SliderAdapter(requireActivity(), mFragmentList)
					viewPager.setSliderAdapter(pagerAdapter)
					pagerAdapter.notifyDataSetChanged()
					viewPager.setIndicatorAnimation(IndicatorAnimationType.NONE);
					viewPager.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
					viewPager.startAutoCycle();
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
                fieldName = resources.getStringArray(R.array.recharge_list_home)

				for (i in fieldName.indices) {
                    val gridModel = GridModel(fieldName[i], fieldImage[i])
                    homeListModelClassArrayList1!!.add(gridModel)
                }

				bAdapterNS = GridRecycleAdapter(
                    homeListModelClassArrayList1!!, object : NSRechargeSelectCallback {
                        override fun onClick(position: Int) {
							switchActivity(
								NSRechargeActivity::class.java,
								bundle = bundleOf(NSConstants.KEY_RECHARGE_TYPE to fieldName[position])
							)
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

            clVoucherBtn.setOnClickListener {
                switchActivity(
                    NSVouchersActivity::class.java
                )
            }

			clDownLine.setOnClickListener {
				EventBus.getDefault().post(NSTabChange(R.id.tb_register))
			}

			clQrCode.setOnClickListener(object : SingleClickListener() {
				override fun performClick(v: View?) {
					activityResultPermission.launch(Manifest.permission.CAMERA)
				}
			})

            tvUpdate.setOnClickListener(object : SingleClickListener() {
                override fun performClick(v: View?) {
                    switchActivity(NSActivateActivity::class.java)
                }
            })
        }
    }

	private fun setRechargeLayout() {
		homeBinding.layoutWalletRecharge.apply {
			ivFieldImage.setImageResource(R.drawable.ic_wallet_recharge)
			tvFieldName.text = activity.resources.getString(R.string.recharge)
			llRecharge.setOnClickListener {
				switchActivity(
					NSRechargeActivity::class.java,
					bundle = bundleOf(NSConstants.KEY_RECHARGE_TYPE to homeModel.fieldName[0])
				)
			}
		}

		homeBinding.layoutWalletReport.apply {
			ivFieldImage.setImageResource(R.drawable.ic_wallet_report)
			tvFieldName.text = activity.resources.getString(R.string.reports)
			llRecharge.setOnClickListener {
				EventBus.getDefault().post(NSTabChange(R.id.tb_wallets))
				/*switchActivity(
					NSReportsActivity::class.java
				)*/
			}
		}

		homeBinding.layoutWalletTransfer.apply {
			ivFieldImage.setImageResource(R.drawable.ic_wallet_transfer)
			tvFieldName.text = activity.resources.getString(R.string.transfer)
			llRecharge.setOnClickListener {
				switchActivity(NSTransferActivity::class.java, bundleOf(NSConstants.KEY_IS_VOUCHER_FROM_TRANSFER to false))
			}
		}

		homeBinding.layoutWalletRedemption.apply {
			ivFieldImage.setImageResource(R.drawable.ic_wallet_redeam)
			tvFieldName.text = activity.resources.getString(R.string.redemption)
			llRecharge.setOnClickListener {
				switchResultActivity(
					dataResult, NSAddRedeemActivity::class.java,
					bundleOf(
						NSConstants.KEY_AVAILABLE_BALANCE to homeModel.dashboardData?.data?.wltAmt?.get(0)?.amount
					)
				)
			}
		}
	}

    private fun setUserData(isUserData: Boolean) {
        with(homeBinding) {
            with(homeModel) {
                if (isUserData) {
                    with(nsUserData!!) {
                        tvUserName.text = addText(activity, R.string.name, userName!!)
						setAccountNumber(true)
                        navigationView()
                        tvActive.visible()

                        if (nsUserData!!.isActive.equals("Y")) {
                            pref.isActive = true
                            tvActive.text = "${nsUserData?.packageName?.uppercase()} (${activity.resources.getString(R.string.active)})"
                        } else {
                            pref.isActive = false
                            tvActive.text = activity.resources.getString(R.string.deActive)
                        }
                    }
                }
            }
        }
    }

	private fun setAccountNumber(isMasking: Boolean) {
		homeBinding.apply {
			homeModel.apply {
				val mask = nsUserData?.acNo?.replace("\\w(?=\\w{4})".toRegex(), "X")?:""
				tvAccountNo.text = addText(activity, R.string.ac_no, if (isMasking) mask else nsUserData?.acNo?:"")

				ivShowHide.setOnClickListener {
					ivShowHide.setImageResource(if (!isMasking) R.drawable.ic_visible_view else R.drawable.ic_in_visible_view)
					setAccountNumber(!isMasking)
				}
			}
		}
	}

    private fun setDashboardData(isDashboardData: Boolean) {
        with(homeBinding) {
            with(homeModel) {
                if (isDashboardData) {
                    tvDownline.text = addText(activity, R.string.dashboard_data, setDownLine())
                    /*tvVoucher.text = addText(activity, R.string.dashboard_data, setVoucher())
                    tvJoinVoucher.text =
                        addText(activity, R.string.dashboard_data, setJoinVoucher())*/
					NSConstants.SOCKET_TYPE = getSocketType()
                    tvBalance.text = addText(activity, R.string.balance, setWallet())
                    NSApplication.getInstance().setWalletBalance(setWallet())
                    tvStatusRoyalty.text =
                        addText(activity, R.string.status_royalty, setRoyaltyStatus())
                    layoutHeader.tvAmountData.text =
                        addText(activity, R.string.my_earning, setEarningAmount())
					setupViewPager(viewPager)
					showPopup(getPopUpImage())
					EventBus.getDefault().post(NSChangeNavigationMenuNameEvent())
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

						llRegisterSeller.setOnClickListener {
							NSUtilities.openBrowser(activity, "https://moneytree.biz/Seller/Login")
						}

                        llVouchers.setOnClickListener {
                            drawer.closeDrawer(GravityCompat.START)
                            switchActivity(
                                NSVouchersActivity::class.java
                            )
                        }

						llYoutube.setOnClickListener {
                            drawer.closeDrawer(GravityCompat.START)
                            switchActivity(
                                YoutubeActivity::class.java
                            )
                        }

						llNotification.setOnClickListener {
							drawer.closeDrawer(GravityCompat.START)
							switchActivity(
								NSNotificationActivity::class.java
							)
						}

                        llWallet.setOnClickListener {
                            EventBus.getDefault().post(NSTabChange(R.id.tb_wallets))
                        }

                        llProducts.setOnClickListener {
                            drawer.closeDrawer(GravityCompat.START)
                            switchActivity(MTProductsCategoryActivity::class.java)
                        }

						llMyReports.setOnClickListener {
							drawer.closeDrawer(GravityCompat.START)
							switchActivity(
								NSReportsActivity::class.java
							)
						}

                        llActivate.setOnClickListener {
                            drawer.closeDrawer(GravityCompat.START)
                            switchActivity(NSActivateActivity::class.java)
                        }

						llDownload.setOnClickListener {
							drawer.closeDrawer(GravityCompat.START)
							switchActivity(NSDownloadPlansActivity::class.java)
						}

						llContactUs.setOnClickListener {
							NSUtilities.callCustomerCare(requireContext(), NSConstants.CUSTOMER_CARE)
						}

						llInstagram.setOnClickListener {
							drawer.closeDrawer(GravityCompat.START)
							val instagramUrl = "https://www.instagram.com/moneytreeofficial_india/"
							val uri: Uri = Uri.parse(instagramUrl)
							val likeIng = Intent(Intent.ACTION_VIEW, uri)

							likeIng.setPackage("com.instagram.android")

							try {
								startActivity(likeIng)
							} catch (e: ActivityNotFoundException) {
								startActivity(
									Intent(
										Intent.ACTION_VIEW,
										Uri.parse(instagramUrl)
									)
								)
							}

						}

                        llRePurchase.setOnClickListener {
							pref.offerTabPosition = 0
							switchActivity(OffersActivity::class.java)
                        }

                        llLogout.setOnClickListener(object : SingleClickListener() {
                            override fun performClick(v: View?) {
                                with(activity.resources) {
                                    showLogoutDialog(
                                        getString(R.string.logout),
                                        getString(R.string.logout_message),
                                        getString(R.string.no_title),
                                        getString(R.string.yes_title)
                                    )
                                }
                            }
                        })
                    }
                }
            }
        }
    }

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onPermissionResultEvent(event: NSActivityPermissionEvent) {
		if (event.isGranted) {
			openCameraWithScanner()
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

            chekVersionLiveData.observe(viewLifecycleOwner) { apiResponse ->
                checkUpdateDialog(apiResponse)
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

    private fun checkUpdateDialog(apiResponse: NSCheckVersionResponse?) {
        apiResponse?.data?.apply {
            version?.let { version ->
                if (version.isInteger())
                    version.toInt().takeIf { it > BuildConfig.VERSION_CODE }?.let {
                        showUpdateDialog(activity, skip == "1",link)
                    }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkUpdateDialog(homeModel.chekVersionResponse)
    }

	private fun openCameraWithScanner() {
		BarcodeScanningActivity.start(requireContext(), BarcodeScanningActivity.ScannerSDK.MLKIT, object :
			OnScannerResponse {
			override fun onScan(isSuccess: Boolean, value: String) {
				showSnackBar(isSuccess, value)
			}
		})
	}

	private fun showSnackBar(isSuccess: Boolean, value: String) {
		if (isSuccess) {
			switchActivity(QRCodeActivity::class.java, bundleOf(NSConstants.KEY_QR_CODE_ID to value, NSConstants.KEY_WALLET_AMOUNT to homeModel.setWallet()))
		} else {
			Snackbar.make(homeBinding.root, "User canceled", Snackbar.LENGTH_INDEFINITE).apply {
				view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)?.run {
					maxLines = 5
					setTextIsSelectable(true)
				}
				setAction(R.string.ok) { }
				setActionTextColor(Color.parseColor(activity.resources.getString(R.string.orange)))
			}.show()
		}
	}
}
