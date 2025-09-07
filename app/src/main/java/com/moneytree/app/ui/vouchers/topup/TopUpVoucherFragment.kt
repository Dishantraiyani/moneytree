package com.moneytree.app.ui.vouchers.topup

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
import com.moneytree.app.databinding.NsFragmentTopupVoucherBinding
import com.moneytree.app.repository.network.requests.VoucherTransferModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TopUpVoucherFragment : NSFragment() {
	private val transferModel: TopUpVoucherModel by lazy {
		ViewModelProvider(this)[TopUpVoucherModel::class.java]
	}
	private var _binding: NsFragmentTopupVoucherBinding? = null
	private val adBinding get() = _binding!!
	private var isTransferFromVoucher: Boolean = false
	private var packageId: String? = null
	private var voucherQty: Int = 0
	
	companion object {
		fun newInstance(bundle: Bundle?) = TopUpVoucherFragment().apply {
			arguments = bundle
		}
	}
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = NsFragmentTopupVoucherBinding.inflate(inflater, container, false)
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
			HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.top_vouchers))
			btnSearch.visible()
			transferModel.getQuantity(true)
		}
	}
	
	/**
	 * Set listener
	 */
	private fun setListener() {
		with(adBinding) {
			with(layoutHeader) {
				
				var selectedPaymentType: String? = ""
				spinnerPayment.setPlaceholderAdapter(
					resources.getStringArray(R.array.select_payment_type),
					requireContext(), isHideFirstPosition = true, "Select Payment Type"
				) {
					selectedPaymentType = if (it.equals("Select Payment Type")) {
						""
					} else {
						it
					}
				}
				spinnerTransferFrom.prompt = "Select Payment Type"
				
				btnSearch.setOnClickListener {
					val id = etTransactionId.text.toString()
					if (id.isNotEmpty()) {
						transferModel.getMemberDetail(id, true)
					} else {
						etTransactionId.error = activity.resources.getString(R.string.please_enter_transation_id)
					}
				}
				
				etTransactionId.addTextChangeListener {
					if ((transferModel.memberTransferId?:"").length >= 9) {
						tvMemberName.gone()
						cardMember.gone()
					}
					if (transferModel.memberTransferId != it && !transferModel.memberTransferId.isNullOrEmpty()) {
						transferModel.memberTransferId = ""
					}
				}
				
				btnSubmit.setOnClickListener(object : OnSingleClickListener() {
					override fun onSingleClick(v: View?) {
						val transferId = etTransactionId.text.toString()
						val voucherQty = etVoucherQty.text.toString()
						
						transferModel.memberTransferId
						
						if (transferId.isEmpty()) {
							etTransactionId.error = activity.resources.getString(R.string.please_enter_transfer_id)
							return
						} else if (selectedPaymentType.isNullOrEmpty()) {
							Toast.makeText(activity, activity.resources.getString(R.string.please_select_payment_type), Toast.LENGTH_SHORT).show()
							return
						} else if (voucherQty.isEmpty()) {
							etVoucherQty.error = activity.resources.getString(R.string.please_enter_voucher_qty)
							return
						} else {
							if (voucherQty.toDouble() > 0) {
								if (voucherQty.toDouble() > this@TopUpVoucherFragment.voucherQty) {
									Toast.makeText(activity, activity.resources.getString(R.string.insufficient_voucher), Toast.LENGTH_SHORT).show()
									return
								}
								
								fun voucherTransfer() {
									val model = VoucherTransferModel(
										transferId,
										voucherQty,
										selectedPaymentType
									)
									
									transferModel.saveVoucherTransfer(model, true) {
										requireActivity().setResult(RESULT_OK)
										finish()
									}
									
									/*switchResultActivity(
										dataResult, VerifyMemberActivity::class.java, bundleOf(
											NSConstants.KEY_WALLET_VERIFY to Gson().toJson(model)
										)
									)*/
								}
								
								transferModel.getUserDetail {
									if (it) {
										if (NSUtilities.checkKycVerified()) {
											voucherTransfer()
											return@getUserDetail
										}
										showCommonDialog("Kyc Verification", activity.resources.getString(R.string.your_kyc_verification_ask), "Yes", "No", callback = object :
											NSDialogClickCallback {
											override fun onClick(isOk: Boolean) {
												if (isOk) {
													NSUtilities.isKycVerified(activity, false)
												} else {
													voucherTransfer()
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
													voucherTransfer()
												}
											}
										})
									}
								}
							} else {
								Toast.makeText(
									activity,
									activity.resources.getString(R.string.please_enter_valid_amount),
									Toast.LENGTH_SHORT
								).show()
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
							memberTransferId = etTransactionId.text.toString()
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
				
				isVoucherDataAvailable.observe(
					viewLifecycleOwner
				) {
					voucherQty = if (voucherQuantity == null) 0 else voucherQuantity!!.toInt()
					adBinding.etAvailableQuantity.text = voucherQuantity
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
