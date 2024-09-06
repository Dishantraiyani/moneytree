package com.moneytree.app.ui.meeting.review

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.moneytree.app.R
import com.moneytree.app.base.fragment.BaseViewModelFragment
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.databinding.FragmentMeetingReviewBinding
import com.moneytree.app.repository.network.manager.requestBody
import com.moneytree.app.ui.meeting.edit.MeetingAddEditViewModel
import okhttp3.RequestBody


class ReviewFragment : BaseViewModelFragment<MeetingAddEditViewModel, FragmentMeetingReviewBinding>() {

	override val viewModel: MeetingAddEditViewModel by lazy {
		ViewModelProvider(this)[MeetingAddEditViewModel::class.java]
	}

	private var imageList: MutableList<String> = arrayListOf()
	private var map: HashMap<String, String> = hashMapOf()

	companion object {
		fun newInstance(bundle: Bundle?) = ReviewFragment().apply {
			arguments = bundle
		}
	}

	override fun getFragmentBinding(
		inflater: LayoutInflater,
		container: ViewGroup?
	): FragmentMeetingReviewBinding {
		return FragmentMeetingReviewBinding.inflate(inflater, container, false)
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
			getMeetingMap()
			getMeetingImages()
			HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.meeting_review))
		}
	}

	private fun getMeetingMap() {
		val data = arguments?.getString(NSConstants.KEY_IS_MEETING_REVIEW)
		val listType = object : TypeToken<HashMap<String, String>>() {}.type
		map = Gson().fromJson(data, listType)
		binding.etReview.setText(map["meeting_review"]?:"")
	}

	private fun getMeetingImages() {
		val data = arguments?.getString(NSConstants.KEY_IS_MEETING_IMAGES)
		val listType = object : TypeToken<List<String>>() {}.type
		imageList = Gson().fromJson(data, listType)
	}

	/**
	 * Set listener
	 */
	private fun setListener() {
		with(binding) {
			btnSubmit.setOnClickListener(object : SingleClickListener() {
				override fun performClick(v: View?) {
					addUpdateData()
				}
			})
		}
	}

	private fun addUpdateData() {
		binding.apply {
			viewModel.apply {
				val meetingReview = etReview.text.toString()

				if (meetingReview.isEmpty()) {
					etReview.error = "Enter Meeting Review"
					return
				}

				map["meeting_review"] = meetingReview

				val map1: HashMap<String, RequestBody> = hashMapOf()

				for ((key, value) in map) {
					map1[key] = requestBody(value)
				}

				viewModel.addOrUpdateMeeting(map1, imageList) {
					requireActivity().setResult(RESULT_OK)
					finish()
				}
			}
		}
	}
}
