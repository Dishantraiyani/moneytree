package com.moneytree.app.ui.profile

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.callbacks.NSProfileSelectCallback
import com.moneytree.app.databinding.LayoutProfileItemBinding

class ProfileRecycleAdapter(
    profileItemList: MutableList<String>,
    isLanguageSelected: Boolean,
    profileItemSelectCallBack: NSProfileSelectCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val profileItemListData: MutableList<String> = profileItemList
    private val nsProfileItemSelectCallBack: NSProfileSelectCallback = profileItemSelectCallBack
    private val isLanguage = isLanguageSelected

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val orderView = LayoutProfileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSProfileViewHolder(orderView)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSProfileViewHolder) {
            val holder: NSProfileViewHolder = holderRec
            holder.bind(profileItemListData[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return profileItemListData.size
    }

    /**
     * The view holder for order list
     *
     * @property profileBinding The order list view binding
     */
    inner class NSProfileViewHolder(private val profileBinding: LayoutProfileItemBinding) :
        RecyclerView.ViewHolder(profileBinding.root) {

        /**
         * To bind the order details view into Recycler view with given data
         *
         * @param response The order details
         */
        fun bind(response: String) {
            with(profileBinding) {
                tvProfileTitle.text = response
                if (absoluteAdapterPosition == itemCount - 1) {
                    viewLine.visibility = View.GONE
                }
                if (isLanguage) {
                    ivNext.setImageResource(R.drawable.arrow_left)
                    if (absoluteAdapterPosition == 1) {
                        tvProfileTitle.gravity = Gravity.END or Gravity.CENTER
                    }
                } else {
                    ivNext.setImageResource(R.drawable.arrow_right)
                }

                clProfileItem.setOnClickListener {
                    nsProfileItemSelectCallBack.onPosition(absoluteAdapterPosition)
                }
            }
        }
    }
}