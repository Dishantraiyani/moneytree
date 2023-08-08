package com.moneytree.app.ui.doctor

import android.app.Activity
import com.moneytree.app.R
import com.moneytree.app.base.adapter.BaseViewBindingAdapter
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.setCircleImage
import com.moneytree.app.databinding.LayoutDoctorItemBinding
import com.moneytree.app.repository.network.responses.DoctorDataItem

class NSDoctorRecycleAdapter(
    private val activity: Activity,
    private val callback: ((DoctorDataItem, Boolean) -> Unit),
    private val pageChange: (() -> Unit),
) : BaseViewBindingAdapter<LayoutDoctorItemBinding, DoctorDataItem>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutDoctorItemBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, position, size ->
        binding.apply {
            response.apply {
                ivDoctorImg.setCircleImage(R.drawable.placeholder, image)
                tvDoctorName.text = doctorName
                tvMobileNo.text = mobile
                tvCharges.text = addText(activity, R.string.price_value, charges.toString())
                tvExperience.text = experience
                tvEducation.text = education

                if (position == size - 1) {
                    if (((position + 1) % NSConstants.PAGINATION) == 0) {
                        pageChange.invoke()
                    }
                }
            }
        }
    }
)