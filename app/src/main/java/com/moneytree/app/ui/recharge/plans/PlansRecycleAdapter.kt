package com.moneytree.app.ui.recharge.plans

import android.app.Activity
import com.moneytree.app.R
import com.moneytree.app.base.adapter.BaseViewBindingAdapter
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.databinding.LayoutPlansBinding
import com.moneytree.app.repository.network.responses.PackItem

class PlansRecycleAdapter(
    private val activity: Activity,
    private val callback: (PackItem) -> Unit
) : BaseViewBindingAdapter<LayoutPlansBinding, PackItem>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutPlansBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, position, size ->
        binding.apply {
            response.apply {
                NSUtilities.getBoldTexts(response.benefit?:"", tvBenefit)
                tvAmount.text = addText(activity, R.string.price_value, amount.toString())

                clPlan.setSafeOnClickListener {
                    callback.invoke(response)
                }
            }
        }
    }
)