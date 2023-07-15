package com.moneytree.app.ui.repurchaseInfo

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.databinding.LayoutRepurchaseInfoItemBinding
import com.moneytree.app.repository.network.responses.NSRePurchaseInfoData

class NSRePurchaseInfoRecycleAdapter(
    activityNS: Activity,
    onPageChange: NSPageChangeCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val repurchaseData: MutableList<NSRePurchaseInfoData> = arrayListOf()
    private val onPageChangeCallback: NSPageChangeCallback = onPageChange

    fun updateData(repurchaseList: MutableList<NSRePurchaseInfoData>) {
        repurchaseData.addAll(repurchaseList)
        notifyItemRangeChanged(0, repurchaseData.size - 1)
    }

    fun clearData() {
        repurchaseData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val repurchaseView = LayoutRepurchaseInfoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSRePurchaseViewHolder(repurchaseView)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSRePurchaseViewHolder) {
            val holder: NSRePurchaseViewHolder = holderRec
            holder.bind(repurchaseData[holder.absoluteAdapterPosition])
        }

        if (position == repurchaseData.size - 1) {
            if (((position + 1) % NSConstants.PAGINATION) == 0) {
                onPageChangeCallback.onPageChange(1)
            }
        }
    }

    override fun getItemCount(): Int {
        return repurchaseData.size
    }

    /**
     * The view holder for repurchase list
     *
     * @property rePurchaseBinding The repurchase list view binding
     */
    inner class NSRePurchaseViewHolder(private val rePurchaseBinding: LayoutRepurchaseInfoItemBinding) :
        RecyclerView.ViewHolder(rePurchaseBinding.root) {

        /**
         * To bind the repurchase details view into Recycler view with given data
         *
         * @param response The repurchase details
         */
        fun bind(response: NSRePurchaseInfoData) {
            with(rePurchaseBinding) {
                with(response) {
                    tvProductName.text = addText(activity, R.string.product_name, productName!!)
                    tvQty.text = addText(activity, R.string.qty_title, qty!!)
                    tvAmount.text = addText(activity, R.string.dashboard_data, amount!!)
                    tvRate.text = addText(activity, R.string.rate_title, rate!!)
                }
            }
        }
    }
}