package com.moneytree.app.ui.register

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.callbacks.NSMemberActiveSelectCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.callbacks.NSRegisterActiveSelectCallback
import com.moneytree.app.common.utils.*
import com.moneytree.app.databinding.LayoutRegisterItemBinding
import com.moneytree.app.repository.network.responses.NSRegisterListData

class NSRegisterListRecycleAdapter(
    activityNS: Activity,
    private val packageActiveCallback: NSRegisterActiveSelectCallback,
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
                onPageChangeCallback.onPageChange(1)
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
                    tvUserNameRegister.text = username?.let { addText(activity, R.string.user_name_register, it) }
                    tvEmailRegister.text = email?.let { addText(activity, R.string.email_register, it) }
                    tvPhoneRegister.text = mobile?.let { addText(activity, R.string.phone_register, it) }
                    tvDate.text = createdAt?.let { addText(activity, R.string.date_register, it) }
                    tvFullNameRegister.text = fullName?.let { addText(activity, R.string.full_name_value, it) }
                    tvPackageName.text = if (packageName == null) "" else packageName

                    if (response.setDefault?.lowercase().equals("n")) {
                        btnSetDefault.visible()
                    } else {
                        btnSetDefault.gone()
                    }


                    btnSetDefault.setOnClickListener(object : SingleClickListener() {
                        override fun performClick(v: View?) {
                            packageActiveCallback.onDefault(response)
                        }
                    })

                    btnSendMessage.setOnClickListener(object : SingleClickListener() {
                        override fun performClick(v: View?) {
                            packageActiveCallback.onMessageSend(response)
                        }
                    })

                    val isActive = directActivation.equals("NOT REQUIRED")
                    btnActive.isEnabled = !isActive
                    btnActive.setBackgroundResource(if (isActive) R.drawable.gray_button_order_border else R.drawable.blue_button_order_border)
                    btnActive.text = directActivation
                    btnActive.setOnClickListener(object : SingleClickListener() {
                        override fun performClick(v: View?) {
                            packageActiveCallback.onClick(response)
                        }
                    })
                }
            }
        }
    }
}

