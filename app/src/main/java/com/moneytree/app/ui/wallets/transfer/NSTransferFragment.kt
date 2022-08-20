package com.moneytree.app.ui.wallets.transfer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.switchResultActivity
import com.moneytree.app.databinding.NsFragmentTransferBinding
import com.moneytree.app.repository.network.requests.NSWalletTransferModel
import com.moneytree.app.ui.packageVoucher.packageDetail.NSPackageDetailFragment
import com.moneytree.app.ui.wallets.verifyMember.VerifyMemberActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NSTransferFragment : NSFragment() {
    private var _binding: NsFragmentTransferBinding? = null
    private val adBinding get() = _binding!!
	private var isTransferFromVoucher: Boolean = false
	private var packageId: String? = null
	private var voucherQty: Int = 0

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
        return adBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(adBinding) {
            with(layoutHeader) {
                clBack.visibility = View.VISIBLE
                ivBack.visibility = View.VISIBLE
				if (isTransferFromVoucher) {
					tvAmountTitle.text = activity.resources.getString(R.string.voucher_qty)
					etAmount.hint = activity.resources.getString(R.string.enter_voucher_qty)
					tvRemark.gone()
					tvPassword.gone()
					clRemark.gone()
					clTransactionPassword.gone()
					tvHeaderBack.text = activity.resources.getString(R.string.voucher_transfer)
				} else {
					tvHeaderBack.text = activity.resources.getString(R.string.wallet_transfer)
				}
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

				btnSubmit.setOnClickListener {
					val transactionId = etTransactionId.text.toString()
					val password = etTransactionPassword.text.toString()
					val remark = etRemark.text.toString()
					val amount = etAmount.text.toString()

					if (transactionId.isEmpty()) {
						etTransactionId.error = activity.resources.getString(R.string.please_enter_transation_id)
						return@setOnClickListener
					} else if (amount.isEmpty()) {
						if (isTransferFromVoucher) {
							etAmount.error =
								activity.resources.getString(R.string.please_enter_voucher_qty)
						} else {
							etAmount.error =
								activity.resources.getString(R.string.please_enter_amount)
						}
						return@setOnClickListener
					} else if (password.isEmpty() && !isTransferFromVoucher) {
						etTransactionPassword.error = activity.resources.getString(R.string.please_enter_password)
						return@setOnClickListener
					} else {
						if (amount.toDouble() > 0) {
							val model = NSWalletTransferModel(transactionId, password, remark, amount, isTransferFromVoucher, packageId, voucherQty)
							switchResultActivity(dataResult, VerifyMemberActivity::class.java, bundleOf(
								NSConstants.KEY_WALLET_VERIFY to Gson().toJson(model)
							)
							)
						} else {
							Toast.makeText(activity, activity.resources.getString(R.string.please_enter_valid_amount), Toast.LENGTH_SHORT).show()
						}
					}
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
