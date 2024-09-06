package com.moneytree.app.ui.meeting

import com.moneytree.app.base.adapter.BaseViewBindingAdapter
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.databinding.LayoutMeetingsBinding
import com.moneytree.app.repository.network.responses.MeetingsDataResponse

class MeetingRecycleAdapter(
	private val callback: ((MeetingsDataResponse, Boolean, Boolean, Int) -> Unit)
) : BaseViewBindingAdapter<LayoutMeetingsBinding, MeetingsDataResponse>(

	bindingInflater = { inflater, parent, attachToParent ->
		LayoutMeetingsBinding.inflate(inflater, parent, attachToParent)
	},

	onBind = { binding, response, position, size ->
		binding.apply {
			response.apply {

				etMeetingName.text = eventName
				tvHostName.text = hostName
				tvSpecialGuestName.text = specialGuestName
				tvMeetingDate.text = eventDate
				tvMeetingTime.text = eventTime
				val a = "$venue, $city, $state"
				tvAddress.text = a

				ivDelete.setSafeOnClickListener {
					callback.invoke(response, false, true, position)
				}

				cardMeeting.setSafeOnClickListener {
					callback.invoke(response, true, false, position)
				}
			}
		}
	}
)