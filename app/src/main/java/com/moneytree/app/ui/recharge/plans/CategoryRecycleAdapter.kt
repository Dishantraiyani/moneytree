package com.moneytree.app.ui.recharge.plans

import android.graphics.Color
import com.moneytree.app.R
import com.moneytree.app.base.adapter.BaseViewBindingAdapter
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.databinding.LayoutCategoryNameBinding

var plansSelectedPosition = 0
class CategoryRecycleAdapter(
    private val callback: ((String, Int, Boolean) -> Unit)
) : BaseViewBindingAdapter<LayoutCategoryNameBinding, String>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutCategoryNameBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, position, size ->
        binding.apply {
            response.apply {
                tvCategoryName.text = response

                if (plansSelectedPosition == position) {
                    tvCategoryName.setTextColor(Color.parseColor("#eba94a") )
                    viewLine.setBackgroundResource(R.drawable.background_line)
                    callback.invoke(response, position, true)
                } else {
                    tvCategoryName.setTextColor(Color.parseColor("#000000") )
                    viewLine.setBackgroundResource(0)
                }

                tvCategoryName.setSafeOnClickListener {
                    plansSelectedPosition = position
                    callback.invoke(response, position, false)
                }
            }
        }
    }
)