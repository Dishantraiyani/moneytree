package com.moneytree.app.ui.mycart.address.selectAddress

import android.app.Activity
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.moneytree.app.BuildConfig
import com.moneytree.app.R
import com.moneytree.app.base.adapter.BaseViewBindingAdapter
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.callbacks.NSCartTotalAmountCallback
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.setCircleImage
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.LayoutAddressBinding
import com.moneytree.app.databinding.LayoutCartItemBinding
import com.moneytree.app.databinding.LayoutDoctorItemBinding
import com.moneytree.app.repository.network.responses.DoctorDataItem
import com.moneytree.app.repository.network.responses.NSAddressCreateResponse
import com.moneytree.app.repository.network.responses.ProductDataDTO

class NSAddressListRecycleAdapter(
	private val callback: ((NSAddressCreateResponse, Boolean, Boolean, Int) -> Unit)
) : BaseViewBindingAdapter<LayoutAddressBinding, NSAddressCreateResponse>(

	bindingInflater = { inflater, parent, attachToParent ->
		LayoutAddressBinding.inflate(inflater, parent, attachToParent)
	},

	onBind = { binding, response, position, size ->
		binding.apply {
			response.apply {
				etUserName.text = fullName

				val list: MutableList<String> = arrayListOf()
				list.add(flatHouse)
				list.add(area)
				list.add(landMark)
				list.add("$city,$state,$country")

				var address = ""
				for (data in list) {
					if (data.isNotEmpty()) {
						address = if (address.isEmpty()) {
							data
						} else {
							address + "\n" + data
						}
					}
				}

				tvAddress.text = address
				tvAddress.visible()

				val isSelected = NSApplication.getInstance().getSelectedAddress()
				rbChecked.isChecked = isSelected.addressId == addressId

				if (rbChecked.isChecked) {
					btnEditAddress.text = "Edit Address"
					btnEditAddress.visible()
				} else {
					btnEditAddress.gone()
				}

				ivDelete.setSafeOnClickListener {
					callback.invoke(response, false, true, position)
				}

				btnEditAddress.setSafeOnClickListener {
					callback.invoke(response, true, false, position)
				}

				cardAddress.setSafeOnClickListener {
					callback.invoke(response, false, false, position)
				}
			}
		}
	}
)