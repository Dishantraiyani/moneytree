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
import com.moneytree.app.common.NSActivityPermissionEvent
import com.moneytree.app.common.NSAlertButtonClickEvent
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSChangeNavigationMenuNameEvent
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.NSLog
import com.moneytree.app.common.NSTabChange
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.callbacks.NSProductCategoryCallback
import com.moneytree.app.common.callbacks.NSRechargeSelectCallback
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.NSUtilities.showPopUpHome
import com.moneytree.app.common.utils.NSUtilities.showUpdateDialog
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.invisible
import com.moneytree.app.common.utils.isInteger
import com.moneytree.app.common.utils.setEmail
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.common.utils.setUserName
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.common.utils.switchResultActivity
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.LayoutHeaderNavBinding
import com.moneytree.app.databinding.NsFragmentHomeBinding
import com.moneytree.app.repository.network.responses.GridModel
import com.moneytree.app.repository.network.responses.NSCategoryData
import com.moneytree.app.repository.network.responses.NSCheckVersionResponse
import com.moneytree.app.repository.network.responses.NSDataUser
import com.moneytree.app.repository.network.responses.NSJointCategoryDiseasesResponse
import com.moneytree.app.ui.activate.NSActivateActivity
import com.moneytree.app.ui.assessment.NSAssessmentActivity
import com.moneytree.app.ui.common.ProductCategoryViewModel
import com.moneytree.app.ui.doctor.NSDoctorActivity
import com.moneytree.app.ui.downloads.NSDownloadPlansActivity
import com.moneytree.app.ui.login.NSLoginActivity
import com.moneytree.app.ui.mycart.orders.NSOrderActivity
import com.moneytree.app.ui.notification.NSNotificationActivity
import com.moneytree.app.ui.offers.OffersActivity
import com.moneytree.app.ui.paymentSummary.PaymentSummaryActivity
import com.moneytree.app.ui.productCategory.MTProductsCategoryActivity
import com.moneytree.app.ui.products.MTProductsActivity
import com.moneytree.app.ui.qrCode.QRCodeActivity
import com.moneytree.app.ui.recharge.NSRechargeActivity
import com.moneytree.app.ui.recharge.history.NSRechargeHistoryActivity
import com.moneytree.app.ui.recharge.rechargePayment.RozerActivity
import com.moneytree.app.ui.reports.NSReportsActivity
import com.moneytree.app.ui.slide.GridRecycleAdapter
import com.moneytree.app.ui.vouchers.NSVouchersActivity
import com.moneytree.app.ui.wallets.redeemForm.NSAddRedeemActivity
import com.moneytree.app.ui.wallets.transfer.NSTransferActivity
import com.moneytree.app.ui.youtube.YoutubeActivity
import maulik.barcodescanner.OnScannerResponse
import maulik.barcodescanner.ui.BarcodeScanningActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NSHomeFragment : NSFragment() {
    private val homeModel: NSHomeViewModel by lazy {
        ViewModelProvider(this)[NSHomeViewModel::class.java]
    }
	private val productCategoryModel: ProductCategoryViewModel by lazy {
		ViewModelProvider(this)[ProductCategoryViewModel::class.java]
	}
    private var _binding: NsFragmentHomeBinding? = null
    private val homeBinding get() = _binding!!
    private var homeListModelClassArrayList1: ArrayList<GridModel>? = null
    private var bAdapterNS: GridRecycleAdapter? = null

    companion object {
        fun newInstance() = NSHomeFragment()
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
		with(homeModel) {
			setHeader()
			checkVersion()
			getUserDetail()
			getDashboardData(true)
			addRechargeItems()
			setRechargeLayout()
			getKycKey()
		}
        observeViewModel()
    }

	/**
	 * Set listener
	 */
	private fun setListener() {
		with(homeBinding) {
			layoutHeader.ivMenu.setOnClickListener {
				drawer.openDrawer(GravityCompat.START)
			}

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

			cardRechargeHistory.setOnClickListener(object : SingleClickListener() {
				override fun performClick(v: View?) {
					switchActivity(NSRechargeHistoryActivity::class.java, bundleOf(NSConstants.KEY_RECHARGE_TYPE to "All"))
				}
			})

		}
	}

	private fun setHeader() {
		homeBinding.layoutHeader.apply {
			NSConstants.tabName = this@NSHomeFragment.javaClass
			clBack.visible()
			tvHeaderBack.text = activity.resources.getString(R.string.app_name)
			ivBack.invisible()
			ivMenu.visible()
			tvAmountData.visible()
		}
	}

	private fun showPopup(fileName: String) {
		with(homeBinding) {
			if (pref.isPopupDisplay) {
				pref.isPopupDisplay = false
				showPopUpHome(activity, fileName)
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
                        override fun onClick(position: Int, selectedType: String) {
							if (selectedType != "History") {
								switchActivity(
									NSRechargeActivity::class.java,
									bundle = bundleOf(NSConstants.KEY_RECHARGE_TYPE to fieldName[position])
								)
							} else {
								switchActivity(NSRechargeHistoryActivity::class.java, bundleOf(NSConstants.KEY_RECHARGE_TYPE to "All"))
							}
                        }
                    }
                )
                recyclerView.adapter = bAdapterNS
            }
        }
    }


	private fun setRechargeLayout() {
		homeBinding.layoutWalletRecharge.apply {
			ivFieldImage.setImageResource(R.drawable.ic_wallet_recharge)
			tvFieldName.text = activity.resources.getString(R.string.recharge)
			llRecharge.setOnClickListener {
				switchActivity(
					RozerActivity::class.java,
					bundle = bundleOf(NSConstants.KEY_WALLET_AMOUNT to  homeModel.dashboardData?.data?.wltAmt?.get(0)?.amount)
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
				switchActivity(NSTransferActivity::class.java, bundleOf(NSConstants.KEY_IS_VOUCHER_FROM_TRANSFER to false, NSConstants.KEY_AVAILABLE_BALANCE to homeModel.dashboardData?.data?.wltAmt?.get(0)?.amount))
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

    private fun setUserData(userDetail: NSDataUser) {
        with(homeBinding) {
            with(homeModel) {
				userDetail.apply {
					tvUserName.text = addText(activity, R.string.name, userName!!)
					setAccountNumber(true, userDetail)
					tvActive.visible()

					if (activeValue.equals("Y")) {
						pref.isActive = true
						tvActive.text = "${packageName?.uppercase()} (${activity.resources.getString(R.string.active)})"
					} else {
						pref.isActive = false
						tvActive.text = activity.resources.getString(R.string.deActive)
					}
					navigationView(userDetail)
				}
            }
        }
    }

	private fun setAccountNumber(isMasking: Boolean, userDetail: NSDataUser) {
		homeBinding.apply {
			homeModel.apply {
				val mask = userDetail.acNo?.replace("\\w(?=\\w{4})".toRegex(), "X")?:""
				tvAccountNo.text = addText(activity, R.string.ac_no, if (isMasking) mask else userDetail.acNo?:"")

				ivShowHide.setOnClickListener {
					ivShowHide.setImageResource(if (!isMasking) R.drawable.ic_visible_view else R.drawable.ic_in_visible_view)
					setAccountNumber(!isMasking, userDetail)
				}
			}
		}
	}

    private fun setDashboardData(isDashboardData: Boolean) {
        with(homeBinding) {
            with(homeModel) {
                if (isDashboardData) {
					setKycStatus(activity)
                    tvDownline.text = addText(activity, R.string.dashboard_data, setDownLine())
					NSConstants.SOCKET_TYPE = getSocketType()
                    tvBalance.text = addText(activity, R.string.balance, setWallet())
                    NSApplication.getInstance().setWalletBalance(setWallet())
                    tvStatusRoyalty.text =
                        addText(activity, R.string.status_royalty, setRoyaltyStatus())
                    layoutHeader.tvAmountData.text =
                        addText(activity, R.string.my_earning, setEarningAmount())
					clQrCode.setVisibility(dashboardData?.data?.qrStatus.equals("Active"))
					HomeRepository.setupViewPager(activity, homeBinding, homeModel, viewPager)
					showPopup(getPopUpImage())
					EventBus.getDefault().post(NSChangeNavigationMenuNameEvent())
					productCategoryModel.getProductCategory(true)
                }
            }
        }
    }

    private fun navigationView(userDetail: NSDataUser) {
        with(homeBinding) {
            with(homeModel) {
                userDetail.apply {
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

						llDoctor.setOnClickListener {
							drawer.closeDrawer(GravityCompat.START)
							switchActivity(
								NSDoctorActivity::class.java
							)
						}

						llPaymentSummary.setSafeOnClickListener {
							drawer.closeDrawer(GravityCompat.START)
							switchActivity(
								PaymentSummaryActivity::class.java
							)
						}

						llOrders.setOnClickListener {
							drawer.closeDrawer(GravityCompat.START)
							switchActivity(
								NSOrderActivity::class.java
							)
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

						llAssessment.setOnClickListener {
                            drawer.closeDrawer(GravityCompat.START)
                            switchActivity(NSAssessmentActivity::class.java)
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

	private fun setCategoryData(categoryResponse: NSJointCategoryDiseasesResponse) {
		homeBinding.apply {
			val layoutManager = GridLayoutManager(activity, 4)
			rvProducts.layoutManager = layoutManager
			rvProducts.itemAnimator = DefaultItemAnimator()

			val categoryListAdapter = MTCategoryHomeRecycleAdapter(requireContext(), object : NSProductCategoryCallback {
				override fun onResponse(categoryData: NSCategoryData) {
					switchActivity(MTProductsActivity::class.java, bundleOf(NSConstants.KEY_PRODUCT_CATEGORY to categoryData.categoryId, NSConstants.KEY_PRODUCT_CATEGORY_NAME to categoryData.categoryName))
				}
			})
			rvProducts.adapter = categoryListAdapter
			categoryListAdapter.clearData()
			categoryListAdapter.updateData(categoryResponse.categoryList)
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

			productCategoryModel.isCategoryDataAvailable.observe(viewLifecycleOwner) {
				setCategoryData(it)
			}

            isUserDataAvailable.observe(
                viewLifecycleOwner
            ) { userDetail ->
                setUserData(userDetail)
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
		if (NSConstants.RECHARGE_USING_PAYMENT_GATEWAY) {
			NSConstants.RECHARGE_USING_PAYMENT_GATEWAY = false
			EventBus.getDefault().post(NSTabChange(R.id.tb_wallets))
		} else {
			checkUpdateDialog(homeModel.chekVersionResponse)
		}
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
