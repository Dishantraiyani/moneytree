package com.moneytree.app.ui.royalty

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.callbacks.NSInfoSelectCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.databinding.LayoutRoyaltyItemBinding
import com.moneytree.app.repository.network.responses.NSRoyaltyListData
import com.moneytree.app.repository.network.responses.NSTodayRePurchaseListData

class NSRoyaltyListRecycleAdapter(
    activityNS: Activity,
    onPageChange: NSPageChangeCallback,
    onClick: NSInfoSelectCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val royaltyData: MutableList<NSRoyaltyListData> = arrayListOf()
    private val onPageChangeCallback: NSPageChangeCallback = onPageChange
    private val onClickListener: NSInfoSelectCallback = onClick

    fun updateData(royaltyList: MutableList<NSRoyaltyListData>) {
        royaltyData.addAll(royaltyList)
        notifyItemRangeChanged(0, royaltyData.size - 1)
    }

    fun clearData() {
        royaltyData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val royaltyView = LayoutRoyaltyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSRoyaltyViewHolder(royaltyView)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSRoyaltyViewHolder) {
            val holder: NSRoyaltyViewHolder = holderRec
            holder.bind(royaltyData[holder.absoluteAdapterPosition])
        }

        if (position == royaltyData.size - 1) {
            if (((position + 1) % NSConstants.PAGINATION) == 0) {
                onPageChangeCallback.onPageChange()
            }
        }
    }

    override fun getItemCount(): Int {
        return royaltyData.size
    }

    /**
     * The view holder for trip history list
     *
     * @property royaltyBinding The trip history list view binding
     */
    inner class NSRoyaltyViewHolder(private val royaltyBinding: LayoutRoyaltyItemBinding) :
        RecyclerView.ViewHolder(royaltyBinding.root) {

        /**
         * To bind the order details view into Recycler view with given data
         *
         * @param response The order details
         */
        fun bind(response: NSRoyaltyListData) {
            with(royaltyBinding) {
                with(response) {
                    with(response) {
                        tvPayoutNo.text = addText(activity, R.string.payout_no, payoutNo!!)
                        tvDateFrom.text = addText(activity, R.string.dashboard_data, entryFrom!!)
                        tvDateTo.text = addText(activity, R.string.dashboard_data, entryTo!!)
                        tvAmount.text = addText(activity, R.string.dashboard_data, amount!!)
                        tvTda.text = addText(activity, R.string.dashboard_data, tds!!)
                        tvAdmin.text = addText(activity, R.string.dashboard_data, adminCharges!!)
                        tvTotal.text = addText(activity, R.string.dashboard_data, total!!)
                        tvPercentage.text = addText(activity, R.string.dashboard_data, percentage!!)

                        clRepurchase.setOnClickListener {
                            onClickListener.onClick(absoluteAdapterPosition)
                        }
                    }
                }
            }
        }
    }
}