package com.moneytree.app.ui.recharge

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.LayoutRechargeDetailBinding
import com.moneytree.app.repository.network.responses.NSRechargeFetchListData

class RechargeDetailRecycleAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	private val memberData: MutableList<NSRechargeFetchListData> = arrayListOf()

    fun updateData(memberList: MutableList<NSRechargeFetchListData>) {
		memberData.addAll(memberList)
        if (memberList.isValidList()) {
            notifyItemRangeChanged(0, memberData.size - 1)
        } else {
            notifyDataSetChanged()
        }
    }

    fun clearData() {
        memberData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val memberView = LayoutRechargeDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSMemberTreeViewHolder(memberView)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSMemberTreeViewHolder) {
            val holder: NSMemberTreeViewHolder = holderRec
            holder.bind(memberData[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return memberData.size
    }

    /**
     * The view holder for member tree list
     *
     * @property voucherBinding The member tree list view binding
     */
    inner class NSMemberTreeViewHolder(private val voucherBinding: LayoutRechargeDetailBinding) :
        RecyclerView.ViewHolder(voucherBinding.root) {

        /**
         * To bind the member tree details view into Recycler view with given data
         *
         * @param response The member tree details
         */
        fun bind(response: NSRechargeFetchListData) {
            with(voucherBinding) {
                with(response) {
                    tvAccountTitle.text = title
                    tvAccountValue.text = value
                }
            }
        }
    }
}
