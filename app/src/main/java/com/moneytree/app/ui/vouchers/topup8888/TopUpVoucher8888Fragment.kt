package com.moneytree.app.ui.vouchers.topup8888

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
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.common.utils.switchResultActivity
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.NsFragmentTopupVoucher8888Binding
import com.moneytree.app.repository.network.responses.DownlineListItem
import com.moneytree.app.repository.network.responses.PlansResponse
import com.moneytree.app.repository.network.responses.ServiceProviderDataItem
import com.moneytree.app.ui.recharge.plans.PlansFragment
import com.moneytree.app.ui.vouchers.topup8888.downlines.DownlineFragment
import com.moneytree.app.ui.vouchers.topup8888.newactive.TopUp8888CreateActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TopUpVoucher8888Fragment : NSFragment() {
	private val viewModel: TopUpVoucher8888Model by lazy {
		ViewModelProvider(this)[TopUpVoucher8888Model::class.java]
	}
	private var _binding: NsFragmentTopupVoucher8888Binding? = null
	private val adBinding get() = _binding!!
	private var downlineFragment: DownlineFragment? = null
	
	private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
		if (result.resultCode == RESULT_OK) {
			callTopupVoucher8888(false)
		}
	}
	
	companion object {
		fun newInstance() = TopUpVoucher8888Fragment()
	}
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = NsFragmentTopupVoucher8888Binding.inflate(inflater, container, false)
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
					downlineFragment = DownlineFragment(viewModel.downlineList?: arrayListOf(), {
						callTopupVoucher8888(true, it)
					}) {
						downlineFragment = null
					}
					downlineFragment?.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog)
					downlineFragment?.show(childFragmentManager, "show_downlines")
				}
				
				btnNewCreate.setOnClickListener {
					switchResultActivity(launcher, TopUp8888CreateActivity::class.java)
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
		viewModel.getTopupVoucher8888Dashboard(!isRefresh) { topupData ->
			adBinding.srlRefresh.isRefreshing = false
			callback?.invoke()
			setSelfActivationStatus(topupData?.selfActivation)
			setPins(topupData?.availablePin?:0)
			setDownlineDetail(topupData?.downlineList?: arrayListOf())
		}
	}
	
	private fun setSelfActivationStatus(selfActivation: Any?) {
		adBinding.apply {
			tvActivationSubTitle.text = if (selfActivation == null) getString(R.string.you_are_not_yet_activated_via_8888_pro) else getString(R.string.you_are_activated_via_8888_pro)
			tvActivationStatus.text = if (selfActivation == null) getString(R.string.not_activated) else getString(R.string.active)
			tvActivationStatus.setTextColor(if (selfActivation == null) ContextCompat.getColor(requireContext(), R.color.orange) else ContextCompat.getColor(requireContext(), R.color.green_light))
		}
	}
	
	private fun setPins(pins: Int) {
		adBinding.tvPinsValue.text = pins.toString()
	}
	
	private fun setDownlineDetail(downlineList: List<DownlineListItem>) {
		viewModel.downlineList = downlineList
		downlineFragment?.setDownlineList(downlineList)
		
		adBinding.apply {
			tvTotalDownlineValue.text = downlineList.size.toString()
			
			val activated = "${getString(R.string.activated)}: ${downlineList.filter { it.activationId != null }.size}"
			tvDownlineActivated.text =  activated
			
			val notActivated = "${getString(R.string.not_activated)}: ${downlineList.filter { it.activationId == null }.size}"
			tvDownlineNotActivated.text =  notActivated
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
