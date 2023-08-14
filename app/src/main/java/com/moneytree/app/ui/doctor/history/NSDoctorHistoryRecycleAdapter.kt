package com.moneytree.app.ui.doctor.history

import android.app.Activity
import com.moneytree.app.R
import com.moneytree.app.base.adapter.BaseViewBindingAdapter
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.setCircleImage
import com.moneytree.app.common.utils.setImage
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.databinding.LayoutDoctorHistoryItemBinding
import com.moneytree.app.databinding.LayoutDoctorItemBinding
import com.moneytree.app.repository.network.responses.DoctorDataItem
import com.moneytree.app.repository.network.responses.DoctorHistoryDataItem

class NSDoctorHistoryRecycleAdapter(
    private val activity: Activity,
    private val callback: ((DoctorHistoryDataItem) -> Unit),
    private val pageChange: (() -> Unit),
) : BaseViewBindingAdapter<LayoutDoctorHistoryItemBinding, DoctorHistoryDataItem>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutDoctorHistoryItemBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, position, size ->
        binding.apply {
            response.apply {
                ivDoctorImg.setImage(R.drawable.placeholder, image)
                tvDoctorName.text = doctorName
                tvPatientName.text = name
                val ages = "Age: $age"
                tvAge.text = ages
                tvGender.text = gender
                tvCharges.text = addText(activity, R.string.price_value, charges.toString())
                /*tvExperience.text = experience
                tvEducation.text = education*/

                if (position == size - 1) {
                    if (((position + 1) % NSConstants.PAGINATION) == 0) {
                        pageChange.invoke()
                    }
                }
            }
        }
    }
)