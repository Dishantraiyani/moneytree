package com.moneytree.app.ui.coins.transfer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.moneytree.app.databinding.NsFragmentTransferBinding
import com.moneytree.app.repository.network.requests.NSWalletTransferModel
import com.moneytree.app.ui.coins.verifyMember.VerifyMemberActivity
import com.moneytree.app.ui.wallets.transfer.NSTransferFragment
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NSTransferFragment : NSFragment() {
	private val transferModel: NSTransferModel by lazy {
		ViewModelProvider(this).get(NSTransferModel::class.java)
	}
	private var _binding: NsFragmentTransferBinding? = null
	private val adBinding get() = _binding!!
	private var isTransferFromVoucher: Boolean = false
	private var packageId: String? = null
	private var voucherQty: Int = 0
	private var availableAmount: String = "0"

	companion object {
		fun newInstance(bundle: Bundle?) = NSTransferFragment().apply {
			arguments = bundle
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			isTransferFromVoucher = it.getBoolean(NSConstants.KEY_IS_VOUCHER_FROM_TRANSFER)
			if (isTransferFromVoucher) {
				packageId = it.getString(NSConstants.KEY_IS_PACKAGE_ID)
				voucherQty = it.getInt(NSConstants.KEY_IS_VOUCHER_QUANTITY)
			} else {
				availableAmount = it.getString(NSConstants.KEY_AVAILABLE_BALANCE)?:"0.0"
			}
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = NsFragmentTransferBinding.inflate(inflater, container, false)
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
			HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(if (isTransferFromVoucher) R.string.voucher_transfer else R.string.wallet_transfer))
			if (isTransferFromVoucher) {
				tvAmountTitle.text = activity.resources.getString(R.string.voucher_qty)
				etAmount.hint = activity.resources.getString(R.string.enter_voucher_qty)
				tvRemark.gone()
				tvPassword.gone()
				clRemark.gone()
				btnSearch.visible()
				tvMemberName.visible()
				cardMember.visible()
				clTransactionPassword.gone()
				tvPackageNameTitle.visible()
				cardPackageName.visible()
				transferModel.getPackageListData(true)
			} else {
				tvAvailableAmount.text = availableAmount
				tvAvailableBalanceTitle.visible()
				cardAvailableAmount.visible()
			}
		}
	}

	/**
	 * Set listener
	 */
	private fun setListener() {
		with(adBinding) {
			with(layoutHeader) {

				btnSearch.setOnClickListener {
					val id = etTransactionId.text.toString()
					if (id.isNotEmpty()) {
						transferModel.getMemberDetail(id, true)
					} else {
						etTransactionId.error = activity.resources.getString(R.string.please_enter_transation_id)
					}
				}

				btnSubmit.setOnClickListener(object : OnSingleClickListener() {
					override fun onSingleClick(v: View?) {
						val transactionId = etTransactionId.text.toString()
						val password = etTransactionPassword.text.toString()
						val remark = etRemark.text.toString()
						val amount = etAmount.text.toString()
						val memberName = tvMember.text.toString()

						if (isTransferFromVoucher && memberName.isEmpty()) {
							Toast.makeText(
								activity,
								activity.resources.getString(R.string.member_name_not_empty),
								Toast.LENGTH_SHORT
							).show()
							return
						}

						if (transactionId.isEmpty()) {
							etTransactionId.error =
								activity.resources.getString(R.string.please_enter_transation_id)
							return
						} else if (isTransferFromVoucher && spinnerPackage.selectedItemPosition == 0) {
							Toast.makeText(
								activity,
								activity.resources.getString(R.string.please_select_package_name),
								Toast.LENGTH_SHORT
							).show()
							return
						} else if (amount.isEmpty()) {
							if (isTransferFromVoucher) {
								etAmount.error =
									activity.resources.getString(R.string.please_enter_voucher_qty)
							} else {
								etAmount.error =
									activity.resources.getString(R.string.please_enter_amount)
							}
							return
						} else if (password.isEmpty() && !isTransferFromVoucher) {
							etTransactionPassword.error =
								activity.resources.getString(R.string.please_enter_transaction_password)
							return
						} else {
							if (amount.toDouble() > 0) {
								if (isTransferFromVoucher && amount.toDouble() > voucherQty) {
									Toast.makeText(
										activity,
										activity.resources.getString(R.string.insufficient_voucher),
										Toast.LENGTH_SHORT
									).show()
									return
								}



								val model = NSWalletTransferModel(
									transactionId,
									password,
									remark,
									amount,
									isTransferFromVoucher,
									packageId,
									amount.toInt()
								)
								switchResultActivity(
									dataResult, VerifyMemberActivity::class.java, bundleOf(
										NSConstants.KEY_WALLET_VERIFY to Gson().toJson(model)
									)
								)
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
	 * Set register data
	 *
	 * @param isPackage when data available it's true
	 */
	private fun setPackageData(isPackage: Boolean) {
		with(transferModel) {
			if (isPackage) {
				val aa = ArrayAdapter(activity, R.layout.layout_spinner_item, strPackageList)
				aa.setDropDownViewResource(R.layout.layout_spinner_item)
				adBinding.spinnerPackage.adapter = aa
				adBinding.spinnerPackage.onItemSelectedListener = object :
					AdapterView.OnItemSelectedListener {
					override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
						if (p2 == 0) {
							packageId = ""
							adBinding.tvQuantity.gone()
							adBinding.tvQuantity.text =
								addText(activity, R.string.voucher_available, "0")
						} else {
							packageId = packageList[p2 - 1].packageId!!
							adBinding.tvQuantity.visible()
							getQuantity(packageId!!, true)
						}
					}

					override fun onNothingSelected(p0: AdapterView<*>?) {

					}
				}
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

				isPackageDataAvailable.observe(
					viewLifecycleOwner
				) { isNotification ->
					setPackageData(isNotification)
				}

				isMemberDataAvailable.observe(
					viewLifecycleOwner
				) { isMember ->
					if (isMember) {
						if (memberDetailModel != null) {
							tvMember.text = memberDetailModel?.data?.fullname
						} else {
							tvMember.text = ""
						}
					} else {
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
					adBinding.tvQuantity.text =
						addText(activity, R.string.voucher_available, voucherQuantity!!)
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
