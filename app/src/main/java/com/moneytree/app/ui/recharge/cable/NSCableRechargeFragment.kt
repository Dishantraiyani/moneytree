package com.moneytree.app.ui.recharge.cable

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.utils.*
import com.moneytree.app.databinding.*
import com.moneytree.app.repository.network.requests.NSRechargeSaveRequest
import com.moneytree.app.ui.recharge.NSRechargeViewModel
import com.moneytree.app.ui.recharge.detail.NSRechargeDetailActivity
import com.moneytree.app.ui.recharge.detail.NSRechargeDetailFragment
import com.moneytree.app.ui.wallets.redeemForm.NSRedeemSaveViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NSCableRechargeFragment : NSFragment() {
	private val viewModel: NSRechargeViewModel by lazy {
		ViewModelProvider(this)[NSRechargeViewModel::class.java]
	}
    private var _binding: NsFragmentCableRechargeBinding? = null
    private val rgBinding get() = _binding!!

	companion object {
		fun newInstance(bundle: Bundle?) = NSCableRechargeFragment().apply {
			arguments = bundle
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			with(viewModel) {
				rechargeSelectedType = it.getString(NSConstants.KEY_RECHARGE_TYPE)
			}
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onTabSelectEvent(event: NSRechargeAllEventTab) {
		Log.d(TAG, "onTabSelectEvent: $event")
		viewModel.rechargeSelectedType = event.rechargeType
		if (event.rechargeType == activity.resources.getString(R.string.recharge_dth)) {
			viewCreated(true)
			setListener()
		}
	}


	override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentCableRechargeBinding.inflate(inflater, container, false)
        rgBinding.llRechargeData.gone()
		viewCreated(false)
        //setListener()
        return rgBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated(isApiCall: Boolean) {
		with(viewModel) {
			with(rgBinding) {
				serviceProvidersList.clear()
				setServiceProvider(false)
				with(activity.resources) {
					when (rechargeSelectedType) {
						getString(R.string.mobile) -> {
							rdoSimType.visible()
							if (isApiCall) {
								setServiceProvider(
									getString(R.string.prepaid),
									isShowProgress = true
								)
							}
							llRechargeData.visible()
						}
						else -> {
							rdoSimType.gone()
							if (isApiCall) {
								setServiceProvider(
									rechargeSelectedType!!,
									isShowProgress = true
								)
							}
							llRechargeData.visible()
						}
					}
				}
			}
		}
		observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
		with(rgBinding) {
			with(viewModel) {
				with(activity.resources) {
					rbPrepaid.setOnCheckedChangeListener { p0, isChecked ->
						run {
							if (isChecked) {
								setServiceProvider(
									getString(R.string.prepaid),
									isShowProgress = true
								)
							}
						}
					}

					rbPostPaid.setOnCheckedChangeListener { p0, isChecked ->
						run {
							if (isChecked) {
								setServiceProvider(
									getString(R.string.postpaid),
									isShowProgress = true
								)
							}
						}
					}

					btnSubmit.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							val amount = etAmount.text.toString()
							val mobileNumber = etCustomerDetail.text.toString()
							val note = etNote.text.toString()
							val ad1 = etAd1.text.toString()
							val ad2 = etAd2.text.toString()
							val ad3 = etAd3.text.toString()
							val serviceProvider = dataItemModel!!.rechargeMasterId
							val rechargeType = dataItemModel!!.rechargeType
							val accountDisplay = dataItemModel!!.accountDisplay
							val serviceProviderTitle = dataItemModel!!.serviceProvider

							if (etCustomerDetail.text.toString().isEmpty()) {
								etCustomerDetail.error = activity.resources.getString(R.string.please_enter_mobile, dataItemModel?.accountDisplay)
								return
							} else if (etCustomerDetail.text.toString().length < 10) {
								etCustomerDetail.error = activity.resources.getString(R.string.please_enter_valid_mobile, dataItemModel?.accountDisplay)
								return
							} else if (etAmount.text.toString().isEmpty()) {
								etAmount.error = activity.resources.getString(R.string.please_enter_amount)
								return
							} else if (amount.toDouble() < dataItemModel?.minAmt!!.toDouble() || amount.toDouble() > dataItemModel?.maxAmt!!.toDouble()) {
								etAmount.error = activity.resources.getString(R.string.please_enter_amount_between, dataItemModel!!.minAmt, dataItemModel!!.maxAmt)
								return
							} else {
								val rechargeRequest = NSRechargeSaveRequest(rechargeType, serviceProvider, serviceProviderTitle, mobileNumber, accountDisplay, amount, note, ad1, ad2, ad3)
								dataResult.launch(Intent(activity, NSRechargeDetailActivity::class.java).putExtra(NSConstants.KEY_RECHARGE_VERIFY, Gson().toJson(rechargeRequest)))
								finish()
							}
						}
					})
				}
			}
		}
    }

	private fun setServiceProvider(isProviderAvailable: Boolean) {
		with(rgBinding) {
			with(viewModel) {
				tvMobileNumberTitle.setVisibility(isProviderAvailable)
				cardMobileNumber.setVisibility(isProviderAvailable)
				if (serviceProvidersList.isValidList()) {
					setHintData(tvMobileNumberTitle, etCustomerDetail, serviceProviderResponse?.data?.get(0)?.accountDisplay ?: "")
				}
				val adapter = ArrayAdapter(activity, R.layout.layout_spinner, serviceProvidersList)
				spinner.adapter = adapter
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
				spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
					override fun onItemSelected(
						p0: AdapterView<*>?, view: View?, position: Int, id: Long
					) {
						dataItemModel = serviceProviderDataList[position]
						if (dataItemModel != null) {
							with(dataItemModel!!) {
								if (ad1 != null && ad1 != "NA" && ad1.isNotEmpty()) {
									setHintData(tvAd1, etAd1, ad1 ?: "")
								}
								if (ad2 != null && ad2 != "NA" && ad2.isNotEmpty()) {
									setHintData(tvAd2, etAd2, ad2 ?: "")
								}
								if (ad3 != null && ad3 != "NA" && ad3.isNotEmpty()) {
									setHintData(tvAd3, etAd3, ad3 ?: "")
								}
							}
						}
					}

					override fun onNothingSelected(p0: AdapterView<*>?) {
					}
				}
			}
		}
	}

	private fun setHintData(tvText: TextView, edtText: EditText, title: String) {
		tvText.text = title
		val hint = "Enter $title"
		edtText.hint = hint
	}

	/**
	 * To observe the view model for data changes
	 */
	private fun observeViewModel() {
		with(viewModel) {
			with(rgBinding) {
				isProgressShowing.observe(
					viewLifecycleOwner
				) { shouldShowProgress ->
					updateProgress(shouldShowProgress)
				}

				isServiceProviderDataAvailable.observe(
					viewLifecycleOwner
				) { isProvider ->
					setServiceProvider(isProvider)
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
}
