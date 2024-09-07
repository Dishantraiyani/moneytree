package com.moneytree.app.ui.meeting.edit

import android.app.Activity.RESULT_OK
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.github.drjacky.imagepicker1.ImagePicker
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.base.fragment.BaseViewModelFragment
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.callbacks.NSDialogClickCallback
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.common.utils.switchResultActivity
import com.moneytree.app.common.utils.visible
import com.moneytree.app.config.ApiConfig
import com.moneytree.app.databinding.FragmentMeetingAddEditBinding
import com.moneytree.app.repository.network.manager.requestBody
import com.moneytree.app.repository.network.responses.MeetingsDataResponse
import com.moneytree.app.ui.meeting.review.ReviewActivity
import okhttp3.RequestBody


class MeetingAddEditFragment : BaseViewModelFragment<MeetingAddEditViewModel, FragmentMeetingAddEditBinding>() {

	override val viewModel: MeetingAddEditViewModel by lazy {
		ViewModelProvider(this)[MeetingAddEditViewModel::class.java]
	}

	private var isAddMeeting = false
	private var selectedMeeting: String? = null
	private var selectedModel: MeetingsDataResponse? = null

	private var imageList: MutableList<String> = arrayListOf()

	private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
		if (result.resultCode == RESULT_OK) {
			val uri: Uri? = result.data?.data
			uri?.let {
				imageList.clear()
				imageList.add(uri.path!!)
				if (imageList.isValidList()) {
					binding.ivEdit.visible()
					Glide.with(requireActivity()).load(imageList[0]).into(binding.ivMeetingImg)
				}
			}
		}
	}

	private val meetingReviewLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
		if (result.resultCode == RESULT_OK) {
			requireActivity().setResult(RESULT_OK)
			finish()
		}
	}

	companion object {
		fun newInstance(bundle: Bundle?) = MeetingAddEditFragment().apply {
			arguments = bundle
		}
	}

	override fun getFragmentBinding(
		inflater: LayoutInflater,
		container: ViewGroup?
	): FragmentMeetingAddEditBinding {
		return FragmentMeetingAddEditBinding.inflate(inflater, container, false)
	}

	override fun setupViews() {
		super.setupViews()
		baseObserveViewModel(viewModel)
		viewCreated()
		setListener()
	}

	/**
	 * View created
	 */
	private fun viewCreated() {
		with(binding) {
			isAddMeeting = arguments?.getBoolean(NSConstants.KEY_IS_ADD_MEETING)?:false
			selectedMeeting = arguments?.getString(NSConstants.KEY_IS_SELECTED_MEETING)
			HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(if (isAddMeeting) R.string.add_meeting else R.string.edit_meeting,))
			if (!isAddMeeting) {
				setMeetingDetail()
			}
		}
	}

	/**
	 * Set listener
	 */
	private fun setListener() {
		with(binding) {
			with(layoutHeader) {

				clMeetingImg.setSafeOnClickListener {
					imagePicker()
				}

				etMeetingDate.setSafeOnClickListener {
					NSUtilities.selectMeetingDate(requireActivity()) {
						etMeetingDate.text = it
					}
				}

				etMeetingTime.setSafeOnClickListener {
					NSUtilities.selectTime(requireActivity()) {
						etMeetingTime.text = it
					}
				}

				btnSubmit.setOnClickListener(object : SingleClickListener() {
					override fun performClick(v: View?) {
						addUpdateData()
					}
				})
			}
		}
	}

	private fun addUpdateData(isUpdate: Boolean = false) {
		binding.apply {
			viewModel.apply {
				val meetingName = etFullName.text.toString()
				val meetingDate = etMeetingDate.text.toString()
				val meetingTime = etMeetingTime.text.toString()
				val address = etAddress.text.toString()
				val city = etCity.text.toString()
				val state = etState.text.toString()
				val hostName = etHostName.text.toString()
				val specialGuestName = etSpecialGuestName.text.toString()
				val mobile = etMobile.text.toString()
				val newPerson = etNewPerson.text.toString()
				val oldPerson = etOldPerson.text.toString()
				val expectedResult = etExpectedResult.text.toString()

				if (meetingName.isEmpty()) {
					etFullName.error = "Enter Meeting Name"
					return
				} else if (address.isEmpty()) {
					etAddress.error = "Enter Address."
					return
				} else if (city.isEmpty()) {
					etCity.error = "Enter City"
					return
				} else if (state.isEmpty()) {
					etState.error = "Enter State"
					return
				} else if (hostName.isEmpty()) {
					etHostName.error = "Enter Host Name"
					return
				} else if (specialGuestName.isEmpty()) {
					etSpecialGuestName.error = "Enter Special Guest Name"
					return
				}  else if (mobile.isEmpty()) {
					etMobile.error = "Enter Mobile No."
					return
				} else if (mobile.isEmpty() || mobile.length < 10 || !NSUtilities.isValidMobile(mobile)) {
					etMobile.error = getString(R.string.please_enter_valid_mobile_no)
					return
				} else if (newPerson.isEmpty()) {
					etNewPerson.error = "Enter New Person Name."
					return
				} else if (oldPerson.isEmpty()) {
					etOldPerson.error = "Enter Old Person Name."
					return
				} else if (expectedResult.isEmpty()) {
					etExpectedResult.error = "Enter Excepted Result."
					return
				}

				val map: HashMap<String, String> = hashMapOf()
				if (isUpdate) {
					map["event_id"] = selectedModel?.eventId?:""
				}
				map["event_name"] = meetingName
				map["meeting_date"] = meetingDate
				map["meeting_time"] = meetingTime
				map["venue"] = address
				map["city"] = city
				map["state"] = state
				map["host_name"] = hostName
				map["special_guest_name"] = specialGuestName
				map["mobile"] = mobile
				map["new_person"] = newPerson
				map["old_person"] = oldPerson
				map["meeting_result"] = expectedResult
				map["meeting_review"] = tvReview.text.toString()

				val map1: HashMap<String, RequestBody> = hashMapOf()

				for ((key, value) in map) {
					map1[key] = requestBody(value)
				}

				if (selectedMeeting == null || isUpdate) {
					viewModel.addOrUpdateMeeting(map1, imageList) {
						requireActivity().setResult(RESULT_OK)
						finish()
					}
				} else {
					map["event_id"] = selectedModel?.eventId?:""
					switchResultActivity(meetingReviewLauncher, ReviewActivity::class.java, bundleOf(NSConstants.KEY_IS_MEETING_REVIEW to Gson().toJson(map), NSConstants.KEY_IS_MEETING_IMAGES to Gson().toJson(imageList)))
				}
			}
		}
	}

	private fun imagePicker() {
		ImagePicker.with(requireActivity())
			.crop(16f, 9f)
			.createIntentFromDialog { intent, _ ->
				imagePickerLauncher.launch(intent)
			}
	}

	private fun setMeetingDetail() {
		binding.apply {
			if (selectedMeeting != null && selectedMeeting?.isNotEmpty() == true) {
				selectedModel = Gson().fromJson(selectedMeeting, MeetingsDataResponse::class.java)
				selectedModel?.apply {
					etFullName.setText(eventName)
					etMeetingDate.text = eventDate
					etMeetingTime.text = eventTime
					etAddress.setText(venue)
					etCity.setText(city)
					etState.setText(state)
					etHostName.setText(hostName)
					etSpecialGuestName.setText(specialGuestName)
					etMobile.setText(mobile)
					etNewPerson.setText(newPerson)
					etOldPerson.setText(oldPerson)
					etExpectedResult.setText(meetingResult)
					tvReview.text = meetingReview
					btnUpdate.visible()
					btnSubmit.text = activity.resources.getString(if(meetingReview.isNullOrEmpty())R.string.add_review else R.string.edit_review)

					if (!meetingImage.isNullOrEmpty()) {
						binding.ivEdit.visible()
						Glide.with(requireActivity()).load(ApiConfig.meetingImages + meetingImage)
							.into(binding.ivMeetingImg)
					}

					btnUpdate.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							addUpdateData(true)
						}
					})

					ivDelete.visible()
					ivDelete.setSafeOnClickListener {
						viewModel.apply {
							showCommonDialog(getString(R.string.app_name), getString(R.string.are_you_sure_delete), getString(R.string.yes_title), getString(R.string.no_title), callback = object :
								NSDialogClickCallback {
								override fun onClick(isOk: Boolean) {
									if (isOk) {
										deleteMeetings(eventId!!) {
											if (it.status) {
												requireActivity().setResult(RESULT_OK)
												finish()
											} else {
												if (it.message?.isNotEmpty() == true) {
													showError(it.message ?: "")
												}
											}
										}
									}
								}
							})
						}
					}
				}
			}
		}
	}
}
