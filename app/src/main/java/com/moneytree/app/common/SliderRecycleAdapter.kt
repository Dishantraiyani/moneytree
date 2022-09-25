package com.moneytree.app.common

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.LayoutLevelMemberItemBinding
import com.moneytree.app.databinding.LayoutMemberTreeBinding
import com.moneytree.app.databinding.RowSliderBinding
import com.moneytree.app.repository.network.responses.NSLevelMemberTreeData
import com.moneytree.app.repository.network.responses.NSMemberTreeData
import com.moneytree.app.ui.levelMember.LevelMemberTreeActivity

class SliderRecycleAdapter(
    activityNS: Activity,
	private var list: MutableList<String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val memberData: MutableList<NSLevelMemberTreeData> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val memberView = RowSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    inner class NSMemberTreeViewHolder(private val voucherBinding: RowSliderBinding) :
        RecyclerView.ViewHolder(voucherBinding.root) {

        /**
         * To bind the member tree details view into Recycler view with given data
         *
         * @param response The member tree details
         */
        fun bind(response: NSLevelMemberTreeData) {
            with(voucherBinding) {
                with(response) {
                    Glide.with(activity).load(list[absoluteAdapterPosition]).into(voucherBinding.image)
                }
            }
        }
    }
}
