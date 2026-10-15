package com.moneytree.app.ui.repurchaseAct

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.moneytree.app.R
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSActivityEvent
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.NSRequestCodes
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.common.utils.switchResultActivity
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.NsFragmentTopupVoucher30000Binding
import com.moneytree.app.repository.network.responses.DownlineSummary
import com.moneytree.app.repository.network.responses.MyStatus
import com.moneytree.app.ui.repurchaseAct.activation.ActivationListFragment
import com.moneytree.app.ui.repurchaseAct.downlines.RepurchaseDownLineFragment
import com.moneytree.app.ui.vouchers.topup8888.newactive.TopUp8888CreateActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class Repurchase3000ActivationFragment : NSFragment() {
	private val viewModel: Repurchase3000ActivationModel by lazy {
		ViewModelProvider(this)[Repurchase3000ActivationModel::class.java]
	}
	private var _binding: NsFragmentTopupVoucher30000Binding? = null
	private val adBinding get() = _binding!!
	private var downlineFragment: RepurchaseDownLineFragment? = null
	private var activationListFragment: ActivationListFragment? = null

	private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
		if (result.resultCode == RESULT_OK) {
			callTopupVoucher8888(false)
		}
	}
	
	companion object {
		fun newInstance() = Repurchase3000ActivationFragment()
	}
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = NsFragmentTopupVoucher30000Binding.inflate(inflater, container, false)
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
			HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.topup_8888_activation))
			callTopupVoucher8888(false)
		}
	}
	
	/**
	 * Set listener
	 */
	private fun setListener() {
		with(adBinding) {
			with(layoutHeader) {
				
				/** ----------------------------------------------------------------------------------------*/
				srlRefresh.setOnRefreshListener {
					callTopupVoucher8888(true)
				}
				
				btnSubmit.setOnClickListener {
					downlineFragment = RepurchaseDownLineFragment(viewModel.downlineList?: arrayListOf(), {
						callTopupVoucher8888(true, it)
					}) {
						downlineFragment = null
					}
					downlineFragment?.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog)
					downlineFragment?.show(childFragmentManager, "show_downlines")
				}
				
				btnNewCreate.setOnClickListener {
					activationListFragment = ActivationListFragment(viewModel, viewModel.activationList?: arrayListOf(), {
						callTopupVoucher8888(true, it)
					}) {
						activationListFragment = null
					}
					activationListFragment?.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog)
					activationListFragment?.show(childFragmentManager, "show_activation")
				}
				
				/** ----------------------------------------------------------------------------------------*/
			}
		}
	}
	
	/**
	 * To observe the view model for data changes
	 */
	private fun observeViewModel() {
		with(viewModel) {
			with(adBinding) {
				isProgressShowing.observe(
					viewLifecycleOwner
				) { shouldShowProgress ->
					updateProgress(shouldShowProgress)
				}
				
				failureErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
					adBinding.srlRefresh.isRefreshing = false
					showAlertDialog(errorMessage)
				}
				
				apiErrors.observe(viewLifecycleOwner) { apiErrors ->
					adBinding.srlRefresh.isRefreshing = false
					parseAndShowApiError(apiErrors)
				}
				
				noNetworkAlert.observe(viewLifecycleOwner) {
					adBinding.srlRefresh.isRefreshing = false
					showNoNetworkAlertDialog(
						getString(R.string.no_network_available),
						getString(R.string.network_unreachable)
					)
				}
				
				validationErrorId.observe(viewLifecycleOwner) { errorId ->
					adBinding.srlRefresh.isRefreshing = false
					showAlertDialog(getString(errorId))
				}
			}
		}
	}
	
	private fun callTopupVoucher8888(isRefresh: Boolean, callback: (() -> Unit)? = null) {
		viewModel.getTopUpVoucher30000Dashboard(!isRefresh) { topupData ->
			adBinding.srlRefresh.isRefreshing = false
			callback?.invoke()
			viewModel.downlineList = topupData?.downLineList?: arrayListOf()
			viewModel.activationList = topupData?.activationList?: arrayListOf()
			setSelfActivationStatus(topupData?.myStatus)
			setDownLineDetail(topupData?.downLineSummary)

			adBinding.btnSubmit.visible()
			adBinding.btnNewCreate.visible()
		}
	}
	
	private fun setSelfActivationStatus(selfActivation: MyStatus?) {
		adBinding.apply {
			clActivationStatus.setVisibility(selfActivation != null)
			val statusText = selfActivation?.status?.ifBlank { getString(R.string.not_activated) } ?: getString(R.string.not_activated)
			tvActivationStatus.text = statusText

			val isActivated = selfActivation != null && (selfActivation.status.equals("Activated", ignoreCase = true) || selfActivation.status.equals("Active", ignoreCase = true))

			if (isActivated) {
				tvActivationStatus.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_status_activated)
				tvActivationStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_activated_text))
			} else {
				tvActivationStatus.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_status_not_activated)
				tvActivationStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_not_activated_text))
			}

			tvVoucherCode.text = selfActivation?.voucherCode?.ifBlank { "-" } ?: "-"
			tvSponsorId.text = selfActivation?.sponsorId?.ifBlank { "-" } ?: "-"
			tvEntryDate.text = selfActivation?.entryDate?.ifBlank { "-" } ?: "-"
			tvExpiryDate.text = selfActivation?.expiryDate?.ifBlank { "-" } ?: "-"
		}
	}
	
	private fun setDownLineDetail(downLineSummary: DownlineSummary?) {
		adBinding.apply {
			clDownlineSummary.setVisibility(downLineSummary != null)
			tvTotalDownlineValue.text = (downLineSummary?.totalDownline ?: 0).toString()
			tvDownlineActivated.text = (downLineSummary?.activated ?: 0).toString()
			tvDownlineNotActivated.text = (downLineSummary?.notActivated ?: 0).toString()
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
