package com.moneytree.app.ui.memberTree

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
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
                    setImage(ivSlot2, response.slot2 != null && response.slot2 != false)
                    setImage(ivSlot3, response.slot3 != null && response.slot3 != false)
                    setImage(ivSlot4, response.slot4 != null && response.slot4 != false)
                    setImage(ivSlot5, response.slot5 != null && response.slot5 != false)
                    setImage(ivSlot6, response.slot6 != null && response.slot6 != false)
                    setImage(ivSlot7, response.slot7 != null && response.slot7 != false)
                    setImage(ivSlot8, response.slot8 != null && response.slot8 != false)
                    setImage(ivSlot9, response.slot9 != null && response.slot9 != false)
                    setImage(ivSlot10, response.slot10 != null && response.slot10 != false)
                    setImage(ivSlot11, response.slot11 != null && response.slot11 != false)
                    setImage(ivSlot12, response.slot12 != null && response.slot12 != false)
                }
            }
        }
    }

    private fun setImage(ivTick: ImageView, isEnable: Boolean) {
        ivTick.setImageResource(if (isEnable) R.drawable.correct else R.drawable.ic_close_s)
        ivTick.setPadding(if(!isEnable) 8 else 0)
    }
}