package com.moneytree.app.ui.vouchers.topup8888.downlines

import android.app.Activity
import com.moneytree.app.R
import com.moneytree.app.base.adapter.BaseViewBindingAdapter
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.databinding.LayoutDownlinesBinding
import com.moneytree.app.repository.network.responses.DownlineListItem

class DownlineRecycleAdapter(
    private val activity: Activity
) : BaseViewBindingAdapter<LayoutDownlinesBinding, DownlineListItem>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutDownlinesBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, position, size ->
        binding.apply {
            response.apply {
                tvMemberId.text = addText(activity, R.string.member_id, username?:"")
                tvMemberName.text = addText(activity, R.string.member_name_value, fullname?:"")
                tvMobile.text = addText(activity, R.string.mobile_value, mobile?:"")
                
                tvStatusTitle.setVisibility(!activationStatus.isNullOrEmpty())
                tvStatus.setVisibility(!activationStatus.isNullOrEmpty())
                
                tvStatus.text = activationStatus?:""
                val date = if (activatedAt.isNullOrEmpty()) "Not Available" else activatedAt
                tvDateStatus.text = addText(activity, R.string.activated_on, date)
            }
        }
    }
)