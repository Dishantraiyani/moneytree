package com.moneytree.app.ui.repurchaseAct.activation

import android.app.Activity
import androidx.core.content.ContextCompat
import com.moneytree.app.R
import com.moneytree.app.base.adapter.BaseViewBindingAdapter
import com.moneytree.app.databinding.LayoutRepurchaseActivationListBinding
import com.moneytree.app.repository.network.responses.ActivationListItem

class ActivationListRecycleAdapter(
    private val activity: Activity,
    private val callback: (ActivationListItem) -> Unit
) : BaseViewBindingAdapter<LayoutRepurchaseActivationListBinding, ActivationListItem>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutRepurchaseActivationListBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, position, size ->
        binding.apply {
            response.apply {
                tvVoucherId.text = voucherId?.ifBlank { "-" } ?: "-"
                
                val statusText = status?.ifBlank { activity.getString(R.string.active_title) } ?: activity.getString(R.string.active_title)
                tvStatus.text = statusText
                
                val isActive = statusText.equals("Active", ignoreCase = true) || statusText.equals("Activated", ignoreCase = true)
                if (isActive) {
                    tvStatus.background = ContextCompat.getDrawable(activity, R.drawable.bg_status_activated)
                    tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.status_activated_text))
                } else {
                    tvStatus.background = ContextCompat.getDrawable(activity, R.drawable.bg_status_not_activated)
                    tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.status_not_activated_text))
                }

                tvDate.text = date?.ifBlank { "-" } ?: "-"
                tvVoucherCode.text = voucherCode?.ifBlank { "-" } ?: "-"
                tvSponsorId.text = sponsorId?.ifBlank { "-" } ?: "-"
                tvActivatedBy.text = activatedBy?.ifBlank { "-" } ?: "-"
                tvActivatedByName.text = activatedByName?.ifBlank { "-" } ?: "-"
                tvEntryDate.text = entryDate?.ifBlank { "-" } ?: "-"
                tvExpiryDate.text = expiryDate?.ifBlank { "-" } ?: "-"

                llCheckVoucher.setOnClickListener {
                    callback.invoke(response)
                }
            }
        }
    }
)
