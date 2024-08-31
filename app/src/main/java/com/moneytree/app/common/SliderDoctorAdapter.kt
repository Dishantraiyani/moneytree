package com.moneytree.app.common

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.moneytree.app.R
import com.moneytree.app.databinding.RowSliderBinding
import com.moneytree.app.slider.SliderViewAdapter

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
		return mItems.size
	}

	inner class SliderAdapterVH(private val productBinding: RowSliderBinding) :
		ViewHolder(productBinding.root) {

		fun bind(response: String) {
			Glide.with(context).load(response).placeholder(R.drawable.placeholder).centerCrop().into(productBinding.image)
		}
	}
}
