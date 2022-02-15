package com.moneytree.app.ui.retail

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.callbacks.NSInfoSelectCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.databinding.LayoutRetailItemBinding
import com.moneytree.app.repository.network.responses.NSRetailListData
import com.moneytree.app.repository.network.responses.NSTodayRePurchaseListData

class NSRetailListRecycleAdapter(
    activityNS: Activity,
    onPageChange: NSPageChangeCallback,
    onClick: NSInfoSelectCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val retailData: MutableList<NSRetailListData> = arrayListOf()
    private val onPageChangeCallback: NSPageChangeCallback = onPageChange
    private val onClickListener: NSInfoSelectCallback = onClick

    fun updateData(retailList: MutableList<NSRetailListData>) {
        retailData.addAll(retailList)
        notifyItemRangeChanged(0, retailData.size - 1)
    }

    fun clearData() {
        retailData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val retailView = LayoutRetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSRetailViewHolder(retailView)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSRetailViewHolder) {
            val holder: NSRetailViewHolder = holderRec
            holder.bind(retailData[holder.absoluteAdapterPosition])
        }

        if (position == retailData.size - 1) {
            if (((position + 1) % NSConstants.PAGINATION) == 0) {
                onPageChangeCallback.onPageChange()
            }
        }
    }

    override fun getItemCount(): Int {
        return retailData.size
    }

    /**
     * The view holder for trip history list
     *
     * @property retailBinding The trip history list view binding
     */
    inner class NSRetailViewHolder(private val retailBinding: LayoutRetailItemBinding) :
        RecyclerView.ViewHolder(retailBinding.root) {

        /**
         * To bind the order details view into Recycler view with given data
         *
         * @param response The order details
         */
        fun bind(response: NSRetailListData) {
            with(retailBinding) {
                with(response) {
                    with(response) {
                        tvPayoutNo.text = addText(activity, R.string.payout_no, payoutNo!!)
                        tvDateFrom.text = addText(activity, R.string.dashboard_data, entryFrom!!)
                        tvDateTo.text = addText(activity, R.string.dashboard_data, entryTo!!)
                        tvAmount.text = addText(activity, R.string.dashboard_data, amount!!)
                        tvTda.text = addText(activity, R.string.dashboard_data, tds!!)
                        tvAdmin.text = addText(activity, R.string.dashboard_data, adminCharges!!)
                        tvTotal.text = addText(activity, R.string.dashboard_data, total!!)

                        clRetail.setOnClickListener {
                            onClickListener.onClick(absoluteAdapterPosition)
                        }
                    }
                }
            }
        }
    }
}