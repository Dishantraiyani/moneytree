package com.moneytree.app.ui.vouchers.topup.list

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.LayoutVoucherItemBinding
import com.moneytree.app.repository.network.responses.NSVoucherListData
import com.moneytree.app.repository.network.responses.TopUpSVoucherListData

class TopUpVoucherListRecycleAdapter(
    activityNS: Activity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val voucherData: MutableList<TopUpSVoucherListData> = arrayListOf()

    fun updateData(voucherList: MutableList<TopUpSVoucherListData>) {
        voucherData.addAll(voucherList)
        if (voucherList.isValidList()) {
            notifyItemRangeChanged(0, voucherData.size - 1)
        } else {
            notifyDataSetChanged()
        }
    }

    fun clearData() {
        voucherData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val voucherView = LayoutVoucherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSVoucherViewHolder(voucherView)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSVoucherViewHolder) {
            val holder: NSVoucherViewHolder = holderRec
            holder.bind(voucherData[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return voucherData.size
    }

    /**
     * The view holder for voucher list
     *
     * @property voucherBinding The voucher list view binding
     */
    inner class NSVoucherViewHolder(private val voucherBinding: LayoutVoucherItemBinding) :
        RecyclerView.ViewHolder(voucherBinding.root) {

        /**
         * To bind the voucher details view into Recycler view with given data
         *
         * @param response The voucher details
         */
        fun bind(response: TopUpSVoucherListData) {
            with(voucherBinding) {
                with(response) {
                    tvVoucherCode.text = addText(activity, R.string.voucher_code, voucherCode?:"")
                    tvMemberId.text = addText(activity, R.string.transfer_id_value, transferId?:"")
                    //tvMemberType.text = addText(activity, R.string.transfer_type, transferType?:"")
                    tvPackageNameTitle.text = addText(activity, R.string.transfer_type, "")
                    tvPackageName.text = transferType?:""
                    tvDateStatus.text = addText(activity, R.string.voucher_date, createdAt!!)
                }
            }
        }
    }
}
