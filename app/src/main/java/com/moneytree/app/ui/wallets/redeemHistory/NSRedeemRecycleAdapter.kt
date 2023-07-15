package com.moneytree.app.ui.wallets.redeemHistory

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.LayoutRedeemBinding
import com.moneytree.app.repository.network.responses.NSWalletRedeemData

class NSRedeemRecycleAdapter(
    activityNS: Activity,
    onPageChange: NSPageChangeCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val redeemData: MutableList<NSWalletRedeemData> = arrayListOf()
    private val onPageChangeCallback: NSPageChangeCallback = onPageChange

    fun updateData(redeemList: MutableList<NSWalletRedeemData>) {
        redeemData.addAll(redeemList)
        if (redeemList.isValidList()) {
            notifyItemRangeChanged(0, redeemData.size - 1)
        } else {
            notifyDataSetChanged()
        }
    }

    fun clearData() {
        redeemData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutRedeemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSViewHolder(view)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSViewHolder) {
            val holder: NSViewHolder = holderRec
            holder.bind(redeemData[holder.absoluteAdapterPosition])
        }

        if (position == redeemData.size - 1) {
            if (((position + 1) % NSConstants.PAGINATION) == 0) {
                onPageChangeCallback.onPageChange(1)
            }
        }
    }

    override fun getItemCount(): Int {
        return redeemData.size
    }

    /**
     * The view holder for redeem list
     *
     * @property redeemBinding The redeem list view binding
     */
    inner class NSViewHolder(private val redeemBinding: LayoutRedeemBinding) :
        RecyclerView.ViewHolder(redeemBinding.root) {

        /**
         * To bind the redeem details view into Recycler view with given data
         *
         * @param response The redeem details
         */
        fun bind(response: NSWalletRedeemData) {
            with(redeemBinding) {
                with(response) {
                    tvEntryDate.text = addText(activity, R.string.entry_date, createdAt!!)
                    tvAmount.text = amount
                    tvAdmin.text = adminCharges
                    tvTds.text = tdsCharges
                    tvTotal.text = total
                }
            }
        }
    }
}
