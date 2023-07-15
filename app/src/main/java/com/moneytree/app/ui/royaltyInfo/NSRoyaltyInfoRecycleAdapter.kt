package com.moneytree.app.ui.royaltyInfo

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.databinding.LayoutRoyaltyInfoItemBinding
import com.moneytree.app.repository.network.responses.NSRoyaltyInfoData

class NSRoyaltyInfoRecycleAdapter(
    activityNS: Activity,
    onPageChange: NSPageChangeCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val royaltyData: MutableList<NSRoyaltyInfoData> = arrayListOf()
    private val onPageChangeCallback: NSPageChangeCallback = onPageChange

    fun updateData(royaltyList: MutableList<NSRoyaltyInfoData>) {
        royaltyData.addAll(royaltyList)
        notifyItemRangeChanged(0, royaltyData.size - 1)
    }

    fun clearData() {
        royaltyData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val royalView = LayoutRoyaltyInfoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSRoyaltyViewHolder(royalView)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSRoyaltyViewHolder) {
            val holder: NSRoyaltyViewHolder = holderRec
            holder.bind(royaltyData[holder.absoluteAdapterPosition])
        }

        if (position == royaltyData.size - 1) {
            if (((position + 1) % NSConstants.PAGINATION) == 0) {
                onPageChangeCallback.onPageChange(1)
            }
        }
    }

    override fun getItemCount(): Int {
        return royaltyData.size
    }

    /**
     * The view holder for royalty list
     *
     * @property royaltyBinding The royalty list view binding
     */
    inner class NSRoyaltyViewHolder(private val royaltyBinding: LayoutRoyaltyInfoItemBinding) :
        RecyclerView.ViewHolder(royaltyBinding.root) {

        /**
         * To bind the royalty details view into Recycler view with given data
         *
         * @param response The royalty details
         */
        fun bind(response: NSRoyaltyInfoData) {
            with(royaltyBinding) {
                with(response) {
                    tvRepurchaseNo.text = addText(activity, R.string.repurchase_no, repurchaseNo!!)
                    tvMemberId.text = addText(activity, R.string.member_id, repurchaseMemberId!!)
                    tvAmount.text = addText(activity, R.string.dashboard_data, amount!!)
                }
            }
        }
    }
}