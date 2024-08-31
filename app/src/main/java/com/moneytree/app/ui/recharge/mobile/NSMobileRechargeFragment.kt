package com.moneytree.app.ui.recharge.mobile

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.callbacks.NSRechargeRepeatCallback
import com.moneytree.app.common.utils.*
import com.moneytree.app.databinding.NsFragmentMobileRechargeBinding
import com.moneytree.app.repository.network.requests.NSRechargeSaveRequest
import com.moneytree.app.repository.network.responses.PlansResponse
import com.moneytree.app.repository.network.responses.RechargeListDataItem
import com.moneytree.app.repository.network.responses.ServiceProviderDataItem
import com.moneytree.app.ui.recharge.NSRechargeActivity
import com.moneytree.app.ui.recharge.NSRechargeViewModel
import com.moneytree.app.ui.recharge.RechargeDetailRecycleAdapter
import com.moneytree.app.ui.recharge.detail.NSRechargeDetailActivity
import com.moneytree.app.ui.recharge.history.NSRechargeHistoryActivity
import com.moneytree.app.ui.recharge.history.NSRechargeListRecycleAdapter
import com.moneytree.app.ui.recharge.plans.PlansFragment


class NSMobileRechargeFragment : NSFragment() {
	private val viewModel: NSRechargeViewModel by lazy {
		ViewModelProvider(this)[NSRechargeViewModel::class.java]
	}
    private var _binding: NsFragmentMobileRechargeBinding? = null
    private val rgBinding get() = _binding!!
	private var rechargeFetchListAdapter: RechargeDetailRecycleAdapter? = null
	private var rechargeRepeatData: String? = null
	private var rechargeListAdapter: NSRechargeListRecycleAdapter? = null

	companion object {
		fun newInstance(bundle: Bundle?) = NSMobileRechargeFragment().apply {
			arguments = bundle
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			with(viewModel) {
				rechargeSelectedType = it.getString(NSConstants.KEY_RECHARGE_TYPE)
				rechargeRepeatData = it.getString(NSConstants.KEY_RECHARGE_DETAIL)
			}
		}
	}

