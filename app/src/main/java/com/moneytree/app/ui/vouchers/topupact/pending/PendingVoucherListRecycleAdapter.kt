package com.moneytree.app.ui.vouchers.topupact.pending

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.databinding.LayoutPendingVoucherListBinding
import com.moneytree.app.repository.network.responses.PendingVoucherListData

class PendingVoucherListRecycleAdapter(
    private val activity: Activity, private val callback: (PendingVoucherListData) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    private val voucherData: MutableList<PendingVoucherListData> = arrayListOf()

    fun updateDataValue(voucherList: MutableList<PendingVoucherListData>) {
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
        val voucherView = LayoutPendingVoucherListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    
    inner class NSVoucherViewHolder(private val voucherBinding: LayoutPendingVoucherListBinding) :
        RecyclerView.ViewHolder(voucherBinding.root) {
        
        fun bind(response: PendingVoucherListData) {
            with(voucherBinding) {
                with(response) {
                    tvSerialNo.text = addText(activity, R.string.serial_no, topup28VoucherId?:"")
                    tvVoucherName.text = voucherName
                    tvDateTitle.text = addText(activity,R.string.voucher_status_value, voucherMonth?:"")
                    tvDate.text = addText(activity, R.string.remark_value, remark?:"Not Available")
                    tvStatusValue.text = status
                    
                    val isClaim = status?.lowercase()?.contains("claim") == true
                    
                    tvStatus.setVisibility(!isClaim)
                    tvStatusValue.setVisibility(!isClaim)
                    btnView.setVisibility(isClaim)
                    
                    btnView.setSafeOnClickListener {
                        callback.invoke(response)
                    }
                }
            }
        }
    }
}
