package com.moneytree.app.ui.mycart.history

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.callbacks.NSStockHistoryDetailCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.databinding.LayoutRepurchaseStockItemBinding
import com.moneytree.app.repository.network.responses.RepurchaseDataItem


class RSHistoryRecycleAdapter(
	activityNS: Activity,
	val isStock: Boolean,
	onPageChange: NSPageChangeCallback,
	val stockItemCallback: NSStockHistoryDetailCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val productData: MutableList<RepurchaseDataItem> = arrayListOf()
    private val onPageChangeCallback: NSPageChangeCallback = onPageChange

    fun updateData(voucherList: MutableList<RepurchaseDataItem>) {
        productData.addAll(voucherList)
        if (voucherList.isValidList()) {
            notifyItemRangeChanged(0, productData.size - 1)
        } else {
            notifyDataSetChanged()
        }
    }

    fun clearData() {
        productData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val voucherView = LayoutRepurchaseStockItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSProductViewHolder(voucherView)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSProductViewHolder) {
            val holder: NSProductViewHolder = holderRec
            holder.bind(productData[holder.absoluteAdapterPosition])
        }

        if (position == productData.size - 1) {
            if (((position + 1) % NSConstants.PAGINATION) == 0) {
                onPageChangeCallback.onPageChange(1)
            }
        }
    }

    override fun getItemCount(): Int {
        return productData.size
    }

    /**
     * The view holder for voucher list
     *
     * @property productBinding The voucher list view binding
     */
    inner class NSProductViewHolder(private val productBinding: LayoutRepurchaseStockItemBinding) :
        RecyclerView.ViewHolder(productBinding.root) {

        /**
         * To bind the voucher details view into Recycler view with given data
         *
         * @param response The voucher details
         */
        fun bind(response: RepurchaseDataItem) {
            with(productBinding) {
                with(response) {
					if (isStock) {
						tvRepurchaseNo.text = response.orderNo
						tvMemberIdTitle.text = activity.resources.getString(R.string.stockiest_to)
						tvMemberId.text = response.stockiestTo
						llStockType.gone()
					} else {
						tvRepurchaseNo.text = response.repurchaseNo
						tvMemberId.text = response.memberid
						tvStockType.text = response.stockiestType
					}

                    tvDate.text = createdAt
                    tvRemark.text = remark
	                tvTotal.text = total?.let { addText(activity, R.string.price_value, it) }
	                tvStockId.text = response.stockiestid
	                
	                paymentSummary?.let { summary ->
		                llPaymentSummary.visibility = View.VISIBLE
		                tvPaymentMode.text = summary.paymentMode ?: "-"
		                tvUsedFrom.text = summary.usedFrom ?: "-"
		                
		                val voucherAmount = summary.voucherAmount?.toDoubleOrNull()?:0.0
		                llVoucherAmount.setVisibility(voucherAmount > 0)
		                tvVoucherAmount.text = summary.voucherAmount?.let { addText(activity, R.string.price_value, it) } ?: "₹0"
		                
		                llWalletAmount.setVisibility((summary.walletAmount?.toDoubleOrNull()?:0.0) > 0)
		                tvWalletAmount.text = summary.walletAmount?.let { addText(activity, R.string.price_value, it) } ?: "₹0"
		                
		                llOvCreditAmount.setVisibility((summary.ovCreditAmount?.toDoubleOrNull()?:0.0) > 0)
		                tvOvCreditAmount.text = summary.ovCreditAmount?.let { addText(activity, R.string.price_value, it) } ?: "₹0"
		                
		                llCashAmount.setVisibility((summary.cashAmount?.toDoubleOrNull()?:0.0) > 0)
		                tvCashAmount.text = summary.cashAmount?.let { addText(activity, R.string.price_value, it) } ?: "₹0"

                        llPaymentSummaryDetails.visibility = if (response.isExpanded) View.VISIBLE else View.GONE
                        ivPaymentSummaryArrow.rotation = if (response.isExpanded) 180f else 0f

                        llPaymentSummaryHeader.setOnClickListener {
                            response.isExpanded = !response.isExpanded
                            llPaymentSummaryDetails.visibility = if (response.isExpanded) View.VISIBLE else View.GONE
                            ivPaymentSummaryArrow.animate().rotation(if (response.isExpanded) 180f else 0f).setDuration(200).start()
                        }
	                } ?: run {
		                llPaymentSummary.visibility = View.GONE
	                }

					clProductLayout.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							stockItemCallback.onResponse(response)
						}
					})
                }
            }
        }
    }
}
