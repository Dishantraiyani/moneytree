package com.moneytree.app.ui.home

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.moneytree.app.R
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.callbacks.NSCartTotalAmountCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.callbacks.NSProductDetailCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.invisible
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.setGrayScale
import com.moneytree.app.common.utils.visible
import com.moneytree.app.config.ApiConfig
import com.moneytree.app.databinding.LayoutHomeProductItemBinding
import com.moneytree.app.repository.network.responses.ProductDataDTO


class ProductHomeListRecycleAdapter(
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
			LayoutHomeProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
	inner class NSProductViewHolder(private val productBinding: LayoutHomeProductItemBinding) :
		RecyclerView.ViewHolder(productBinding.root) {

		/**
		 * To bind the voucher details view into Recycler view with given data
		 *
		 * @param response The voucher details
		 */
		fun bind(response: ProductDataDTO) {
			with(productBinding) {
				with(response) {
					
					val url = ApiConfig.baseUrlImage + productImage
					tvStockQtyGrid.text = stockQty

					val instance = NSApplication.getInstance()
					val selectedItem = instance.getProduct(response)
					if (selectedItem != null) {
						itemQty = selectedItem.itemQty
					}

					tvQtyGrid.text = itemQty.toString()
					val amount: Int = rate?.toInt() ?: 0
					val finalAmount = itemQty * amount
					isProductValid = finalAmount > 0

					if (sdPrice == rate) {
						tvRateGrid.gone()
					}

					addGrid.setOnClickListener {
						addCart(response, finalAmount)
					}

					removeGrid.setOnClickListener {
						removeCart(response, finalAmount)
						
						if (itemQty <= 0) {
							tvAddToCart.visible()
							qutGrid.invisible()
						}
					}
					
					tvAddToCart.setOnClickListener {
						addCart(response, finalAmount)
						qutGrid.visible()
						tvAddToCart.gone()
					}
					
					val isStockAvailable = (stockQty?:"0").toInt() > 0
					ivProductImgGrid.setGrayScale(!isStockAvailable)
					
					val grayColor = ContextCompat.getColor(activity, R.color.hint_color)
					val blackColor = ContextCompat.getColor(activity, R.color.black)
					val grayC = ContextCompat.getColor(activity, R.color.gray_text)
					val white = ContextCompat.getColor(activity, R.color.white)
					
					if (!isStockAvailable) {
						tvProductNameGrid.setTextColor(grayColor)
					} else {
						tvProductNameGrid.setTextColor(blackColor)
					}
					
					if (!isStockAvailable) {
						tvPriceGrid.setTextColor(grayColor)
					} else {
						tvPriceGrid.setTextColor(blackColor)
					}
					
					tvAddToCart.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(tvAddToCart.context, if(isStockAvailable) R.color.orange else R.color.image_background))
					tvAddToCart.setText(if (isStockAvailable) R.string.add_to_cart else R.string.out_of_stock)
					tvAddToCart.setTextColor(if (!isStockAvailable) grayC else white)
					

					//Grid
					Glide.with(activity).load(url).error(R.drawable.placeholder)
						.into(ivProductImgGrid)
					tvProductNameGrid.text = productName
					tvProductNameGrid.isSelected = true
					tvRateGrid.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
					tvPriceGrid.text = rate?.let { addText(activity, R.string.price_value, it) }
					tvRateGrid.text = addText(activity, R.string.rate_title, sdPrice?:"")
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
					stock = try { stockQty?.toInt() ?: 0
					} catch (e: Exception) {
						0
					}
					if ((itemQty < stock && stock != 0)) {
						if (itemQty == 0) {
							val instance = NSApplication.getInstance()
							instance.setProductList(response)
						}
						itemQty += 1
						tvQtyGrid.text = itemQty.toString()

						val amount1: Int = rate?.toInt() ?: 0
						val finalAmount1 = itemQty * amount1
						isProductValid = finalAmount > 0

						/*tvPriceGrid.text =
							addText(activity, R.string.price_value, finalAmount1.toString())*/
						onCartTotalClick.onResponse()
					} else {
						Toast.makeText(activity, NSConstants.STOCK_NOT_AVAILABLE, Toast.LENGTH_SHORT).show()
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
							val instance = NSApplication.getInstance()
							instance.removeProduct(response)
						}
						tvQtyGrid.text = itemQty.toString()

						val amount1: Int = rate?.toInt() ?: 0
						val finalAmount1 = itemQty * amount1
						isProductValid = finalAmount > 0

						/*tvPriceGrid.text =
							addText(activity, R.string.price_value, finalAmount1.toString())*/
						onCartTotalClick.onResponse()
					}
				}
			}

		}

	}
}
