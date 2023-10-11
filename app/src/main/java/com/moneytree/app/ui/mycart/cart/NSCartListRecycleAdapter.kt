package com.moneytree.app.ui.mycart.cart

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.moneytree.app.BuildConfig
import com.moneytree.app.R
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.callbacks.NSCartTotalAmountCallback
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.LayoutCartItemBinding
import com.moneytree.app.repository.network.responses.ProductDataDTO


class NSCartListRecycleAdapter(
	activityNS: Activity,
	val isFromOrderProduct: Boolean,
	val isGrid: Boolean,
	val onProductClick: NSCartTotalAmountCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	private val activity: Activity = activityNS
	private val productData: MutableList<ProductDataDTO> = arrayListOf()

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
			LayoutCartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return NSProductViewHolder(voucherView)
	}

	override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
		if (holderRec is NSProductViewHolder) {
			val holder: NSProductViewHolder = holderRec
			holder.bind(productData[holder.absoluteAdapterPosition])
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
	inner class NSProductViewHolder(private val productBinding: LayoutCartItemBinding) :
		RecyclerView.ViewHolder(productBinding.root) {

		/**
		 * To bind the voucher details view into Recycler view with given data
		 *
		 * @param response The voucher details
		 */
		fun bind(response: ProductDataDTO) {
			with(productBinding) {
				with(response) {
					if (isFromOrderProduct) {
						tvQtyTitle.gone()
						tvStockQty.gone()
						qut.gone()
						qutOrder.visible()
						productDel.gone()
						productDelOrder.visible()
					}

					clProductLayout.setVisibility(!isGrid)
					val url = NSUtilities.decrypt(BuildConfig.BASE_URL_IMAGE) + productImage
					Glide.with(activity).load(url).error(R.drawable.placeholder)
						.diskCacheStrategy(DiskCacheStrategy.NONE)
						.skipMemoryCache(true).into(ivProductImg)
					tvProductName.text = productName
					tvRate.text = addText(activity, R.string.rate_title, rate!!)
					tvQty.text = itemQty.toString()
					tvQtyOrder.text = itemQty.toString()
					tvStockQty.text = stockQty

					val amount: Int = sdPrice?.toInt() ?: 0
					val finalAmount = itemQty * amount
					isProductValid = finalAmount > 0

					tvPrice.text = addText(activity, R.string.price_value, finalAmount.toString())

					fun add() {
						var stock = 0
						stock = try {
							stockQty?.toInt() ?: 0
						} catch (e: Exception) {
							0
						}
						if ((itemQty < stock && stock != 0) || isFromOrderProduct) {
							itemQty += 1
							tvQty.text = itemQty.toString()
							tvQtyOrder.text = itemQty.toString()

							val amount1: Int = sdPrice?.toInt() ?: 0
							val finalAmount1 = itemQty * amount1
							isProductValid = finalAmount > 0

							tvPrice.text =
								addText(activity, R.string.price_value, finalAmount1.toString())
							onProductClick.onResponse()
						} else {
							Toast.makeText(activity, "No Stock Available", Toast.LENGTH_SHORT)
								.show()
						}
					}

					add.setOnClickListener {
						add()
					}

					addOrder.setOnClickListener {
						add()
					}

					fun remove() {
						if (itemQty > 0) {
							itemQty -= 1
							tvQty.text = itemQty.toString()
							tvQtyOrder.text = itemQty.toString()

							val amount1: Int = sdPrice?.toInt() ?: 0
							val finalAmount1 = itemQty * amount1
							isProductValid = finalAmount > 0

							tvPrice.text =
								addText(activity, R.string.price_value, finalAmount1.toString())

							if (itemQty == 0) {
								val instance = NSApplication.getInstance()
								if (isFromOrderProduct) {
									instance.removeOrder(response)
								} else {
									instance.removeProduct(response)
								}
								productData.remove(response)
								notifyItemRemoved(absoluteAdapterPosition)
							}

							onProductClick.onResponse()
						}
					}

					remove.setOnClickListener {
						remove()
					}

					removeOrder.setOnClickListener {
						remove()
					}

					fun orderDelete() {
						itemQty = 0
						val instance = NSApplication.getInstance()
						if (isFromOrderProduct) {
							instance.removeOrder(response)
						} else {
							instance.removeProduct(response)
						}
						productData.remove(response)
						notifyItemRemoved(absoluteAdapterPosition)
						onProductClick.onResponse()
					}

					productDel.setOnClickListener {
						orderDelete()
					}

					productDelOrder.setOnClickListener {
						orderDelete()
					}
				}
			}
		}
	}
}
