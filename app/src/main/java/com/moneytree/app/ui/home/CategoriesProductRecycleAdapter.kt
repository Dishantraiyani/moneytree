package com.moneytree.app.ui.home

import android.app.Activity
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.callbacks.NSCartTotalAmountCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.callbacks.NSProductCategoryCallback
import com.moneytree.app.common.callbacks.NSProductDetailCallback
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.LayoutHomeCategoriesProductBinding
import com.moneytree.app.repository.network.responses.NSCategoryData
import com.moneytree.app.repository.network.responses.ProductDataDTO

class CategoriesProductRecycleAdapter(private val context: Activity, private val onClickResponse: NSProductCategoryCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	private val categoryData: MutableList<NSCategoryData> = arrayListOf()

    fun updateData(categoriesList: List<NSCategoryData>) {
        categoryData.addAll(categoriesList)
        if (categoriesList.isValidList()) {
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
        val voucherView = LayoutHomeCategoriesProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

   
    inner class NSCategoryViewHolder(private val voucherBinding: LayoutHomeCategoriesProductBinding) :
        RecyclerView.ViewHolder(voucherBinding.root) {
        
        private val productListAdapter: ProductHomeListRecycleAdapter
        
        init {
                voucherBinding.rvProducts.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                productListAdapter = ProductHomeListRecycleAdapter(context, object : NSPageChangeCallback {
                        override fun onPageChange(pageNo: Int) {
                        
                        }
                    }, object : NSProductDetailCallback {
                        override fun onResponse(productDetail: ProductDataDTO) {
                            /*switchResultActivity(dataResult, NSProductsDetailActivity::class.java, bundleOf(
                                NSConstants.KEY_PRODUCT_DETAIL to Gson().toJson(productDetail), NSConstants.KEY_PRODUCT_FULL_LIST to Gson().toJson(
                                    NSProductListResponse(data = productModel.productList)
                                ), NSConstants.KEY_IS_FROM_ORDER to isFromOrderValue)
                            )
                            finish()*/
                        }
                    }, object : NSCartTotalAmountCallback {
                        override fun onResponse() {
                            //setTotalAmount()
                        }
                    })
                voucherBinding.rvProducts.adapter = productListAdapter
            }
            
        fun bind(response: NSCategoryData) {
            with(voucherBinding) {
                with(response) {
                    
                    try {
                        val drawable: Drawable? = ResourcesCompat.getDrawable(context.resources, context.resources.getIdentifier(categoryName?.replace(" ", "_")?.lowercase(), "drawable", context.packageName), null)
                        ivCategoryProduct.setImageDrawable(drawable)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    tvCategoryName.text = categoryName
                    productListAdapter.updateData(response.products)
                    tvViewAll.paintFlags = tvViewAll.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                    
					tvViewAll.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							onClickResponse.onResponse(response)
						}
					})
                }
            }
        }
    }
}
