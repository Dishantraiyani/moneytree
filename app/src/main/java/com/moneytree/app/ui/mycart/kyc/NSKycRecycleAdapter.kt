package com.moneytree.app.ui.mycart.kyc

import android.app.Activity
import com.moneytree.app.R
import com.moneytree.app.base.adapter.BaseViewBindingAdapter
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.setCircleImage
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.databinding.LayoutDoctorItemBinding
import com.moneytree.app.databinding.LayoutKycBinding
import com.moneytree.app.repository.network.responses.DoctorDataItem
import com.moneytree.app.repository.network.responses.KycListResponse

class NSKycRecycleAdapter(
) : BaseViewBindingAdapter<LayoutKycBinding, KycListResponse>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutKycBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, position, size ->
        binding.apply {
            response.apply {
                tvMemberId.text = key
                tvIsActive.text = value
            }
        }
    }
)