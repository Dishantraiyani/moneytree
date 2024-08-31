package com.moneytree.app.ui.packageVoucher.packageList

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.callbacks.NSPackageClickCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.LayoutPackageListItemBinding
import com.moneytree.app.repository.network.responses.NSPackageData
import com.moneytree.app.repository.network.responses.NSRegisterListData

class NSPackageListRecycleAdapter(
    activityNS: Activity,
	private val onClickAdapter: NSPackageClickCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val registerData: MutableList<NSPackageData> = arrayListOf()

    fun updateData(registerList: MutableList<NSPackageData>) {
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
        val registerView = LayoutPackageListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    inner class NSPackageListViewHolder(private val registerBinding: LayoutPackageListItemBinding) :
        RecyclerView.ViewHolder(registerBinding.root) {

        /**
         * To bind the register details view into Recycler view with given data
         *
         * @param response The register details
         */
        fun bind(response: NSPackageData) {
            with(registerBinding) {
                with(response) {
					tvPackageId.text = addText(activity, R.string.package_id, packageId!!)
                    tvPackageName.text = packageName!!
                    tvMrp.text = addText(activity, R.string.mrp, mrp!!)

					clPackageLayout.setOnClickListener {
						onClickAdapter.onResponse(response)
					}

                }
            }
        }
    }
}
