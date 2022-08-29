package com.moneytree.app.ui.wallets.verifyMember

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.switchResultActivity
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.FragmentVerifyMemberBinding
import com.moneytree.app.repository.network.requests.NSWalletTransferModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class VerifyMemberFragment : NSFragment() {
	private val verifyModel: NSVerifyWalletModel by lazy {
		ViewModelProvider(this).get(NSVerifyWalletModel::class.java)
	}
	private var _binding: FragmentVerifyMemberBinding? = null
	private val adBinding get() = _binding!!

	companion object {
		fun newInstance(bundle: Bundle?) = VerifyMemberFragment().apply {
			arguments = bundle
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			with(verifyModel) {
				strVerifyData = it.getString(NSConstants.KEY_WALLET_VERIFY)
				getDetail()
			}
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentVerifyMemberBinding.inflate(inflater, container, false)
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
			with(layoutHeader) {
				clBack.visibility = View.VISIBLE
				tvHeaderBack.text = activity.resources.getString(R.string.member_detail)
				ivBack.visibility = View.VISIBLE
			}
		}
	}

	/**
	 * Set listener
	 */
	private fun setListener() {
		with(adBinding) {
			with(layoutHeader) {
				clBack.setOnClickListener {
					onBackPress()
				}

				btnSubmit.setOnClickListener (
					object : OnSingleClickListener() {
						override fun onSingleClick(v: View?) {
							with(verifyModel) {
								if (memberDetailModel != null && verifyTransferModel != null) {
									val transactionId = verifyTransferModel!!.transactionId
									val password = verifyTransferModel!!.password
									val remark = verifyTransferModel!!.remark
									val amount = verifyTransferModel!!.amount
									if (verifyTransferModel!!.isVoucherAvailable == true) {
										verifyModel.transferVoucherTransfer(
											transactionId!!,
											verifyTransferModel!!.voucherQty!!.toString(),
											true
										)
									} else {
										verifyModel.transferWalletAmount(
											transactionId!!,
											amount!!,
											remark!!,
											password!!,
											true
										)
									}
								} else {
									showSuccessDialog(
										activity.resources.getString(R.string.app_name),
										activity.resources.getString(R.string.please_try_again),
										NSConstants.REDEEM_SAVE_CLICK
									)
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
		with(verifyModel) {
			with(adBinding) {
				isProgressShowing.observe(
					viewLifecycleOwner
				) { shouldShowProgress ->
					updateProgress(shouldShowProgress)
				}

				isWalletTransferDataAvailable.observe(
					viewLifecycleOwner
				) { isRedeem ->
					if (isRedeem) {
						if (successResponse?.status == true) {
							showSuccessDialog(
								activity.resources.getString(R.string.app_name),
								successResponse?.message,
								NSConstants.REDEEM_SAVE_CLICK
							)
						}
					}
				}

				isMemberDetailGet.observe(viewLifecycleOwner) {
					if (it) {
						if (memberDetailModel != null) {
							tvEmail.text =
								memberDetailModel!!.email!!.ifEmpty { activity.resources.getString(R.string.not_available) }
							tvName.text = memberDetailModel!!.fullname!!.ifEmpty {
								activity.resources.getString(R.string.not_available)
							}
							tvMobile.text = memberDetailModel!!.mobile!!.ifEmpty {
								activity.resources.getString(R.string.not_available)
							}
						}
					}
				}

				isWalletDataAvailable.observe(
					viewLifecycleOwner
				) { isRedeem ->
					if (isRedeem) {
						if (verifyTransferModel != null) {
							if (verifyTransferModel!!.isVoucherAvailable == true) {
								tvAmountTitle.text =
									activity.resources.getString(R.string.voucher_qty)
								llRemark.gone()
								tvAmount.text = verifyTransferModel!!.amount
							} else {
								llRemark.visible()
								tvAmount.text = verifyTransferModel!!.amount?.let {
									addText(
										requireActivity(),
										R.string.balance,
										it
									)
								}
							}

							tvRemark.text = verifyTransferModel!!.remark!!.ifEmpty {
								activity.resources.getString(R.string.not_available)
							}
							tvTransactionId.text = verifyTransferModel!!.transactionId!!.ifEmpty {
								activity.resources.getString(R.string.not_available)
							}
							verifyModel.getMemberDetail(verifyTransferModel!!.transactionId!!, true)
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
	fun onPositiveButtonClickEvent(event: NSAlertButtonClickEvent) {
		if (event.buttonType == NSConstants.KEY_ALERT_BUTTON_POSITIVE && event.alertKey == NSConstants.REDEEM_SAVE_CLICK) {
			val intent = Intent()
			activity.setResult(NSRequestCodes.REQUEST_WALLET_UPDATE_TRANSFER, intent)
			finish()
		}
	}
}
