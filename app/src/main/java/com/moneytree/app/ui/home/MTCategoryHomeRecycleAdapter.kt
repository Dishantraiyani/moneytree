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
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.LayoutItemRechargesBinding
import com.moneytree.app.repository.network.responses.NSCategoryData
import com.moneytree.app.BuildConfig
import com.moneytree.app.R
import com.moneytree.app.databinding.LayoutItemCategoryProductBinding

class MTCategoryHomeRecycleAdapter(private val context: Context, private val onClickResponse: NSProductCategoryCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	private val categoryData: MutableList<NSCategoryData> = arrayListOf()
    
    private val iconMap = mapOf(
        "ayurvedic" to R.drawable.ic_ayurvedic,
        "cosmetic" to R.drawable.ic_cosmetic,
        "fmcg" to R.drawable.ic_fmcg,
        "herbo_nutraceuticals" to R.drawable.ic_herbo_nutraceuticals,
        "otc" to R.drawable.ic_otc,
        "protein_powder" to R.drawable.ic_protein_powder,
        "spray" to R.drawable.ic_spray,
        "home_care" to R.drawable.ic_home_care,
        "agriculture_product" to R.drawable.ic_agriculture_product,
        "personal_care" to R.drawable.ic_personal_care,
        "gym_supplement" to R.drawable.ic_gym_supplement
    )

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
        val voucherView = LayoutItemCategoryProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    inner class NSCategoryViewHolder(private val voucherBinding: LayoutItemCategoryProductBinding) :
        RecyclerView.ViewHolder(voucherBinding.root) {

        /**
         * To bind the voucher details view into Recycler view with given data
         *
         * @param response The voucher details
         */
        fun bind(response: NSCategoryData) {
            with(voucherBinding) {
                with(response) {
                    
                    val resId = iconMap[categoryName?.replace(" ", "_")?.lowercase()] ?: R.drawable.placeholder
                    ivFieldImage.setImageResource(resId)

                    tvFieldName.text = categoryName?.replace(" ", "\n")
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
