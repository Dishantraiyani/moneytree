package com.moneytree.app.ui.register

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.LayoutRegisterItemBinding
import com.moneytree.app.repository.network.responses.NSRegisterListData

class NSRegisterListRecycleAdapter(
    activityNS: Activity,
    onPageChange: NSPageChangeCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val registerData: MutableList<NSRegisterListData> = arrayListOf()
    private val onPageChangeCallback: NSPageChangeCallback = onPageChange

    fun updateData(registerList: MutableList<NSRegisterListData>) {
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
        val registerView = LayoutRegisterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSRegisterViewHolder(registerView)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSRegisterViewHolder) {
            val holder: NSRegisterViewHolder = holderRec
            holder.bind(registerData[holder.absoluteAdapterPosition])
        }

        if (position == registerData.size - 1) {
            if (((position + 1) % NSConstants.PAGINATION) == 0) {
                onPageChangeCallback.onPageChange()
            }
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
    inner class NSRegisterViewHolder(private val registerBinding: LayoutRegisterItemBinding) :
        RecyclerView.ViewHolder(registerBinding.root) {

        /**
         * To bind the register details view into Recycler view with given data
         *
         * @param response The register details
         */
        fun bind(response: NSRegisterListData) {
            with(registerBinding) {
                with(response) {
                    tvUserNameRegister.text = addText(activity, R.string.user_name_register, username!!)
                    tvEmailRegister.text = addText(activity, R.string.email_register, email!!)
                    tvPhoneRegister.text = addText(activity, R.string.phone_register, mobile!!)
                    tvDate.text = addText(activity, R.string.date_register, createdAt!!)
                }
            }
        }
    }
}