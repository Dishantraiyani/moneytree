package com.moneytree.app.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class ViewBindingAdapter<T : ViewBinding, D>(
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> T,
    private val onBind: (T, D) -> Unit
) : RecyclerView.Adapter<ViewBindingAdapter<T, D>.ViewHolder>() {

    private var data: MutableList<D> = mutableListOf()
    var isLoading = false
    var isLastPage = false
    private var onLoadMore: (() -> Unit)? = null

    fun getData(): MutableList<D> {
        return data
    }

    fun setData(newData: List<D>, isLastPage: Boolean) {
        this.isLastPage = isLastPage
        data.clear()
        data.addAll(newData.toMutableList())
        notifyDataSetChanged()
    }

    fun addData(newData: List<D>, isLastPage: Boolean) {
        this.isLastPage = isLastPage
        val previousSize = data.size
        data.addAll(newData.toMutableList())
        notifyItemRangeInserted(previousSize, newData.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = bindingInflater(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        onBind(holder.binding, item)

        // Check if we need to load more data when reaching the last item
        if (!isLoading && !isLastPage && position == data.size - 1) {
            isLoading = true
            onLoadMore?.invoke()
        }
    }

    override fun getItemCount(): Int = data.size

    fun setLoadingState(isLoadingData: Boolean) {
        this.isLoading = isLoadingData
    }

    fun setLastPageState(isLastPage: Boolean) {
        this.isLastPage = isLastPage
    }

    fun setOnLoadMoreListener(onLoadMore: (() -> Unit)?) {
        this.onLoadMore = onLoadMore
    }

    // Function to notify adapter that loading is finished, and it can load more data
    fun notifyLoadingFinished() {
        isLoading = false
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: T) : RecyclerView.ViewHolder(binding.root)
}