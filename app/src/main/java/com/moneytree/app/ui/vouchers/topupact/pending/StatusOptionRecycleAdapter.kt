package com.moneytree.app.ui.vouchers.topupact.pending

import android.content.res.ColorStateList
import android.graphics.Color
import com.moneytree.app.base.adapter.BaseViewBindingAdapter
import com.moneytree.app.databinding.LayoutStatusOptionItemBinding
import com.moneytree.app.repository.network.responses.StatusOptionModel


class StatusOptionRecycleAdapter(
	private val callback: ((Int) -> Unit)
) : BaseViewBindingAdapter<LayoutStatusOptionItemBinding, StatusOptionModel>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutStatusOptionItemBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, position , _ ->
        binding.apply {
            response.apply {
                rbLanguage.text = title
                
                val singleStates = arrayOf(intArrayOf())
                val colors = intArrayOf(Color.parseColor("#52965B"))
                
                rbLanguage.buttonTintList = ColorStateList(singleStates, colors)
                rbLanguage.isChecked = isChecked
                rbLanguage.isEnabled = isEnable
                rbLanguage.isClickable = isEnable
                
                rbLanguage.setOnClickListener {
                    callback.invoke(position)
                }
                
                
            }
        }
    }
)