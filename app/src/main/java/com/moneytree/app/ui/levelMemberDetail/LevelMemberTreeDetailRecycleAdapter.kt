package com.moneytree.app.ui.levelMemberDetail

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.LayoutLevelMemberItemBinding
import com.moneytree.app.databinding.LayoutLevelMemberItemDetailBinding
import com.moneytree.app.databinding.LayoutMemberTreeBinding
import com.moneytree.app.repository.network.responses.NSLevelMemberTreeData
import com.moneytree.app.repository.network.responses.NSLevelMemberTreeDetail
import com.moneytree.app.repository.network.responses.NSMemberTreeData
import com.moneytree.app.ui.levelMember.LevelMemberTreeActivity

class LevelMemberTreeDetailRecycleAdapter(
    activityNS: Activity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val memberData: MutableList<NSLevelMemberTreeDetail> = arrayListOf()

    fun updateData(memberList: MutableList<NSLevelMemberTreeDetail>) {
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
        val memberView = LayoutLevelMemberItemDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    inner class NSMemberTreeViewHolder(private val voucherBinding: LayoutLevelMemberItemDetailBinding) :
        RecyclerView.ViewHolder(voucherBinding.root) {

        /**
         * To bind the member tree details view into Recycler view with given data
         *
         * @param response The member tree details
         */
        fun bind(response: NSLevelMemberTreeDetail) {
            with(voucherBinding) {
                with(response) {
                    tvLevelNo.text = addText(activity, R.string.level_no, levelNo!!)
					tvMemberId.text = memberId
					tvMemberName.text = fullName
					tvDirectSponsor.text = directSponsor.toString()
					tvRoyaltyRank.text = royaltyName
					tvSponsorId.text = sponsorId
                    tvRepurchase.text = repurchase.toString()

                }
            }
        }
    }
}
