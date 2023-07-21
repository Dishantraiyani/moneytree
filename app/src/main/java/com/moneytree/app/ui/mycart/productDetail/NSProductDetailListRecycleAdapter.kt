package com.moneytree.app.ui.mycart.productDetail

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.moneytree.app.BuildConfig
import com.moneytree.app.R
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.callbacks.NSCartTotalAmountCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.callbacks.NSProductDetailCallback
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.LayoutShopProductDetailItemBinding
import com.moneytree.app.repository.network.responses.ProductDataDTO


class NSProductDetailListRecycleAdapter(
	activityNS: Activity,
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
			LayoutShopProductDetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
	inner class NSProductViewHolder(private val productBinding: LayoutShopProductDetailItemBinding) :
		RecyclerView.ViewHolder(productBinding.root) {

		/**
		 * To bind the voucher details view into Recycler view with given data
		 *
		 * @param response The voucher details
		 */
		fun bind(response: ProductDataDTO) {
			with(productBinding) {
				with(response) {
					val url = NSUtilities.decrypt(BuildConfig.BASE_URL_IMAGE) + productImage
					tvStockQtyGrid.text = stockQty

					val selectedItem = NSApplication.getInstance().getProduct(response)
					if (selectedItem != null) {
						itemQty = selectedItem.itemQty
					}

					tvQtyGrid.text = itemQty.toString()
					val amount: Int = sdPrice?.toInt() ?: 0
					val finalAmount = itemQty * amount
					isProductValid = finalAmount > 0

					addGrid.setOnClickListener {
						addCart(response, finalAmount)
					}

					removeGrid.setOnClickListener {
						removeCart(response, finalAmount)
					}

					//Grid
					Glide.with(activity).load(url).error(R.drawable.placeholder)
						.into(ivProductImgGrid)
					tvProductNameGrid.text = productName
					tvProductNameGrid.isSelected = true
					tvPriceGrid.text = sdPrice?.let { addText(activity, R.string.price_value, it) }
					tvRateGrid.text = addText(activity, R.string.rate_title, rate?:"")
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
					var stock = 0
					stock = try {
						stockQty?.toInt() ?: 0
					} catch (e: Exception) {
						0
					}
					if (itemQty < stock && stock != 0) {
						if (itemQty == 0) {
							NSApplication.getInstance().setProductList(response)
						}
						itemQty += 1
						tvQtyGrid.text = itemQty.toString()

						val amount1: Int = sdPrice?.toInt() ?: 0
						val finalAmount1 = itemQty * amount1
						isProductValid = finalAmount > 0

						tvPriceGrid.text =
							addText(activity, R.string.price_value, finalAmount1.toString())
						onCartTotalClick.onResponse()
					} else {
						Toast.makeText(activity, "No Stock Available", Toast.LENGTH_SHORT).show()
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
							NSApplication.getInstance().removeProduct(response)
						}
						tvQtyGrid.text = itemQty.toString()

						val amount1: Int = sdPrice?.toInt() ?: 0
						val finalAmount1 = itemQty * amount1
						isProductValid = finalAmount > 0

						tvPriceGrid.text =
							addText(activity, R.string.price_value, finalAmount1.toString())
						onCartTotalClick.onResponse()
					}
				}
			}

		}

	}
}
