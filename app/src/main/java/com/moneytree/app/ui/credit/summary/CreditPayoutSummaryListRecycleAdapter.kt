package com.moneytree.app.ui.credit.summary

import android.app.Activity
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.callbacks.NSInfoSelectCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.databinding.LayoutCreditPayoutSummaryItemBinding
import com.moneytree.app.repository.network.responses.SummaryDataItem

class CreditPayoutSummaryListRecycleAdapter(
	activityNS: Activity,
	onPageChange: NSPageChangeCallback,
	onClick: NSInfoSelectCallback,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val payoutData: MutableList<SummaryDataItem> = arrayListOf()
    private val onPageChangeCallback: NSPageChangeCallback = onPageChange
    private val onClickListener: NSInfoSelectCallback = onClick

    fun updateData(payoutList: MutableList<SummaryDataItem>) {
        payoutData.addAll(payoutList)
        notifyItemRangeChanged(0, payoutData.size - 1)
    }

    fun clearData() {
        payoutData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val orderView = LayoutCreditPayoutSummaryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
     * The view holder for credit payout summary list
     *
     * @property payoutBinding The credit payout summary list view binding
     */
    inner class CreditPayoutViewHolder(private val payoutBinding: LayoutCreditPayoutSummaryItemBinding) :
        RecyclerView.ViewHolder(payoutBinding.root) {

        /**
         * To bind the credit payout summary details view into RecyclerView with given data
         *
         * @param response The credit payout summary details
         */
        fun bind(response: SummaryDataItem) {
            with(payoutBinding) {
                with(response) {
                    tvOrderNoRepurchase.text = addText(activity, R.string.payout_no, ovCreditPayoutId ?: "")
                    tvPayoutName.text = payoutName ?: ""
                    tvTotal.text = amount?.let { addText(activity, R.string.price_value, it) } ?: ""
                    tvReleaseDate.text = payoutReleaseDate ?: ""
                    tvDate.text = entryDate ?: ""

                    tvTransactionType.text = status ?: ""
                    val hasStatus = !status.isNullOrEmpty()
                    tvTransactionType.visibility = if (hasStatus) View.VISIBLE else View.GONE

                    val statusLower = status?.lowercase() ?: ""
                    val (textColor, bgTintRes) = when {
                        statusLower.contains("released") || statusLower.contains("success") || statusLower.contains("accepted") || statusLower.contains("approved") -> {
                            Pair("#0FCE6E".toColorInt(), R.color.transaction_type_success)
                        }
                        statusLower.contains("reject") || statusLower.contains("fail") -> {
                            Pair("#F51D46".toColorInt(), R.color.transaction_type_rejected)
                        }
                        else -> {
                            Pair("#FFA500".toColorInt(), R.color.transaction_type_other)
                        }
                    }

                    tvTransactionType.setTextColor(textColor)
                    tvTransactionType.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity, bgTintRes))
                  
                    tvAdminCharge.text = adminCharge?.let { addText(activity, R.string.price_value, it) } ?: "₹0.00"
                    tvNetAmount.text = total?.let { addText(activity, R.string.price_value, it) } ?: "₹0.00"
                    tvCoinAmount.text = coinAmount?.let { addText(activity, R.string.price_value, it) } ?: "₹0.00"

                    clRepurchase.setOnClickListener {
                        onClickListener.onClick(absoluteAdapterPosition)
                    }
                }
            }
        }
    }
}
