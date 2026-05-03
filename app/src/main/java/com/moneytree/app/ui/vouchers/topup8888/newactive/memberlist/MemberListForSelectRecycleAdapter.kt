package com.moneytree.app.ui.vouchers.topup8888.newactive.memberlist

import android.app.Activity
import com.moneytree.app.R
import com.moneytree.app.base.adapter.BaseViewBindingAdapter
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.databinding.LayoutMemberListVoucherBinding
import com.moneytree.app.repository.network.responses.TopUp8888MemberListData

class MemberListForSelectRecycleAdapter(
    private val activity: Activity,
    private val selectedMember: TopUp8888MemberListData?,
    private val callback: (TopUp8888MemberListData) -> Unit
) : BaseViewBindingAdapter<LayoutMemberListVoucherBinding, TopUp8888MemberListData>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutMemberListVoucherBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, position, size ->
        binding.apply {
            response.apply {
                tvMemberId.text = addText(activity, R.string.member_id, username?:"")
                tvMemberName.text = addText(activity, R.string.member_name_value, fullname?:"")
                tvSponsorId.text = addText(activity, R.string.sponsor_id_value, sponsorId?:"")
                rbSelect.isChecked = selectedMember?.username == response.username
                
                clVoucherLayout.setOnClickListener {
                    callback.invoke(response)
                }
            }
        }
    }
)