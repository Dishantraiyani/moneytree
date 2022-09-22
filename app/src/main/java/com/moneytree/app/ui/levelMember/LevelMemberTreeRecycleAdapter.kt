package com.moneytree.app.ui.levelMember

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
import com.moneytree.app.databinding.LayoutMemberTreeBinding
import com.moneytree.app.repository.network.responses.NSLevelMemberTreeData
import com.moneytree.app.repository.network.responses.NSMemberTreeData

class LevelMemberTreeRecycleAdapter(
    activityNS: Activity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val memberData: MutableList<NSLevelMemberTreeData> = arrayListOf()

    fun updateData(memberList: MutableList<NSLevelMemberTreeData>) {
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
        val memberView = LayoutLevelMemberItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    inner class NSMemberTreeViewHolder(private val voucherBinding: LayoutLevelMemberItemBinding) :
        RecyclerView.ViewHolder(voucherBinding.root) {

        /**
         * To bind the member tree details view into Recycler view with given data
         *
         * @param response The member tree details
         */
        fun bind(response: NSLevelMemberTreeData) {
            with(voucherBinding) {
                with(response) {
                    tvLevelNo.text = addText(activity, R.string.level_no, levelNo!!)
                    tvActiveMember.text = addText(activity, R.string.active_members, cnt!!)
                    tvDirect.text = addText(activity, R.string.direct, directFifty.toString())
                    tvRepurchase.text = addText(activity, R.string.repurchase_data, repurchase.toString())

					btnView.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							activity.startActivity(Intent(activity, LevelMemberTreeActivity::class.java).putExtra(NSConstants.KEY_MEMBER_LEVEL_NUMBER, levelNo))
						}
					})
                }
            }
        }
    }
}
