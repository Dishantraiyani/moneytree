package com.moneytree.app.ui.home

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.callbacks.NSProductCategoryCallback
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.LayoutItemRechargesBinding
import com.moneytree.app.repository.network.responses.NSCategoryData


class MTCategoryHomeRecycleAdapter(private val context: Context, private val onClickResponse: NSProductCategoryCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	private val categoryData: MutableList<NSCategoryData> = arrayListOf()

    fun updateData(voucherList: MutableList<NSCategoryData>) {
        categoryData.addAll(voucherList)
        if (voucherList.isValidList()) {
            notifyItemRangeChanged(0, categoryData.size - 1)
        } else {
            notifyDataSetChanged()
        }
    }

    fun clearData() {
        categoryData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val voucherView = LayoutItemRechargesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSCategoryViewHolder(voucherView)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSCategoryViewHolder) {
            val holder: NSCategoryViewHolder = holderRec
            holder.bind(categoryData[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return categoryData.size
    }

    /**
     * The view holder for voucher list
     *
     * @property voucherBinding The voucher list view binding
     */
    inner class NSCategoryViewHolder(private val voucherBinding: LayoutItemRechargesBinding) :
        RecyclerView.ViewHolder(voucherBinding.root) {

        /**
         * To bind the voucher details view into Recycler view with given data
         *
         * @param response The voucher details
         */
        fun bind(response: NSCategoryData) {
            with(voucherBinding) {
                with(response) {

                    if (response.categoryImg?.isNotEmpty() == true) {
                        Glide.with(context).load(response.categoryImg).into(ivFieldImage)
                    } else {
                        try {
                            val drawable: Drawable? = ResourcesCompat.getDrawable(context.resources, context.resources.getIdentifier(categoryName?.replace(" ", "_")?.lowercase(), "drawable", context.packageName), null)
                            ivFieldImage.setImageDrawable(drawable)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    tvFieldName.text = categoryName
					llRecharge.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							onClickResponse.onResponse(response)
						}
					})
                }
            }
        }
    }
}
