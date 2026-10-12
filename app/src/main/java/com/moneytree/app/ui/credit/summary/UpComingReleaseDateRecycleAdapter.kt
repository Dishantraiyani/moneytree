package com.moneytree.app.ui.credit.summary

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.utils.addText
import com.moneytree.app.databinding.LayoutUpcomingReleaseBinding
import com.moneytree.app.repository.network.responses.ReleaseDatesItem
import java.util.Locale

class UpComingReleaseDateRecycleAdapter(
	activityNS: Activity,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val payoutData: MutableList<ReleaseDatesItem> = arrayListOf()

    fun updateData(payoutList: List<ReleaseDatesItem>) {
        payoutData.addAll(payoutList)
        notifyItemRangeChanged(0, payoutData.size - 1)
    }

    fun clearData() {
        payoutData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val orderView = LayoutUpcomingReleaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CreditPayoutViewHolder(orderView)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is CreditPayoutViewHolder) {
            val holder: CreditPayoutViewHolder = holderRec
            holder.bind(payoutData[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return payoutData.size
    }

    /**
     * The view holder for credit payout summary list
     *
     * @property payoutBinding The credit payout summary list view binding
     */
    inner class CreditPayoutViewHolder(private val payoutBinding: LayoutUpcomingReleaseBinding) :
        RecyclerView.ViewHolder(payoutBinding.root) {

        /**
         * To bind the credit payout summary details view into RecyclerView with given data
         *
         * @param response The credit payout summary details
         */
        fun bind(response: ReleaseDatesItem) {
            with(payoutBinding) {
                with(response) {
                    tvTotalRequestTitle.text = payoutReleaseDate
                    tvTotalPayout.text = netAmount?.let { addText(activity, R.string.price_value, String.format(Locale.ENGLISH, "%.2f", it.toDoubleOrNull()?:0.0)) } ?: ""
                    
                    val totalCnt = (cnt?:"0").toIntOrNull()?:0
                    val adminCharge = "₹${coinAmount?:"0"} OV Coin"
                    val payoutValue = "$totalCnt payout(s)"
                    val payoutDetail = "$payoutValue • $adminCharge"
                    tvPayoutDetail.text = payoutDetail
                }
            }
        }
    }
}
