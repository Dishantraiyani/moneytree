package com.moneytree.app.common

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.moneytree.app.R
import com.moneytree.app.databinding.RowSliderBinding
import com.smarteist.autoimageslider.SliderViewAdapter

class SliderDoctorAdapter(private val context: Context, private val mItems: List<String>) :
	SliderViewAdapter<SliderDoctorAdapter.SliderAdapterVH>() {

	override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
		val voucherView = RowSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return SliderAdapterVH(voucherView)
	}

	override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
		viewHolder.bind(mItems[position])
	}

	override fun getCount(): Int {
		//slider view count could be dynamic size
		return mItems.size
	}

	/**
	 * The view holder for voucher list
	 *
	 * @property productBinding The voucher list view binding
	 */
	inner class SliderAdapterVH(private val productBinding: RowSliderBinding) :
		ViewHolder(productBinding.root) {

		/**
		 * To bind the voucher details view into Recycler view with given data
		 *
		 * @param response The voucher details
		 */
		fun bind(response: String) {
			Glide.with(context).load(response).placeholder(R.drawable.placeholder).centerCrop().into(productBinding.image)
		}
	}
}
