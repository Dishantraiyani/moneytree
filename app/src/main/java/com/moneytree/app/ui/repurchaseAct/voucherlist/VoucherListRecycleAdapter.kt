package com.moneytree.app.ui.repurchaseAct.voucherlist

import androidx.core.content.ContextCompat
import com.moneytree.app.R
import com.moneytree.app.base.adapter.BaseViewBindingAdapter
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.databinding.LayoutVoucherListItemBinding
import com.moneytree.app.repository.network.responses.VoucherList30000DataItem

class VoucherListRecycleAdapter : BaseViewBindingAdapter<LayoutVoucherListItemBinding, VoucherList30000DataItem>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutVoucherListItemBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, position, size ->
        binding.apply {
            response.apply {
                val context = root.context

                val statusText = claimStatus?.ifBlank { context.getString(R.string.accepted) } ?: context.getString(R.string.accepted)
                tvClaimStatus.text = statusText

                val isAccepted = statusText.equals("Accepted", ignoreCase = true) || statusText.equals("Active", ignoreCase = true) || statusText.equals("Success", ignoreCase = true)
                if (isAccepted) {
                    tvClaimStatus.background = ContextCompat.getDrawable(context, R.drawable.bg_status_activated)
                    tvClaimStatus.setTextColor(ContextCompat.getColor(context, R.color.status_activated_text))
                } else {
                    tvClaimStatus.background = ContextCompat.getDrawable(context, R.drawable.bg_status_not_activated)
                    tvClaimStatus.setTextColor(ContextCompat.getColor(context, R.color.status_not_activated_text))
                }

                tvVoucherAmount.text = voucherAmount?.ifBlank { "-" } ?: "-"

                tvEntryDate.text = entryDate?.ifBlank { "-" } ?: "-"
                tvExpiryDate.text = expiryDate?.ifBlank { "-" } ?: "-"

                tvTargetRepurchase.text = targetRepurchase?.ifBlank { "-" } ?: "-"
                tvRepurchaseDone.text = repurchaseDone?.ifBlank { "-" } ?: "-"

                val claimRemarkData = claimRemark?.ifBlank { "-" } ?: "-"
                tvClaimRemark.text = claimRemarkData
                tvClaimRemark.setVisibility(claimRemarkData != "-")
                tvClaimRemarkTitle.setVisibility(claimRemarkData != "-")
                vDivider2.setVisibility(claimRemarkData != "-")
            }
        }
    }
)