	override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentMobileRechargeBinding.inflate(inflater, container, false)
		rgBinding.llRechargeData.gone()
		viewCreated(true)
        setListener()
		getRechargeRepeatData()
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
							/*if (isApiCall) {
								setServiceProvider(
									getString(R.string.prepaid),
									isShowProgress = true
								)
							}*/
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
		setRechargeFetchAdapter()
		observeViewModel()
		selectedType()
		setRechargeHistoryAdapter()
    }

	private fun prepaidPostpaid(isPostpaid: Boolean) {
		rgBinding.apply {
			tvServiceProviderTitle.setVisibility(isPostpaid)
			cardWalletAmount.setVisibility(isPostpaid)
			cardAmount.setVisibility(isPostpaid)
			tvAmountTitle.setVisibility(isPostpaid)
			tvMobileNumberTitle.setVisibility(!isPostpaid)
			cardMobileNumber.setVisibility(!isPostpaid)
			etAmount.isEnabled = isPostpaid
			if (!isPostpaid) {
				etCustomerDetail.gone()
				tvCustomerDetail.visible()

				cardMobileNumber.setSafeOnClickListener {
					openPrepaidDialog()
				}

				cardPrepaidOperator.setSafeOnClickListener {
					openPrepaidDialog()
				}
			} else {
				etCustomerDetail.visible()
				tvCustomerDetail.gone()
			}
		}
	}

	private fun openPrepaidDialog() {
		rgBinding.apply {
			viewModel.apply {
				val planFragment = PlansFragment.newInstance(tvCustomerDetail.text.toString(), object : PlansFragment.DialogDismissListener {
					override fun onDismiss() {

					}

					override fun showProgress(isShowProgress: Boolean) {
						if (isShowProgress) {
							viewModel.showProgress()
						} else {
							viewModel.hideProgress()
						}
					}

					override fun onClickDetail(planResponse: PlansResponse, mobileNo: String) {
						dataItemModel = ServiceProviderDataItem(rechargeMasterId = planResponse.serviceProvider, rechargeType = rechargeType, accountDisplay = mobileNo, serviceProvider = planResponse.mobileOperator?.info?.opr)
						rgBinding.llPrepaidView.visible()
						tvServiceProviderPrepaid.text = planResponse.mobileOperator?.info?.opr
						tvAmountTitle.visible()
						cardAmount.visible()
						//Prepaid in display tvCustomerDetail other etCustomerDetail
						tvCustomerDetail.text = mobileNo
						etCustomerDetail.setText(mobileNo)
						etAmount.setText(planResponse.data?.selectedPack?.amount?:"0")
						etAmount.isEnabled = false
					}
				})
				planFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog)
				planFragment.show(childFragmentManager, "show_plans")
			}
		}
	}

	//RechargeMasterId = service_provider
	//AccountDisplay = phoneNumber

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
								/*setServiceProvider(
									getString(R.string.prepaid),
									isShowProgress = true
								)*/
								rechargeType = "Prepaid"
								prepaidPostpaid(false)
								getRechargeListData(true)
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
								rechargeType = "Postpaid"
								prepaidPostpaid(true)
								getRechargeListData(true)
							}
						}
					}

					etCustomerDetail.addTextChangedListener(object: TextWatcher {
						override fun beforeTextChanged(
							p0: CharSequence?,
							p1: Int,
							p2: Int,
							p3: Int
						) {

						}

						override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
							if (p0.toString().length >= 10) {
								if (spinner.selectedItemPosition != 0 || rechargeType.lowercase() == "prepaid") {
									dataItemModel?.apply {
										rechargeRequestFetchData =
											NSRechargeSaveRequest(
												rechargeType,
												rechargeMasterId,
												"",
												p0.toString(),
												"",
												"0",
												"",
												etAd1.text.toString(),
												etAd2.text.toString(),
												etAd3.text.toString()
											)
									}
									getRechargeFetchData()
								}
							} else {
								rvAccountDetail.gone()
								tvMessage.gone()
								tvMessageTitle.gone()
							}
						}

						override fun afterTextChanged(p0: Editable?) {

						}
					})

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
							} else if ((amount.toDouble() < (dataItemModel?.minAmt?:"0.0").toDouble() || amount.toDouble() > (dataItemModel?.maxAmt?:"0.0").toDouble()) && rechargeType?.lowercase() != "prepaid") {
								etAmount.error = activity.resources.getString(R.string.please_enter_amount_between, dataItemModel!!.minAmt, dataItemModel!!.maxAmt)
								return
							} else {
								val rechargeRequest = NSRechargeSaveRequest(rechargeType, serviceProvider, serviceProviderTitle, mobileNumber, accountDisplay, amount, note, ad1, ad2, ad3)
								dataResult.launch(Intent(activity, NSRechargeDetailActivity::class.java).putExtra(NSConstants.KEY_RECHARGE_VERIFY, Gson().toJson(rechargeRequest)))
								finish()
							}
						}
					})

					tvViewAll.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							selectedType()
							switchActivity(NSRechargeHistoryActivity::class.java, bundleOf(NSConstants.KEY_RECHARGE_TYPE to rechargeType))
						}
					})
				}
			}
		}
    }

	private fun selectedType() {
		with(rgBinding) {
			with(viewModel) {
				var selectedType: String? = "All"
				selectedType = if (rechargeSelectedType?.lowercase().equals("mobile")) {
					if (!rbPrepaid.isChecked && !rbPostPaid.isChecked) {
						"prepaid"
					} else if (rbPrepaid.isChecked) {
						"prepaid"
					} else {
						"postpaid"
					}
				} else {
					rechargeSelectedType!!
				}
				rechargeType = selectedType
			}
		}
	}

	private fun getRechargeRepeatData() {
		with(rgBinding) {
			with(viewModel) {
				if (rechargeRepeatData != null && rechargeRepeatData?.isNotEmpty() == true) {
					rechargeRepeat = Gson().fromJson(rechargeRepeatData, RechargeListDataItem::class.java)
					if (rechargeRepeat?.rechargeType?.lowercase().equals("prepaid") || rechargeRepeat?.rechargeType?.lowercase().equals("postpaid")) {
						if (rechargeRepeat?.rechargeType?.lowercase().equals("prepaid")) {
							rbPrepaid.isChecked = true
						} else {
							rbPostPaid.isChecked = true
							setServiceProvider(
								getString(R.string.postpaid),
								isShowProgress = true
							)
						}
					}

					/*etCustomerDetail.setText(rechargeRepeat!!.accountDisplay)
					etAmount.setText(rechargeRepeat!!.amount)
					etNote.setText(rechargeRepeat!!.note)
					etAd1.setText(rechargeRepeat!!.ad1)
					etAd2.setText(rechargeRepeat!!.ad2)
					etAd3.setText(rechargeRepeat!!.ad3)*/

				} else {
					if (rechargeSelectedType?.lowercase().equals("mobile")) {
						rbPrepaid.isChecked = true
					}
				}
			}
		}
	}

	private fun setServiceProvider(isProviderAvailable: Boolean) {
		with(rgBinding) {
			with(viewModel) {
				val adapter = ArrayAdapter(activity, R.layout.layout_spinner, serviceProvidersList)
				spinner.adapter = adapter
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
				spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
					override fun onItemSelected(
						p0: AdapterView<*>?, view: View?, position: Int, id: Long
					) {

						if (position != 0) {
							dataItemModel = serviceProviderDataList[position]
							setHintData(
								tvMobileNumberTitle,
								etCustomerDetail,
								dataItemModel!!.accountDisplay ?: ""
							)
							with(dataItemModel!!) {
								rechargeRequestFetchData =
									NSRechargeSaveRequest(rechargeType, rechargeMasterId, "", etCustomerDetail.text.toString(), "", "0", "", tvAd1.text.toString(), tvAd2.text.toString(), tvAd3.text.toString())
							}
						}

						if (dataItemModel != null) {
							with(dataItemModel!!) {
								if (position != 0) {
									if (accountDisplay != null && accountDisplay != "NA" && accountDisplay.isNotEmpty()) {
										tvMobileNumberTitle.setVisibility(position != 0)
										cardMobileNumber.setVisibility(position != 0)
									}
									if (ad1 != null && ad1 != "NA" && ad1.isNotEmpty()) {
										ivAd1.text = ad1.first().uppercase()
										tvAd1.visible()
										cardAd1.visible()
										setHintData(tvAd1, etAd1, ad1)
									} else {
										tvAd1.gone()
										cardAd1.gone()
									}
									if (ad2 != null && ad2 != "NA" && ad2.isNotEmpty()) {
										ivAd2.text = ad2.first().uppercase()
										tvAd2.visible()
										cardAd2.visible()
										setHintData(tvAd2, etAd2, ad2)
									} else {
										tvAd2.gone()
										cardAd2.gone()
									}
									if (ad3 != null && ad3 != "NA" && ad3.isNotEmpty()) {
										ivAd3.text = ad3.first().uppercase()
										tvAd3.visible()
										cardAd3.visible()
										setHintData(tvAd3, etAd3, ad3)
									} else {
										tvAd3.gone()
										cardAd3.gone()
									}
								} else {
									tvMobileNumberTitle.setVisibility(false)
									cardMobileNumber.setVisibility(false)
								}
							}

							if (rechargeRepeat != null) {
								etCustomerDetail.setText(rechargeRepeat!!.accountDisplay)
								etAmount.setText(rechargeRepeat!!.amount)
								etNote.setText(rechargeRepeat!!.note)
								etAd1.setText(rechargeRepeat!!.ad1)
								etAd2.setText(rechargeRepeat!!.ad2)
								etAd3.setText(rechargeRepeat!!.ad3)
								rechargeRepeat = null
							}
						}
					}

					override fun onNothingSelected(p0: AdapterView<*>?) {
					}
				}

				if (rechargeRepeat != null) {
					serviceProviderDataList.forEachIndexed { index, data ->
						run {
							if (data.rechargeMasterId.equals(rechargeRepeat!!.serviceProvider)) {
								spinner.setSelection(index)
							}
						}
					}
				}
			}
		}
	}

	/**
	 * To add data of member tree in list
	 */
	private fun setRechargeFetchAdapter() {
		with(rgBinding) {
			rvAccountDetail.layoutManager = LinearLayoutManager(activity)
			rechargeFetchListAdapter = RechargeDetailRecycleAdapter()
			rvAccountDetail.adapter = rechargeFetchListAdapter
		}
	}

	/**
	 * Set member tree data
	 *
	 * @param isMemberTree when data available it's true
	 */
	private fun setRechargeFetchData(isMemberTree: Boolean) {
		with(rgBinding) {
			with(viewModel) {
				Log.d("DAdaAvailable", "onSuccess: ${isMemberTree}- ${rechargeFetchDataList.size} ${rechargeFetchResponse?.message}")
				rvAccountDetail.setVisibility(isMemberTree && rechargeFetchDataList.isValidList())
				tvMessage.setVisibility(!isMemberTree)
				tvMessageTitle.setVisibility(rvAccountDetail.isVisible || tvMessage.isVisible)
				if (isMemberTree) {
					rechargeFetchListAdapter!!.clearData()
					rechargeFetchListAdapter!!.updateData(rechargeFetchDataList)
				} else {
					tvMessage.text = rechargeFetchResponse?.message
					if (rechargeFetchResponse == null) {
						tvMessage.gone()
						tvMessageTitle.gone()
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

				isRechargeFetchDataAvailable.observe(
					viewLifecycleOwner
				) { isProvider ->
					setRechargeFetchData(isProvider)
				}

				isRechargeDataAvailable.observe(
					viewLifecycleOwner
				) { isNotification ->
					setRechargeHistoryData(isNotification)
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

	//Recharge History
	/**
	 * To add data of register in list
	 */
	private fun setRechargeHistoryAdapter() {
		with(rgBinding) {
			with(viewModel) {
				rvRechargeList.layoutManager = LinearLayoutManager(activity)
				rechargeListAdapter = NSRechargeListRecycleAdapter(activity, true, object:
					NSRechargeRepeatCallback {
					override fun onClick(rechargeData: RechargeListDataItem) {
						switchActivity(
							NSRechargeActivity::class.java,
							bundle = bundleOf(NSConstants.KEY_RECHARGE_TYPE to rechargeData.rechargeType, NSConstants.KEY_RECHARGE_DETAIL to Gson().toJson(rechargeData)), flags = intArrayOf(
								Intent.FLAG_ACTIVITY_CLEAR_TOP)
						)
					}
				}, object : NSPageChangeCallback {
					override fun onPageChange(pageNo: Int) {

					}
				})

				rvRechargeList.adapter = rechargeListAdapter
				getRechargeListData(true)
			}
		}
	}

	/**
	 * Set register data
	 *
	 * @param isRecharge when data available it's true
	 */
	private fun setRechargeHistoryData(isRecharge: Boolean) {
		with(viewModel) {
			rechargeDataManage(isRecharge)
			if (isRecharge) {
				rechargeListAdapter!!.clearData()
				rechargeListAdapter!!.updateData(rechargeList)
			} else {
				rechargeList.clear()
				rechargeListAdapter!!.clearData()
			}
		}
	}

	/**
	 * Register data manage
	 *
	 * @param isRegisterVisible when register available it's visible
	 */
	private fun rechargeDataManage(isRegisterVisible: Boolean) {
		with(rgBinding) {
			tvViewAll.setVisibility(isRegisterVisible)
			rvRechargeList.visibility = if (isRegisterVisible) View.VISIBLE else View.GONE
			tvRechargeNotFoundSub.visibility = if (isRegisterVisible) View.GONE else View.VISIBLE
		}
	}
}
