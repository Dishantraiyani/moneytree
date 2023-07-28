package com.moneytree.app.ui.mycart.products

import android.app.Activity
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.moneytree.app.BuildConfig
import com.moneytree.app.R
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.callbacks.NSCartTotalAmountCallback
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.databinding.LayoutShopProductItemBinding
import com.moneytree.app.repository.network.responses.ProductDataDTO

class ProductDataSet(val activity: Activity, private val isGrid: Boolean, val binding: LayoutShopProductItemBinding, response: ProductDataDTO, val onCartTotalClick: NSCartTotalAmountCallback) {

    init {
        binding.apply {
            response.apply {

                clProductLayout.setVisibility(!isGrid)
                clProductLayoutGrid.setVisibility(isGrid)
                val url = NSUtilities.decrypt(BuildConfig.BASE_URL_IMAGE) + productImage
                Glide.with(activity.applicationContext).load(url).error(R.drawable.placeholder).into(ivProductImg)
                tvProductName.text = productName
                tvStockQty.text = stockQty
                tvStockQtyGrid.text = stockQty
                tvRate.text = addText(activity, R.string.rate_title, rate!!)

                val selectedItem = NSApplication.getInstance().getProduct(response)
                if (selectedItem != null) {
                    itemQty = selectedItem.itemQty
                }

                tvQty.text = itemQty.toString()
                tvQtyGrid.text = itemQty.toString()
                val amount: Int = sdPrice?.toInt() ?: 0
                val finalAmount = itemQty * amount
                isProductValid = finalAmount > 0

                tvPrice.text = addText(activity, R.string.price_value, finalAmount.toString())


                Glide.with(activity.applicationContext).load(url).error(R.drawable.placeholder)
                    .into(ivProductImgGrid)
                tvProductNameGrid.text = productName
                tvProductNameGrid.isSelected = true
                tvPriceGrid.text = sdPrice?.let { addText(activity, R.string.price_value, it) }
                tvRateGrid.text = addText(activity, R.string.rate_title, rate)


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
            }
        }
    }

    private fun addCart(response: ProductDataDTO, finalAmount: Int) {
        with(binding) {
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
                    tvQty.text = itemQty.toString()
                    tvQtyGrid.text = itemQty.toString()

                    val amount1: Int = sdPrice?.toInt() ?: 0
                    val finalAmount1 = itemQty * amount1
                    isProductValid = finalAmount > 0

                    tvPrice.text =
                        addText(activity, R.string.price_value, finalAmount1.toString())
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
        with(binding) {
            with(response) {
                if (itemQty > 0) {
                    itemQty -= 1
                    if (itemQty == 0) {
                        NSApplication.getInstance().removeProduct(response)
                    }
                    tvQty.text = itemQty.toString()
                    tvQtyGrid.text = itemQty.toString()

                    val amount1: Int = sdPrice?.toInt() ?: 0
                    val finalAmount1 = itemQty * amount1
                    isProductValid = finalAmount > 0

                    tvPrice.text =
                        addText(activity, R.string.price_value, finalAmount1.toString())
                    tvPriceGrid.text =
                        addText(activity, R.string.price_value, finalAmount1.toString())
                    onCartTotalClick.onResponse()
                }
            }
        }

    }
}