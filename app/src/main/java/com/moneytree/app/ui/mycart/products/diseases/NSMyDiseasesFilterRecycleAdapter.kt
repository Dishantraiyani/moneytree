package com.moneytree.app.ui.mycart.products.diseases

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.LayoutCheckboxSpinnerBinding
import com.moneytree.app.repository.network.responses.NSDiseasesData


class NSMyDiseasesFilterRecycleAdapter(
	val activity: Activity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	private val diseasesData: MutableList<NSDiseasesData> = arrayListOf()

	fun updateData(list: MutableList<NSDiseasesData>) {
		diseasesData.addAll(list)
		if (list.isValidList()) {
			notifyItemRangeChanged(0, diseasesData.size - 1)
		} else {
			notifyDataSetChanged()
		}
	}

	fun clearData() {
		diseasesData.clear()
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
			holder.bind(diseasesData[holder.absoluteAdapterPosition])
		}
	}

	override fun getItemCount(): Int {
		return diseasesData.size
	}

	inner class NSSpinnerViewHolder(private val binding: LayoutCheckboxSpinnerBinding) :
		RecyclerView.ViewHolder(binding.root) {

		fun bind(response: NSDiseasesData) {
			with(binding) {
				with(response) {
					tvItemCategories.text = diseasesName
					if (NSApplication.getInstance().isDiseasesFilterAvailable(response)) {
						cbItemCheck.isChecked = true
					}

					clCatItem.setOnClickListener {
						if (cbItemCheck.isChecked) {
							cbItemCheck.isChecked = false
							NSApplication.getInstance().removeDiseasesFilter(response)
						} else {
							cbItemCheck.isChecked = true
							NSApplication.getInstance().setDiseasesFilterList(response)
						}
					}
				}
			}
		}
	}
}
