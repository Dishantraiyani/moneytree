package com.moneytree.app.ui.mycart.orders.brands

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.LayoutCheckboxSpinnerBinding
import com.moneytree.app.repository.network.responses.NSBrandData


class NSBrandFilterRecycleAdapter(
	val activity: Activity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	private val brandData: MutableList<NSBrandData> = arrayListOf()

	fun updateData(list: MutableList<NSBrandData>) {
		brandData.addAll(list)
		if (list.isValidList()) {
			notifyItemRangeChanged(0, brandData.size - 1)
		} else {
			notifyDataSetChanged()
		}
	}

	fun clearData() {
		brandData.clear()
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
			holder.bind(brandData[holder.absoluteAdapterPosition])
		}
	}

	override fun getItemCount(): Int {
		return brandData.size
	}

	inner class NSSpinnerViewHolder(private val binding: LayoutCheckboxSpinnerBinding) :
		RecyclerView.ViewHolder(binding.root) {

		fun bind(response: NSBrandData) {
			with(binding) {
				with(response) {
					tvItemCategories.text = brandName
					if (NSApplication.getInstance().isBrandFilterAvailable(response)) {
						cbItemCheck.isChecked = true
					}

					clCatItem.setOnClickListener {
						if (cbItemCheck.isChecked) {
							cbItemCheck.isChecked = false
							NSApplication.getInstance().removeBrandFilter(response)
						} else {
							cbItemCheck.isChecked = true
							NSApplication.getInstance().setBrandFilterList(response)
						}
					}
				}
			}
		}
	}
}
