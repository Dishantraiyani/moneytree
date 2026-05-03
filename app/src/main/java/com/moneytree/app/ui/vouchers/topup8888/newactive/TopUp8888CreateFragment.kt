package com.moneytree.app.ui.vouchers.topup8888.newactive

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.moneytree.app.R
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSActivityEvent
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.NSRequestCodes
import com.moneytree.app.common.OnSingleClickListener
import com.moneytree.app.common.callbacks.NSDialogClickCallback
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.databinding.NsFragmentTopupVoucher8888CreateBinding
import com.moneytree.app.repository.network.responses.TopUp8888MemberListData
import com.moneytree.app.ui.vouchers.topup8888.newactive.memberlist.MemberListForSelectFragment
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TopUp8888CreateFragment : NSFragment() {
	private val viewModel: TopUp8888CreateModel by lazy {
		ViewModelProvider(this)[TopUp8888CreateModel::class.java]
	}
	private var _binding: NsFragmentTopupVoucher8888CreateBinding? = null
	private val adBinding get() = _binding!!
	private var voucherQty: Int = 0
	private var memberListFragment: MemberListForSelectFragment? = null
	
	
	companion object {
		fun newInstance(bundle: Bundle?) = TopUp8888CreateFragment().apply {
			arguments = bundle
		}
	}
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = NsFragmentTopupVoucher8888CreateBinding.inflate(inflater, container, false)
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
			HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.new_activation))
			setMembers(false)
		}
	}
	
	private fun setMembers(isRefresh: Boolean, callback: (() -> Unit)? = null) {
		viewModel.getMemberList(!isRefresh) {
			callback?.invoke()
			viewModel.memberList = it?.memberList?: arrayListOf()
			setPins((it?.availablePin?:0.0).toInt())
			memberListFragment?.setMemberList(it?.memberList?: arrayListOf())
			adBinding.tvSponsorName.text = pref.userData?.data?.fullName
		}
	}
	
	private fun setPins(pins: Int) {
		viewModel.availablePins = pins
		adBinding.etAvailablePins.text = pins.toString()
		adBinding.tvPinsNotAvailable.setVisibility(pins <= 0)
	}
	
	private fun showMemberList(list: List<TopUp8888MemberListData>) {
		memberListFragment = MemberListForSelectFragment(list, viewModel.selectedMember, {
			setMembers(true, it)
		}, {
			memberListFragment = null
			viewModel.selectedMember = it
			val selectedMember = "${it.username} - ${it.fullname}"
			adBinding.tvSelectMember.text = selectedMember
			adBinding.etSponsorId.setText(it.sponsorId?:"")
			adBinding.tvSponsorId.setVisibility(true)
			adBinding.cardSponsorId.setVisibility(true)
		}) {
			memberListFragment = null
		}
		memberListFragment?.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog)
		memberListFragment?.show(childFragmentManager, "show_member_list")
	}
	
	/**
	 * Set listener
	 */
	private fun setListener() {
		with(adBinding) {
			cardSelectMember.setOnClickListener {
				showMemberList(viewModel.memberList)
			}
			
			//{"status":true,"message":"Activation Successful for Member: 9010530525"}
			btnSubmit.setOnClickListener(object : OnSingleClickListener() {
				override fun onSingleClick(v: View?) {
					if (viewModel.availablePins >= 0) {
						val sponsorId = etSponsorId.text.toString()
						
						if (viewModel.selectedMember?.username.isNullOrEmpty()) {
							Toast.makeText(activity, activity.resources.getString(R.string.please_select_sponsor_member), Toast.LENGTH_SHORT).show()
							return
						} else if (sponsorId.isEmpty()) {
							etSponsorId.error =
								activity.resources.getString(R.string.please_enter_sponsor_id)
							return
						} else {
							fun newActivation() {
								viewModel.saveTopup8888(viewModel.selectedMember?.username?:"", sponsorId, true) {
									if (it.isNotEmpty()) {
										showOnlyCommonDialog("", it, "Yes", callback = object :
											NSDialogClickCallback {
											override fun onClick(isOk: Boolean) {
												if (isOk) {
													requireActivity().setResult(RESULT_OK)
													
													etSponsorId.setText("")
													adBinding.tvSponsorId.gone()
													adBinding.cardSponsorId.gone()
													
													viewModel.selectedMember = null
													tvSelectMember.text = ""
													
													setMembers(false)
												}
											}
										})
									}
								}
							}
							
							newActivation()
							/*viewModel.getUserDetail {
								if (it) {
									if (NSUtilities.checkKycVerified()) {
										newActivation()
										return@getUserDetail
									}
									showCommonDialog("Kyc Verification", activity.resources.getString(R.string.your_kyc_verification_ask), "Yes", "No", callback = object :
										NSDialogClickCallback {
										override fun onClick(isOk: Boolean) {
											if (isOk) {
												NSUtilities.isKycVerified(activity, false)
											} else {
												newActivation()
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
												newActivation()
											}
										}
									})
								}
							}*/
						}
						
					} else {
						Toast.makeText(
							activity,
							activity.resources.getString(R.string.no_available_pins_please_get_pins_first),
							Toast.LENGTH_SHORT
						).show()
					}
				}
			})
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
