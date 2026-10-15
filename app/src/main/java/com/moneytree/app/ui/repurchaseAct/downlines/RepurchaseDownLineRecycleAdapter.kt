package com.moneytree.app.ui.repurchaseAct.downlines

import android.app.Activity
import com.moneytree.app.R
import com.moneytree.app.base.adapter.BaseViewBindingAdapter
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.databinding.LayoutDownlinesBinding
import com.moneytree.app.repository.network.responses.Repurchase3000DownLineListItem

class RepurchaseDownLineRecycleAdapter(
    private val activity: Activity
) : BaseViewBindingAdapter<LayoutDownlinesBinding, Repurchase3000DownLineListItem>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutDownlinesBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _, _ ->
        binding.apply {
            response.apply {
                tvMemberId.text = addText(activity, R.string.member_id, memberId?:"")
                tvMemberName.text = addText(activity, R.string.member_name_value, memberName?:"")
                tvMobile.text = addText(activity, R.string.mobile_value, mobile?:"")
                
                tvStatusTitle.setVisibility(!status.isNullOrEmpty())
                tvStatus.setVisibility(!status.isNullOrEmpty())
                
                tvStatus.text = status?:""
                //val date = if (entryDate.isNullOrEmpty()) "Not Available" else entryDate
                tvDateStatus.text = addText(activity, R.string.activated_on, entryDate?:"-")
            }
        }
    }
)