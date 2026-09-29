package com.moneytree.app.ui.credit.wallet

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.gone
import com.moneytree.app.databinding.LayoutTransactionBinding
import com.moneytree.app.repository.network.responses.CreditWalletData

class CreditWalletListRecycleAdapter(
	activityNS: Activity,
	onPageChange: NSPageChangeCallback,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val walletData: MutableList<CreditWalletData> = arrayListOf()
    private val onPageChangeCallback: NSPageChangeCallback = onPageChange

    fun updateData(walletList: MutableList<CreditWalletData>) {
        walletData.addAll(walletList)
        notifyItemRangeChanged(0, walletData.size - 1)
    }

    fun clearData() {
        walletData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val orderView = LayoutTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CreditWalletViewHolder(orderView)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is CreditWalletViewHolder) {
            val holder: CreditWalletViewHolder = holderRec
            holder.bind(walletData[holder.absoluteAdapterPosition])
        }

        if (position == walletData.size - 1) {
            if (((position + 1) % NSConstants.PAGINATION) == 0) {
                onPageChangeCallback.onPageChange(1)
            }
        }
    }

    override fun getItemCount(): Int {
        return walletData.size
    }

    /**
     * The view holder for credit wallet list
     *
     * @property walletBinding The credit wallet list view binding
     */
    inner class CreditWalletViewHolder(private val walletBinding: LayoutTransactionBinding) :
        RecyclerView.ViewHolder(walletBinding.root) {

        /**
         * To bind the credit wallet details view into RecyclerView with given data
         *
         * @param response The credit wallet details
         */
        fun bind(response: CreditWalletData) {
            with(walletBinding) {
                with(response) {
                    ivNext.gone()
                    tvTransactionId.text = addText(activity, R.string.transfer_id_value, transferId ?: "")
                    
                    val isCreditCheck = entryType?.lowercase() == "credit"
                    val statusColor = if (isCreditCheck) "#0FCE6E".toColorInt() else "#F51D46".toColorInt()
                    
                    tvCreditPrice.setTextColor(statusColor)
                    tvOrderCredit.setTextColor(statusColor)
                    
                    tvDate.text = entryDate
                    tvCreditPrice.text = addText(activity, R.string.price_value, amount?.trim() ?: "0")
                    tvOrderCredit.text = entryType?.trim()?.uppercase()
                    
                    
                    tvTransactionId.text = transferId
                    tvDate.text = entryDate ?: ""
                    tvCreditPrice.text = amount?.let { addText(activity, R.string.price_value, it) } ?: ""

                    tvMemberId.text = status ?: ""
                    tvMemberId.visibility = if (status.isNullOrEmpty()) View.GONE else View.VISIBLE
                }
            }
        }
    }
}
