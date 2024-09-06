package com.moneytree.app.ui.products

import android.app.Activity
import android.graphics.Paint
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.moneytree.app.BuildConfig
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.callbacks.NSProductDetailCallback
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.config.ApiConfig
import com.moneytree.app.databinding.LayoutProductItemBinding
import com.moneytree.app.repository.network.responses.ProductDataDTO


class MTProductListRecycleAdapter(
    activityNS: Activity,
	val isGrid: Boolean,
    onPageChange: NSPageChangeCallback,
	val onProductClick: NSProductDetailCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val productData: MutableList<ProductDataDTO> = arrayListOf()
    private val onPageChangeCallback: NSPageChangeCallback = onPageChange

    fun updateData(voucherList: MutableList<ProductDataDTO>) {
        productData.addAll(voucherList)
        if (voucherList.isValidList()) {
            notifyItemRangeChanged(0, productData.size - 1)
        } else {
            notifyDataSetChanged()
        }
    }

    fun clearData() {
        productData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val voucherView = LayoutProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSProductViewHolder(voucherView)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSProductViewHolder) {
            val holder: NSProductViewHolder = holderRec
            holder.bind(productData[holder.absoluteAdapterPosition])
        }

        if (position == productData.size - 1) {
            if (((position + 1) % NSConstants.PAGINATION) == 0) {
                onPageChangeCallback.onPageChange(1)
            }
        }
    }

    override fun getItemCount(): Int {
        return productData.size
    }

    /**
     * The view holder for voucher list
     *
     * @property productBinding The voucher list view binding
     */
    inner class NSProductViewHolder(private val productBinding: LayoutProductItemBinding) :
        RecyclerView.ViewHolder(productBinding.root) {

        /**
         * To bind the voucher details view into Recycler view with given data
         *
         * @param response The voucher details
         */
        fun bind(response: ProductDataDTO) {
            with(productBinding) {
                with(response) {
					clProductLayout.setVisibility(!isGrid)
					clProductLayoutGrid.setVisibility(isGrid)
					val url = ApiConfig.baseUrlImage + productImage
					Glide.with(activity).load(url).error(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.NONE)
						.skipMemoryCache(true).into(ivProductImg)

                    tvProductName.text = productName
                    tvPrice.text = addText(activity, R.string.price_value, rate!!)
                    tvRate.text = addText(activity, R.string.rate_title, sdPrice!!)
                    tvRate.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvRateGrid.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvDescription.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT)
                    } else {
                        Html.fromHtml(description)
                    }

                    if (sdPrice == rate) {
                        tvRate.gone()
                        tvRateGrid.gone()
                    }

					clProductLayout.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							onProductClick.onResponse(response)
						}
					})

					//Grid
					Glide.with(activity).load(url).error(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.NONE)
						.skipMemoryCache(true).into(ivProductImgGrid)
					tvProductNameGrid.text = productName
					tvProductNameGrid.isSelected = true
					tvPriceGrid.text = addText(activity, R.string.price_value, rate)
					tvRateGrid.text = addText(activity, R.string.rate_title, sdPrice)
					clProductLayoutGrid.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							onProductClick.onResponse(response)
						}
					})
                }
            }
        }
    }
}
