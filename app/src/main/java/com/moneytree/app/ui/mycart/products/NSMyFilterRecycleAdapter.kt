package com.moneytree.app.ui.mycart.products

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.callbacks.NSPositiveCallback
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.LayoutCheckboxSpinnerBinding
import com.moneytree.app.repository.network.responses.NSCategoryData


class NSMyFilterRecycleAdapter(
	val activity: Activity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	private val categoryData: MutableList<NSCategoryData> = arrayListOf()

	fun updateData(list: MutableList<NSCategoryData>) {
		categoryData.addAll(list)
		if (list.isValidList()) {
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
		val voucherView =
			LayoutCheckboxSpinnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return NSSpinnerViewHolder(voucherView)
	}

	override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
		if (holderRec is NSSpinnerViewHolder) {
			val holder: NSSpinnerViewHolder = holderRec
			holder.bind(categoryData[holder.absoluteAdapterPosition])
		}
	}

	override fun getItemCount(): Int {
		return categoryData.size
	}

	/**
	 * The view holder for voucher list
	 *
	 * @property productBinding The voucher list view binding
	 */
	inner class NSSpinnerViewHolder(private val productBinding: LayoutCheckboxSpinnerBinding) :
		RecyclerView.ViewHolder(productBinding.root) {

		/**
		 * To bind the voucher details view into Recycler view with given data
		 *
		 * @param response The voucher details
		 */
		fun bind(response: NSCategoryData) {
			with(productBinding) {
				with(response) {
					tvItemCategories.text = categoryName
					if (NSApplication.getInstance().isFilterAvailable(response)) {
						cbItemCheck.isChecked = true
					}

					clCatItem.setOnClickListener {
						if (cbItemCheck.isChecked) {
							cbItemCheck.isChecked = false
							NSApplication.getInstance().removeFilter(response)
						} else {
							cbItemCheck.isChecked = true
							NSApplication.getInstance().setFilterList(response)
						}
					}
				}
			}
		}
	}
}
