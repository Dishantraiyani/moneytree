package com.moneytree.app.ui.notification

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.common.callbacks.NSNotificationCallback
import com.moneytree.app.databinding.LayoutNotificaitonBinding
import com.moneytree.app.repository.network.responses.NSNotificationListData

class NSNotificationRecycleAdapter(
    onCallBack: NSNotificationCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val onNSNotificationCallback = onCallBack
    private val notificationData: MutableList<NSNotificationListData> = arrayListOf()

    fun updateData(notificationList: MutableList<NSNotificationListData>) {
        notificationData.addAll(notificationList)
        notifyItemRangeChanged(0, notificationData.size - 1)
    }

    fun clearData() {
        notificationData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val orderView = LayoutNotificaitonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSNotificationViewHolder(orderView)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSNotificationViewHolder) {
            val holder: NSNotificationViewHolder = holderRec
            holder.bind(notificationData[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return notificationData.size
    }

    /**
     * The view holder for notification list
     *
     * @property notificationBinding The notification list view binding
     */
    inner class NSNotificationViewHolder(private val notificationBinding: LayoutNotificaitonBinding) :
        RecyclerView.ViewHolder(notificationBinding.root) {

        /**
         * To bind the order details view into Recycler view with given data
         *
         * @param response The order details
         */
        fun bind(response: NSNotificationListData) {
            with(notificationBinding) {
                with(response) {
                    tvNotificationTitle.text = title
                    tvNotificationTime.text = createdAt
                    tvNotificationSub.text = subTitle
                    if (absoluteAdapterPosition == itemCount - 1) {
                        viewLine.visibility = View.GONE
                    }

                    clNotification.setOnClickListener {
                        onNSNotificationCallback.onClick(response)
                    }
                }
            }
        }
    }
}