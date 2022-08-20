package com.moneytree.app.ui.packageVoucher.packageDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.LayoutPackageVoucherDetailBinding
import com.moneytree.app.repository.network.responses.NSPackageVoucherData

class NSPackageDetailRecycleAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	private val registerData: MutableList<NSPackageVoucherData> = arrayListOf()

    fun updateData(registerList: MutableList<NSPackageVoucherData>) {
        registerData.addAll(registerList)
        if (registerList.isValidList()) {
            notifyItemRangeChanged(0, registerData.size - 1)
        } else {
            notifyDataSetChanged()
        }
    }

    fun clearData() {
        registerData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val registerView = LayoutPackageVoucherDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSPackageListViewHolder(registerView)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSPackageListViewHolder) {
            val holder: NSPackageListViewHolder = holderRec
            holder.bind(registerData[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return registerData.size
    }

    /**
     * The view holder for register list
     *
     * @property registerBinding The register list view binding
     */
    inner class NSPackageListViewHolder(private val registerBinding: LayoutPackageVoucherDetailBinding) :
        RecyclerView.ViewHolder(registerBinding.root) {

        /**
         * To bind the register details view into Recycler view with given data
         *
         * @param response The register details
         */
        fun bind(response: NSPackageVoucherData) {
            with(registerBinding) {
                with(response) {
					tvVoucherId.text = voucherId
                    tvVoucherCode.text = voucherCode
                }
            }
        }
    }
}
