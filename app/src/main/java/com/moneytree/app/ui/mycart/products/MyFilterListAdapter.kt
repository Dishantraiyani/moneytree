package com.moneytree.app.ui.mycart.products

import android.content.Context
import com.moneytree.app.repository.network.responses.NSCategoryData
import android.widget.ArrayAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.moneytree.app.R
import android.widget.TextView
import android.widget.CheckBox
import com.moneytree.app.common.NSApplication

class MyFilterListAdapter(
    private val mContext: Context,
    resource: Int,
    private var listState: MutableList<NSCategoryData>
) : ArrayAdapter<NSCategoryData?>(
    mContext, resource, listState as List<NSCategoryData?>
) {
    private var isFromView = false

	override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
		return getCustomView(position, convertView, parent)
	}

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup?): View {
		val layoutInflater = LayoutInflater.from(mContext)
		var cView = convertView
		cView = layoutInflater.inflate(R.layout.layout_checkbox_spinner, null)
		val holder = ViewHolder()
		holder.mTextView = cView.findViewById(R.id.tv_item_categories)
		holder.mCheckBox = cView.findViewById(R.id.cb_item_check)
        holder.mTextView!!.text = listState[position].categoryName

        // To check weather checked event fire from getview() or user input
        // isFromView = true
        val isChecked = NSApplication.getInstance().isFilterAvailable(listState[position])
        holder.mCheckBox!!.isChecked = isChecked
        isFromView = false
        if (position == 0) {
            holder.mCheckBox!!.visibility = View.INVISIBLE
        } else {
            holder.mCheckBox!!.visibility = View.VISIBLE
        }
        holder.mCheckBox!!.tag = position
        holder.mCheckBox!!.setOnCheckedChangeListener { _, _ ->
            if (!isFromView) {
				NSApplication.getInstance().setFilterList(listState[position])
            }
        }
        return cView!!
    }

	fun updateData(list: MutableList<NSCategoryData>) {
		listState.clear()
		listState = list
		notifyDataSetChanged()
	}

    private inner class ViewHolder {
        var mTextView: TextView? = null
        var mCheckBox: CheckBox? = null
    }
}
