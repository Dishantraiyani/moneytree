package com.moneytree.app.ui.slots

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.LayoutSlotItemBinding
import com.moneytree.app.repository.network.responses.NSSlotListData

class NSSlotListRecycleAdapter(
    activityNS: Activity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val slotData: MutableList<NSSlotListData> = arrayListOf()

    fun updateData(slotList: MutableList<NSSlotListData>) {
        slotData.addAll(slotList)
        if (slotList.isValidList()) {
            notifyItemRangeChanged(0, slotData.size - 1)
        } else {
            notifyDataSetChanged()
        }
    }

    fun clearData() {
        slotData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutSlotItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSViewHolder(view)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSViewHolder) {
            val holder: NSViewHolder = holderRec
            holder.bind(slotData[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return slotData.size
    }

    /**
     * The view holder for slot list
     *
     * @property slotBinding The slot list view binding
     */
    inner class NSViewHolder(private val slotBinding: LayoutSlotItemBinding) :
        RecyclerView.ViewHolder(slotBinding.root) {

        /**
         * To bind the slot details view into Recycler view with given data
         *
         * @param response The slot details
         */
        fun bind(response: NSSlotListData) {
            with(slotBinding) {
                with(response) {
                    tvSlotsMrp.text = addText(activity, R.string.slots_mrp, mrp!!)
                    tvSlotCrn.text = cnt
                    tvSlotEntry.text = checkEntry
                }
            }
        }
    }
}