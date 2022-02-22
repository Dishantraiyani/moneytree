package com.moneytree.app.ui.wallets.redeem

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

class NSRedeemRecycleAdapter(
    activityNS: Activity,
    onPageChange: NSPageChangeCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val redeemData: MutableList<NSVoucherListData> = arrayListOf()
    private val onPageChangeCallback: NSPageChangeCallback = onPageChange

    fun updateData(redeemList: MutableList<NSVoucherListData>) {
        redeemData.addAll(redeemList)
        if (redeemList.isValidList()) {
            notifyItemRangeChanged(0, redeemData.size - 1)
        } else {
            notifyDataSetChanged()
        }
    }

    fun clearData() {
        redeemData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSViewHolder(view)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSViewHolder) {
            val holder: NSViewHolder = holderRec
            holder.bind(redeemData[holder.absoluteAdapterPosition])
        }

        if (position == redeemData.size - 1) {
            if (((position + 1) % NSConstants.PAGINATION) == 0) {
                onPageChangeCallback.onPageChange()
            }
        }
    }

    override fun getItemCount(): Int {
        return redeemData.size
    }

    /**
     * The view holder for redeem list
     *
     * @property redeemBinding The redeem list view binding
     */
    inner class NSViewHolder(private val redeemBinding: LayoutTransactionBinding) :
        RecyclerView.ViewHolder(redeemBinding.root) {

        /**
         * To bind the redeem details view into Recycler view with given data
         *
         * @param response The redeem details
         */
        fun bind(response: NSVoucherListData) {
            with(redeemBinding) {
                with(response) {
                    tvTransactionId.text = addText(activity, R.string.transaction_id, "1234567890")
                    tvTransactionStatus.text = "Recharge"
                    tvOrderCredit.text = "Debit"
                    tvDate.text = createdAt
                    tvCreditPrice.text = "100".toString()

                    val isCreditCheck = false//isCredit.equals("Credit")
                    tvOrderCredit.setTextColor(if(isCreditCheck) Color.parseColor("#0FCE6E") else Color.parseColor("#E74B3C"))
                    tvCreditPrice.setTextColor(if(isCreditCheck) Color.parseColor("#0FCE6E") else Color.parseColor("#E74B3C"))
                }
            }
        }
    }
}