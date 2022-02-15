package com.moneytree.app.ui.transaction

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.utils.addText
import com.moneytree.app.databinding.LayoutTransactionBinding
import com.moneytree.app.repository.network.responses.NSTransactionListData

class TransactionRecycleAdapter(
    activityNS: Activity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val transactionListData: MutableList<NSTransactionListData> = arrayListOf()

    fun updateData(transactionList: MutableList<NSTransactionListData>) {
        transactionListData.addAll(transactionList)
        notifyItemRangeChanged(0, transactionListData.size - 1)
    }

    fun clearData() {
        transactionListData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val orderView = LayoutTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSTransactionViewHolder(orderView)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSTransactionViewHolder) {
            val holder: NSTransactionViewHolder = holderRec
            holder.bind(transactionListData[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return transactionListData.size
    }

    /**
     * The view holder for order list
     *
     * @property orderBinding The order list view binding
     */
    inner class NSTransactionViewHolder(private val orderBinding: LayoutTransactionBinding) :
        RecyclerView.ViewHolder(orderBinding.root) {

        /**
         * To bind the order details view into Recycler view with given data
         *
         * @param response The order details
         */
        fun bind(response: NSTransactionListData) {
            with(orderBinding) {
                with(response) {
                    tvShopOrderId.text = addText(activity, R.string.shop_order_id_title, orderId!!)
                    tvOrderCredit.text = isCredit
                    tvDate.text = createdAt
                    tvCreditPrice.text = total.toString()

                    val isCreditCheck = isCredit.equals("Credit")
                    tvOrderCredit.setTextColor(if(isCreditCheck) Color.parseColor("#0FCE6E") else Color.parseColor("#E74B3C"))
                    tvCreditPrice.setTextColor(if(isCreditCheck) Color.parseColor("#0FCE6E") else Color.parseColor("#E74B3C"))
                }
            }
        }
    }
}