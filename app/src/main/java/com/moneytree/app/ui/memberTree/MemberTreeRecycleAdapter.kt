package com.moneytree.app.ui.memberTree

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.LayoutMemberTreeBinding
import com.moneytree.app.databinding.LayoutUpLineMemberBinding
import com.moneytree.app.repository.network.responses.NSMemberTreeData
import com.moneytree.app.repository.network.responses.NSUpLineData

class MemberTreeRecycleAdapter(
    activityNS: Activity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val memberData: MutableList<NSUpLineData> = arrayListOf()

    fun updateData(memberList: MutableList<NSUpLineData>) {
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
        val memberView = LayoutUpLineMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    inner class NSMemberTreeViewHolder(private val voucherBinding: LayoutUpLineMemberBinding) :
        RecyclerView.ViewHolder(voucherBinding.root) {

        /**
         * To bind the member tree details view into Recycler view with given data
         *
         * @param response The member tree details
         */
        fun bind(response: NSUpLineData) {
            with(voucherBinding) {
                with(response) {
                    tvMemberId.text = earnedId
                    tvLevelNo.text = levelNo
                    tvIsActive.text = isActive
                }
            }
        }
    }
}
