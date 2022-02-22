package com.moneytree.app.ui.slots

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
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
    private var isComplete = false

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

                    with(activity.resources){
                        when {
                            absoluteAdapterPosition == 0 -> {
                                isComplete = true
                                clSlot.setBackgroundColor(Color.parseColor(getString(R.string.color1)))
                                ivSlotUser.setImageResource(R.drawable.ic_slot_user)
                            }
                            isComplete -> {
                                isComplete = false
                                clSlot.setBackgroundColor(Color.parseColor(getString(R.string.color2)))
                                ivSlotUser.setImageResource(R.drawable.ic_add_sec)
                            }
                            else -> {
                                clSlot.setBackgroundColor(Color.parseColor(getString(R.string.color3)))
                                ivSlotUser.setImageResource(R.drawable.ic_shopping_cart)
                            }
                        }
                        setPoint(checkEntry!!.toInt(), view1, view2, view3)
                    }
                }
            }
        }
    }

    private fun setPoint(position: Int, view1: View, view2: View, view3: View) {
        if (position > 0) {
            view1.setBackgroundResource(R.drawable.green_circle)
        }
        if (position > 1) {
            view2.setBackgroundResource(R.drawable.green_circle)
        }
        if (position > 2) {
            view3.setBackgroundResource(R.drawable.green_circle)
        }
    }
}