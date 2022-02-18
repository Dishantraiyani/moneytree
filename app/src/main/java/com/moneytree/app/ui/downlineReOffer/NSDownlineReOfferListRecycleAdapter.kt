package com.moneytree.app.ui.downlineReOffer

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.LayoutDownlineItemBinding
import com.moneytree.app.repository.network.responses.NSDownlineMemberDirectReOfferData

class NSDownlineReOfferListRecycleAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val downlineData: MutableList<NSDownlineMemberDirectReOfferData> = arrayListOf()

    fun updateData(downlineList: MutableList<NSDownlineMemberDirectReOfferData>) {
        downlineData.addAll(downlineList)
        if (downlineList.isValidList()) {
            notifyItemRangeChanged(0, downlineData.size - 1)
        } else {
            notifyDataSetChanged()
        }
    }

    fun clearData() {
        downlineData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val downlineView = LayoutDownlineItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSDownlineViewHolder(downlineView)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSDownlineViewHolder) {
            val holder: NSDownlineViewHolder = holderRec
            holder.bind(downlineData[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return downlineData.size
    }

    /**
     * The view holder for downline list
     *
     * @property downlineBinding The downline list view binding
     */
    inner class NSDownlineViewHolder(private val downlineBinding: LayoutDownlineItemBinding) :
        RecyclerView.ViewHolder(downlineBinding.root) {

        /**
         * To bind the downline details view into Recycler view with given data
         *
         * @param response The downline details
         */
        fun bind(response: NSDownlineMemberDirectReOfferData) {
            with(downlineBinding) {
                with(response) {
                    tvMemberId.text = memberid
                    tvStatus.text = status
                    tvMemberId.setTextColor(Color.parseColor(colour))
                    tvStatus.setTextColor(Color.parseColor(colour))
                }
            }
        }
    }
}