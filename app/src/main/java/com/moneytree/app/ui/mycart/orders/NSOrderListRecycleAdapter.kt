package com.moneytree.app.ui.mycart.orders

import android.app.Activity
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.moneytree.app.BuildConfig
import com.moneytree.app.R
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.callbacks.NSCartTotalAmountCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.callbacks.NSProductDetailCallback
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.addAmount
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.invisible
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.common.utils.visible
import com.moneytree.app.config.ApiConfig
import com.moneytree.app.databinding.LayoutOrderProductItemBinding
import com.moneytree.app.databinding.LayoutProductItemBinding
import com.moneytree.app.databinding.LayoutShopProductItemBinding
import com.moneytree.app.repository.network.responses.ProductDataDTO


class NSOrderListRecycleAdapter(
	activityNS: Activity,
	val isGrid: Boolean,
	onPageChange: NSPageChangeCallback,
	val onProductClick: NSProductDetailCallback,
	val onCartTotalClick: NSCartTotalAmountCallback
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
		val voucherView =
			LayoutOrderProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
	inner class NSProductViewHolder(private val productBinding: LayoutOrderProductItemBinding) :
		RecyclerView.ViewHolder(productBinding.root) {

		/**
		 * To bind the voucher details view into Recycler view with given data
		 *
		 * @param response The voucher details
		 */
		fun bind(response: ProductDataDTO) {
			with(productBinding) {
				with(response) {
					tvQtyTitleGrid.gone()
					tvStockQtyGrid.gone()
					tvQtyTitle.gone()
					tvStockQty.gone()
					clProductLayout.setVisibility(!isGrid)
					clProductLayoutGrid.setVisibility(isGrid)
					val url = ApiConfig.baseUrlImage + productImage
					Glide.with(activity).load(url).error(R.drawable.placeholder).into(ivProductImg)
					tvProductName.text = productName
					tvStockQty.text = maxOrderQty
					tvStockQtyGrid.text = maxOrderQty
					tvRate.text = addText(activity, R.string.rate_title, sdPrice!!)
					tvRate.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
					tvRateGrid.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

					if (sdPrice == rate) {
						tvRate.gone()
						tvRateGrid.gone()
					}

					val selectedItem = NSApplication.getInstance().getOrder(response)
					if (selectedItem != null && selectedItem.isFromOrder) {
						itemQty = selectedItem.itemQty
					}

					tvQty.text = itemQty.toString()
					tvQtyGrid.text = itemQty.toString()
					val amount: Int = rate?.toInt() ?: 0
					val finalAmount = itemQty * amount
					isProductValid = finalAmount > 0

					//tvPrice.text = addText(activity, R.string.price_value, finalAmount.toString())
					tvPrice.text = addText(activity, R.string.price_value, amount.toString())
					if (rewardCoin?.isNotEmpty() == true) {
						llReward.visible()
						tvRewardPoint.text = ""+addAmount(activity, R.string.price_value, rewardCoin.toString())
						llRewardGrid.visible()
						tvRewardPointGrid.text = ""+addAmount(activity, R.string.price_value, rewardCoin.toString())
					}

					if (sdPrice == rate) {
						tvRate.gone()
						tvRateGrid.gone()
					}

					add.setOnClickListener {
						addCart(response, finalAmount)
					}

					remove.setOnClickListener {
						removeCart(response, finalAmount)
					}

					addGrid.setOnClickListener {
						addCart(response, finalAmount)
					}

					removeGrid.setOnClickListener {
						removeCart(response, finalAmount)
					}

					ivDetail.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							onProductClick.onResponse(response)
						}
					})

					ivProductImg.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							onProductClick.onResponse(response)
						}
					})

					//Grid
					Glide.with(activity).load(url).error(R.drawable.placeholder)
						.into(ivProductImgGrid)
					tvProductNameGrid.text = productName
					tvProductNameGrid.isSelected = true
					tvPriceGrid.text = rate?.let { addText(activity, R.string.price_value, it) }
					tvRateGrid.text = addText(activity, R.string.rate_title, sdPrice)
					ivProductImgGrid.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							onProductClick.onResponse(response)
						}
					})
				}
			}
		}

		private fun addCart(response: ProductDataDTO, finalAmount: Int) {
			with(productBinding) {
				with(response) {
					response.isFromOrder = true
					var stock = 0
					stock = try {
						maxOrderQty?.toInt() ?: 0
					} catch (e: Exception) {
						0
					}
					if (itemQty < stock && stock != 0) {
						if (itemQty == 0) {
							NSApplication.getInstance().setOrderList(response)
						}
						itemQty += 1
						tvQty.text = itemQty.toString()
						tvQtyGrid.text = itemQty.toString()

						val amount1: Int = rate?.toInt() ?: 0
						val finalAmount1 = itemQty * amount1
						isProductValid = finalAmount > 0

						/*tvPrice.text =
							addText(activity, R.string.price_value, finalAmount1.toString())
						tvPriceGrid.text =
							addText(activity, R.string.price_value, finalAmount1.toString())*/
						onCartTotalClick.onResponse()
					} else {
						Toast.makeText(activity, NSConstants.MAX_TITLE + maxOrderQty?.toInt() + NSConstants.MAX_TITLE_SECOND, Toast.LENGTH_SHORT).show()
					}
				}
			}
		}

		private fun removeCart(response: ProductDataDTO, finalAmount: Int) {
			with(productBinding) {
				with(response) {
					if (itemQty > 0) {
						itemQty -= 1
						if (itemQty == 0) {
							NSApplication.getInstance().removeOrder(response)
						}
						tvQty.text = itemQty.toString()
						tvQtyGrid.text = itemQty.toString()

						val amount1: Int = rate?.toInt() ?: 0
						val finalAmount1 = itemQty * amount1
						isProductValid = finalAmount > 0

						/*tvPrice.text =
							addText(activity, R.string.price_value, finalAmount1.toString())
						tvPriceGrid.text =
							addText(activity, R.string.price_value, finalAmount1.toString())*/
						onCartTotalClick.onResponse()
					}
				}
			}

		}

	}
}
