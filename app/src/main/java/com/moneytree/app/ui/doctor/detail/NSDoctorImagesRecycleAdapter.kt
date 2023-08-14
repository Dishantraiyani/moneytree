package com.moneytree.app.ui.doctor.detail

import com.moneytree.app.R
import com.moneytree.app.base.adapter.BaseViewBindingAdapter
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.setImage
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.LayoutDoctorImagesBinding

class NSDoctorImagesRecycleAdapter(
    private val imageAdd: (() -> Unit),
    private val callback: ((Int) -> Unit)
) : BaseViewBindingAdapter<LayoutDoctorImagesBinding, String>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutDoctorImagesBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, position, size ->
        binding.apply {
            response.apply {
                if (response == "ADD_IMAGE") {
                    ivPatientImg.setImageResource(R.drawable.ic_add_new)
                    ivDelete.gone()
                    cardImage.setSafeOnClickListener {
                        imageAdd.invoke()
                    }
                } else {
                    ivDelete.visible()
                    ivPatientImg.setImage(R.drawable.placeholder, response)
                    ivDelete.setSafeOnClickListener {
                        callback.invoke(position)
                    }
                }
            }
        }
    }
)