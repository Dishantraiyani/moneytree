package com.moneytree.app.ui.retailInfo

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.databinding.LayoutRetailInfoItemBinding
import com.moneytree.app.repository.network.responses.NSRetailInfoData

class NSRetailInfoRecycleAdapter(
    activityNS: Activity,
    onPageChange: NSPageChangeCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val retailData: MutableList<NSRetailInfoData> = arrayListOf()
    private val onPageChangeCallback: NSPageChangeCallback = onPageChange

    fun updateData(retailList: MutableList<NSRetailInfoData>) {
        retailData.addAll(retailList)
        notifyItemRangeChanged(0, retailData.size - 1)
    }

    fun clearData() {
        retailData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val retailView = LayoutRetailInfoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    inner class NSRetailViewHolder(private val retailBinding: LayoutRetailInfoItemBinding) :
        RecyclerView.ViewHolder(retailBinding.root) {

        /**
         * To bind the order details view into Recycler view with given data
         *
         * @param response The order details
         */
        fun bind(response: NSRetailInfoData) {
            with(retailBinding) {
                with(response) {
                    tvMemberId.text = addText(activity, R.string.member_id, repurchaseMemberid!!)
                    tvLevel.text = level
                    tvAmount.text = addText(activity, R.string.dashboard_data, amount!!)
                    tvPercentage.text = percentage
                }
            }
        }
    }
}