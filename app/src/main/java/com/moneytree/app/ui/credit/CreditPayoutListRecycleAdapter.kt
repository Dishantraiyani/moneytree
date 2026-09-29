package com.moneytree.app.ui.credit

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.callbacks.NSInfoSelectCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.databinding.LayoutCreditPayoutItemBinding
import com.moneytree.app.repository.network.responses.CreditPayoutData
import androidx.core.graphics.toColorInt

class CreditPayoutListRecycleAdapter(
	activityNS: Activity,
	onPageChange: NSPageChangeCallback,
	onClick: NSInfoSelectCallback,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val payoutData: MutableList<CreditPayoutData> = arrayListOf()
    private val onPageChangeCallback: NSPageChangeCallback = onPageChange
    private val onClickListener: NSInfoSelectCallback = onClick

    fun updateData(payoutList: MutableList<CreditPayoutData>) {
        payoutData.addAll(payoutList)
        notifyItemRangeChanged(0, payoutData.size - 1)
    }

    fun clearData() {
        payoutData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val orderView = LayoutCreditPayoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CreditPayoutViewHolder(orderView)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is CreditPayoutViewHolder) {
            val holder: CreditPayoutViewHolder = holderRec
            holder.bind(payoutData[holder.absoluteAdapterPosition])
        }

        if (position == payoutData.size - 1) {
            if (((position + 1) % NSConstants.PAGINATION) == 0) {
                onPageChangeCallback.onPageChange(1)
            }
        }
    }

    override fun getItemCount(): Int {
        return payoutData.size
    }

    /**
     * The view holder for credit payout list
     *
     * @property payoutBinding The credit payout list view binding
     */
    inner class CreditPayoutViewHolder(private val payoutBinding: LayoutCreditPayoutItemBinding) :
        RecyclerView.ViewHolder(payoutBinding.root) {

        /**
         * To bind the credit payout details view into Recycler view with given data
         *
         * @param response The credit payout details
         */
        fun bind(response: CreditPayoutData) {
            with(payoutBinding) {
                with(response) {
                    tvOrderNoRepurchase.text = addText(activity, R.string.payout_no, payoutNo ?: "")
                    tvDate.text = createdAt ?: ""
                    tvTotal.text = amount?.let { addText(activity, R.string.price_value, it) } ?: ""

                    tvTransactionType.text = paymentStatus ?: ""
                    tvTransactionType.visibility = if (paymentStatus.isNullOrEmpty()) View.GONE else View.VISIBLE

                    val statusLower = paymentStatus?.lowercase() ?: ""
                    when {
                        statusLower.contains("paid") || statusLower.contains("success") || statusLower.contains("approved") -> {
                            tvTransactionType.setTextColor("#0FCE6E".toColorInt())
                            tvTransactionType.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.transaction_type_success))
                        }
                        statusLower.contains("reject") || statusLower.contains("fail") -> {
                            tvTransactionType.setTextColor("#F51D46".toColorInt())
                            tvTransactionType.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.transaction_type_rejected))
                        }
                        else -> {
                            tvTransactionType.setTextColor("#FFA500".toColorInt())
                            tvTransactionType.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.transaction_type_other))
                        }
                    }

                    tvBankHolderName.text = bankHolderName ?: "-"
                    tvBankName.text = bankName ?: "-"
                    tvAccountNumber.text = acNo ?: "-"
                    tvIfscCode.text = ifscCode ?: "-"
                    tvRemark.text = remark ?: "-"

                    clRepurchase.setOnClickListener {
                        onClickListener.onClick(absoluteAdapterPosition)
                    }
                }
            }
        }
    }
}
