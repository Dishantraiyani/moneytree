package com.moneytree.app.ui.wallets.transaction

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.LayoutTransactionBinding
import com.moneytree.app.repository.network.responses.NSVoucherListData
import com.moneytree.app.repository.network.responses.NSWalletData

class NSTransactionRecycleAdapter(
    activityNS: Activity,
    onPageChange: NSPageChangeCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val transactionData: MutableList<NSWalletData> = arrayListOf()
    private val onPageChangeCallback: NSPageChangeCallback = onPageChange

    fun updateData(transactionList: MutableList<NSWalletData>) {
        transactionData.addAll(transactionList)
        if (transactionList.isValidList()) {
            notifyItemRangeChanged(0, transactionData.size - 1)
        } else {
            notifyDataSetChanged()
        }
    }

    fun clearData() {
        transactionData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSViewHolder(view)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSViewHolder) {
            val holder: NSViewHolder = holderRec
            holder.bind(transactionData[holder.absoluteAdapterPosition])
        }

        if (position == transactionData.size - 1) {
            if (((position + 1) % NSConstants.PAGINATION) == 0) {
                onPageChangeCallback.onPageChange(1)
            }
        }
    }

    override fun getItemCount(): Int {
        return transactionData.size
    }

    /**
     * The view holder for transaction list
     *
     * @property transactionBinding The transaction list view binding
     */
    inner class NSViewHolder(private val transactionBinding: LayoutTransactionBinding) :
        RecyclerView.ViewHolder(transactionBinding.root) {

        /**
         * To bind the voucher details view into Recycler view with given data
         *
         * @param response The voucher details
         */
        fun bind(response: NSWalletData) {
            with(transactionBinding) {
                with(response) {
                    tvTransactionId.text = addText(activity, R.string.transaction_id, transferid!!)
                    tvTransactionStatus.text = status!!
                    tvOrderCredit.text = entryType!!.trim()
                    tvDate.text = entryDate
                    tvCreditPrice.text = amount!!.trim()

                    val isCreditCheck = entryType!!.lowercase() == "credit"
                    tvOrderCredit.setTextColor(if(isCreditCheck) Color.parseColor("#0FCE6E") else Color.parseColor("#E74B3C"))
                    tvCreditPrice.setTextColor(if(isCreditCheck) Color.parseColor("#0FCE6E") else Color.parseColor("#E74B3C"))
                }
            }
        }
    }
}