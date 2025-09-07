package com.moneytree.app.ui.vouchers.topupact

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.moneytree.app.R
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSActivityEvent
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.NSRequestCodes
import com.moneytree.app.common.OnSingleClickListener
import com.moneytree.app.common.callbacks.NSDialogClickCallback
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.addTextChangeListener
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.setPlaceholderAdapter
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.NsFragmentTopupVoucherActivationBinding
import com.moneytree.app.repository.network.requests.VoucherActiveSaveModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TopUpVoucherActivationFragment : NSFragment() {
	private val transferModel: TopUpVoucherActivationModel by lazy {
		ViewModelProvider(this)[TopUpVoucherActivationModel::class.java]
	}
	private var _binding: NsFragmentTopupVoucherActivationBinding? = null
	private val adBinding get() = _binding!!
	
	companion object {
		fun newInstance(bundle: Bundle?) = TopUpVoucherActivationFragment().apply {
			arguments = bundle
		}
	}
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = NsFragmentTopupVoucherActivationBinding.inflate(inflater, container, false)
		viewCreated()
		setListener()
		observeViewModel()
		return adBinding.root
	}
	
	/**
	 * View created
	 */
	private fun viewCreated() {
		with(adBinding) {
			HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.top_voucher_activation))
			btnSearch.gone()
		}
	}
	
	/**
	 * Set listener
	 */
	private fun setListener() {
		with(adBinding) {
			with(layoutHeader) {
				
				/**
				 * Select Options
				 * */
				val selectOptionStr = resources.getString(R.string.select_option)
				var selectedOption: String? = ""
				spinnerOptions.setPlaceholderAdapter(resources.getStringArray(R.array.select_option), requireContext(), isHideFirstPosition = true, selectOptionStr) {
					selectedOption = if (it.equals(selectOptionStr)) {
						""
					} else {
						it
					}
				}
				spinnerOptions.prompt = selectOptionStr
				
				/** ----------------------------------------------------------------------------------------*/
				
				/**
				 * Select Voucher
				 * */
				
				val selectVoucherStr = resources.getString(R.string.select_voucher)
				var selectedVoucherCode: String? = ""
				var selectedVoucherId: String? = ""
				spinnerVoucherList.prompt = selectVoucherStr
				transferModel.getVoucherList(true) { list ->
					val voucherCodeList = list.mapNotNull { it.voucherCode }
					val newList: MutableList<String> = arrayListOf()
					newList.addAll(voucherCodeList)
					newList.add(0, selectVoucherStr)
					
					spinnerVoucherList.setPlaceholderAdapter(newList.toTypedArray(), requireContext(), isHideFirstPosition = true, selectVoucherStr) {
						selectedVoucherCode = if (it.equals(selectVoucherStr)) {
							""
						} else {
							it
						}
						selectedVoucherId = list.find { it.voucherCode == selectedVoucherCode }?.voucherId
					}
					spinnerVoucherList.prompt = selectVoucherStr
				}
				
				
				
				
				/** ----------------------------------------------------------------------------------------*/
				
				
				
				
				btnSearch.setOnClickListener {
					val id = etDspId.text.toString()
					if (id.isNotEmpty()) {
						transferModel.checkDspSponsor("DSP ", id) { isSuccess, modelData ->
							if (isSuccess) {
								transferModel.selectedDspId = etDspId.text.toString()
							} else {
								transferModel.selectedDspId = ""
								showAlertDialog(modelData.message)
							}
						}
					} else {
						etDspId.error = activity.resources.getString(R.string.please_enter_transation_id)
					}
				}
				
				etDspId.addTextChangeListener {
					if (it.length >= 10) {
						transferModel.checkDspSponsor("DSP ", it) { isSuccess, modelData ->
							if (isSuccess) {
								transferModel.selectedDspId = etDspId.text.toString()
							} else {
								transferModel.selectedDspId = ""
								showAlertDialog(modelData.message)
							}
						}
					}
				}
				
				etSponsorId.addTextChangeListener {
					if (it.length >= 10) {
						transferModel.checkDspSponsor("SPONSOR ", it) { isSuccess, modelData ->
							if (isSuccess) {
								transferModel.selectedSponsorId = etSponsorId.text.toString()
							} else {
								transferModel.selectedSponsorId = ""
								showAlertDialog(modelData.message)
							}
						}
					}
				}
				
				btnSubmit.setOnClickListener(object : OnSingleClickListener() {
					override fun onSingleClick(v: View?) {
						
						
						if (transferModel.selectedDspId.isNullOrEmpty()) {
							etDspId.error = activity.resources.getString(R.string.please_enter_dsp_id)
							return
						} else if (transferModel.selectedSponsorId.isNullOrEmpty()) {
							etSponsorId.error = activity.resources.getString(R.string.please_enter_sponsor_id)
							return
						} else if (selectedOption.isNullOrEmpty()) {
							Toast.makeText(activity, activity.resources.getString(R.string.please_select_option), Toast.LENGTH_SHORT).show()
							return
						} else if (selectedVoucherId.isNullOrEmpty()) {
							Toast.makeText(activity, activity.resources.getString(R.string.please_select_voucher), Toast.LENGTH_SHORT).show()
							return
						} else {
							fun saveActiveVoucher() {
								val model = VoucherActiveSaveModel(
									transferModel.selectedDspId, transferModel.selectedSponsorId,
									selectedOption,
									selectedVoucherId
								)
								
								transferModel.saveVoucherTransfer(model, true) {
									requireActivity().setResult(RESULT_OK)
									finish()
								}
							}
							
							transferModel.getUserDetail {
								if (it) {
									if (NSUtilities.checkKycVerified()) {
										saveActiveVoucher()
										return@getUserDetail
									}
									showCommonDialog("Kyc Verification", activity.resources.getString(R.string.your_kyc_verification_ask), "Yes", "No", callback = object :
										NSDialogClickCallback {
										override fun onClick(isOk: Boolean) {
											if (isOk) {
												NSUtilities.isKycVerified(activity, false)
											} else {
												saveActiveVoucher()
											}
										}
									})
								} else {
									showCommonDialog("Kyc Verification", activity.resources.getString(R.string.your_kyc_verification_ask), "Yes", "No", callback = object :
										NSDialogClickCallback {
										override fun onClick(isOk: Boolean) {
											if (isOk) {
												NSUtilities.isKycVerified(activity, false)
											} else {
												saveActiveVoucher()
											}
										}
									})
								}
							}
						}
					}
					
				})
			}
		}
	}
	
	/**
	 * To observe the view model for data changes
	 */
	private fun observeViewModel() {
		with(transferModel) {
			with(adBinding) {
				isProgressShowing.observe(
					viewLifecycleOwner
				) { shouldShowProgress ->
					updateProgress(shouldShowProgress)
				}
				
				isMemberDataAvailable.observe(
					viewLifecycleOwner
				) { isMember ->
					if (isMember) {
						if (memberDetailModel != null) {
							memberTransferId = etDspId.text.toString()
							tvMemberName.visible()
							cardMember.visible()
							tvMember.text = memberDetailModel?.data?.fullname
						} else {
							tvMemberName.gone()
							cardMember.gone()
							tvMember.text = ""
						}
					} else {
						tvMemberName.gone()
						cardMember.gone()
						tvMember.text = ""
						if (memberDetailModel != null) {
							showAlertDialog(memberDetailModel?.message)
						}
					}
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
	fun onResultEvent(event: NSActivityEvent) {
		if (event.resultCode == NSRequestCodes.REQUEST_WALLET_UPDATE_TRANSFER) {
			val intent = Intent()
			activity.setResult(NSRequestCodes.REQUEST_WALLET_UPDATE_TRANSFER, intent)
			finish()
		}
	}
}
