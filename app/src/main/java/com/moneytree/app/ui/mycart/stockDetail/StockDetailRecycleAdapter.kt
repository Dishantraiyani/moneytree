package com.moneytree.app.ui.mycart.stockDetail

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.LayoutRepurchaseStockItemBinding
import com.moneytree.app.databinding.LayoutStockDetailListBinding
import com.moneytree.app.databinding.LayoutUpLineMemberBinding
import com.moneytree.app.repository.network.responses.NSRePurchaseInfoData
import com.moneytree.app.repository.network.responses.NSUpLineData
import com.moneytree.app.repository.network.responses.StockDataItem

class StockDetailRecycleAdapter(
    activityNS: Activity,
	private val isStock: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val stockData: MutableList<NSRePurchaseInfoData> = arrayListOf()

    fun updateData(memberList: MutableList<NSRePurchaseInfoData>) {
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
        val memberView = LayoutRepurchaseStockItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    inner class NSMemberTreeViewHolder(private val voucherBinding: LayoutRepurchaseStockItemBinding) :
        RecyclerView.ViewHolder(voucherBinding.root) {

        /**
         * To bind the member tree details view into Recycler view with given data
         *
         * @param response The member tree details
         */
        fun bind(response: NSRePurchaseInfoData) {
            with(voucherBinding) {
                with(response) {
					ivNext.gone()
					tvRepurchaseNo.text = response.productName
					if (isStock) {
						tvStockId.text = response.stockTransferId
					} else {
						tvStockId.text = response.repurchaseId
					}
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

					tvRemark.gone()


				}
            }
        }
    }
}
