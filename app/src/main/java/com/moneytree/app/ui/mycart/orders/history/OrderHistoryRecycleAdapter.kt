package com.moneytree.app.ui.mycart.orders.history

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.callbacks.NSStockHistoryDetailCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.LayoutOrderHistoryItemBinding
import com.moneytree.app.databinding.LayoutRepurchaseStockItemBinding
import com.moneytree.app.repository.network.responses.OrderAddressResponse
import com.moneytree.app.repository.network.responses.OrderHistoryDataItem
import com.moneytree.app.repository.network.responses.RepurchaseDataItem


class OrderHistoryRecycleAdapter(
	activityNS: Activity,
	onPageChange: NSPageChangeCallback,
	val stockItemCallback: (OrderHistoryDataItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val productData: MutableList<OrderHistoryDataItem> = arrayListOf()
    private val onPageChangeCallback: NSPageChangeCallback = onPageChange

    fun updateData(voucherList: MutableList<OrderHistoryDataItem>) {
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
        val voucherView = LayoutOrderHistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    inner class NSProductViewHolder(private val productBinding: LayoutOrderHistoryItemBinding) :
        RecyclerView.ViewHolder(productBinding.root) {

        /**
         * To bind the voucher details view into Recycler view with given data
         *
         * @param response The voucher details
         */
        fun bind(response: OrderHistoryDataItem) {
            with(productBinding) {
                with(response) {
                    tvRepurchaseNo.text = response.fullName
                    tvStockId.text = response.orderNo
                    tvStockType.text = response.orderStatus
                    tvMemberId.text = response.memberId
                    tvDate.text = createdAt
                    tvRemark.text = response.walletType
					tvTotal.text = total?.let { addText(activity, R.string.price_value, it) }
                    tvAddress.text = response.address1
                    if (response.mtCoin?.isNotEmpty() == true) {
                        llMtCoin.visible()
                        tvMtCoin.text = response.mtCoin
                    }

                    if (response.mtCoinTotal?.isNotEmpty() == true) {
                        llMtCoinTotal.visible()
                        tvMtCoinTotal.text = mtCoinTotal?.let { addText(activity, R.string.price_value, it) }
                    }

                    if (response.addressData?.isNotEmpty() == true) {
                        val model: OrderAddressResponse = Gson().fromJson(response.addressData, OrderAddressResponse::class.java)
                        val addressStr = model.flatHouse + ", " + model.area + ", " + model.landmark + ", " + model.city + ", " + model.state + ", " + model.county
                        tvAddress.text = addressStr
                        addressFinal = addressStr
                    }

					clProductLayout.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							stockItemCallback.invoke(response)
						}
					})
                   /* tvDescription.text = description!!
					*/
                }
            }
        }
    }
}
