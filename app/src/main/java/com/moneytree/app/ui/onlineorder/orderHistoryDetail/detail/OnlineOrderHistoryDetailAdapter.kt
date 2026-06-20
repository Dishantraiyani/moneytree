package com.moneytree.app.ui.onlineorder.orderHistoryDetail.detail

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.LayoutOnlineOrderHistoryInfoItemBinding
import com.moneytree.app.databinding.LayoutOrderHistoryInfoItemBinding
import com.moneytree.app.repository.network.responses.OrderInfoDataItem

class OnlineOrderHistoryDetailAdapter(
    activityNS: Activity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val stockData: MutableList<OrderInfoDataItem> = arrayListOf()

    fun updateData(memberList: MutableList<OrderInfoDataItem>) {
        stockData.addAll(memberList)
        if (memberList.isValidList()) {
            notifyItemRangeChanged(0, stockData.size - 1)
        } else {
            notifyDataSetChanged()
        }
    }

    fun clearData() {
        stockData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val memberView = LayoutOnlineOrderHistoryInfoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSMemberTreeViewHolder(memberView)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSMemberTreeViewHolder) {
            val holder: NSMemberTreeViewHolder = holderRec
            holder.bind(stockData[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return stockData.size
    }

    /**
     * The view holder for member tree list
     *
     * @property voucherBinding The member tree list view binding
     */
    inner class NSMemberTreeViewHolder(private val voucherBinding: LayoutOnlineOrderHistoryInfoItemBinding) :
        RecyclerView.ViewHolder(voucherBinding.root) {

        /**
         * To bind the member tree details view into Recycler view with given data
         *
         * @param response The member tree details
         */
        fun bind(response: OrderInfoDataItem) {
            with(voucherBinding) {
                with(response) {
					ivNext.gone()
                    tvRepurchaseNo.text = response.productName
                    tvStockId.text = response.productId

					//Quantity
					tvStockTypeTitle.text = activity.resources.getString(R.string.qty_title_new)
					tvStockType.text = response.qty

					//Rate
					tvMemberIdTitle.text = activity.resources.getString(R.string.rate_title_new)
					tvMemberId.text = response.rate

					//Amount
					tvTotalTitle.text = activity.resources.getString(R.string.amount_title)
					tvTotal.text = amount?.let { addText(activity, R.string.price_value, it) }

					tvDate.text = createdAt
                    llDate.setVisibility(!createdAt.isNullOrEmpty())

					llRemark.gone()

                    if (response.mtCoin?.isNotEmpty() == true) {
                        llMtCoin.visible()
                        tvMtCoin.text = response.mtCoin
                    }
                    if (response.mtCoinTotal?.isNotEmpty() == true) {
                        llMtCoinTotal.visible()
                        tvMtCoinTotal.text = mtCoinTotal?.let { addText(activity, R.string.price_value, it) }
                    }

				}
            }
        }
    }
}
