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
import com.moneytree.app.repository.network.responses.NSMemberTreeData

class MemberTreeRecycleAdapter(
    activityNS: Activity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val memberData: MutableList<NSMemberTreeData> = arrayListOf()

    fun updateData(memberList: MutableList<NSMemberTreeData>) {
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
        val orderView = LayoutMemberTreeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSMemberTreeViewHolder(orderView)
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
     * The view holder for trip history list
     *
     * @property voucherBinding The trip history list view binding
     */
    inner class NSMemberTreeViewHolder(private val voucherBinding: LayoutMemberTreeBinding) :
        RecyclerView.ViewHolder(voucherBinding.root) {

        /**
         * To bind the order details view into Recycler view with given data
         *
         * @param response The order details
         */
        fun bind(response: NSMemberTreeData) {
            with(voucherBinding) {
                with(response) {
                    tvMemberId.text = addText(activity, R.string.member_id, memberid!!)
                    setImage(ivSlot1, true)
                    setImage(ivSlot2, slot2 != null && slot2 != false)
                    setImage(ivSlot3, slot3 != null && slot3 != false)
                    setImage(ivSlot4, slot4 != null && slot4 != false)
                    setImage(ivSlot5, slot5 != null && slot5 != false)
                    setImage(ivSlot6, slot6 != null && slot6 != false)
                    setImage(ivSlot7, slot7 != null && slot7 != false)
                    setImage(ivSlot8, slot8 != null && slot8 != false)
                    setImage(ivSlot9, slot9 != null && slot9 != false)
                    setImage(ivSlot10, slot10 != null && slot10 != false)
                    setImage(ivSlot11, slot11 != null && slot11 != false)
                    setImage(ivSlot12, slot12 != null && slot12 != false)
                }
            }
        }
    }

    private fun setImage(ivTick: ImageView, isEnable: Boolean) {
        ivTick.setImageResource(if (isEnable) R.drawable.correct else R.drawable.ic_close_s)
        ivTick.setPadding(if(!isEnable) 8 else 0)
    }
}