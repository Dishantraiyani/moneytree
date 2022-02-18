package com.moneytree.app.ui.repurchase

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.callbacks.NSInfoSelectCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.databinding.LayoutRepurchaseItemBinding
import com.moneytree.app.repository.network.responses.NSTodayRePurchaseListData

class NSRePurchaseListRecycleAdapter(
    activityNS: Activity,
    onPageChange: NSPageChangeCallback,
    onClick: NSInfoSelectCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val repurchaseData: MutableList<NSTodayRePurchaseListData> = arrayListOf()
    private val onPageChangeCallback: NSPageChangeCallback = onPageChange
    private val onClickListener: NSInfoSelectCallback = onClick

    fun updateData(repurchaseList: MutableList<NSTodayRePurchaseListData>) {
        repurchaseData.addAll(repurchaseList)
        notifyItemRangeChanged(0, repurchaseData.size - 1)
    }

    fun clearData() {
        repurchaseData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val orderView = LayoutRepurchaseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSRePurchaseViewHolder(orderView)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSRePurchaseViewHolder) {
            val holder: NSRePurchaseViewHolder = holderRec
            holder.bind(repurchaseData[holder.absoluteAdapterPosition])
        }

        if (position == repurchaseData.size - 1) {
            if (((position + 1) % NSConstants.PAGINATION) == 0) {
                onPageChangeCallback.onPageChange()
            }
        }
    }

    override fun getItemCount(): Int {
        return repurchaseData.size
    }

    /**
     * The view holder for repurchase list
     *
     * @property rePurchaseBinding The repurchase list view binding
     */
    inner class NSRePurchaseViewHolder(private val rePurchaseBinding: LayoutRepurchaseItemBinding) :
        RecyclerView.ViewHolder(rePurchaseBinding.root) {

        /**
         * To bind the repurchase details view into Recycler view with given data
         *
         * @param response The repurchase details
         */
        fun bind(response: NSTodayRePurchaseListData) {
            with(rePurchaseBinding) {
                with(response) {
                    tvOrderNoRepurchase.text = addText(activity, R.string.order_no, repurchaseNo!!)
                    tvMemberId.text = addText(activity, R.string.member_id, memberid!!)
                    tvTotal.text = addText(activity, R.string.dashboard_data, total!!)
                    tvDate.text = addText(activity, R.string.voucher_date, createdAt!!)

                    clRepurchase.setOnClickListener {
                        onClickListener.onClick(absoluteAdapterPosition)
                    }
                }
            }
        }
    }
}