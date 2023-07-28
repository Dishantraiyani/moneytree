package com.moneytree.app.common.callbacks

import com.moneytree.app.repository.network.responses.SearchData

/**
 * The interface to listen for change pages
 */
interface NSSearchResponseCallback {
    fun onSearch(searchList: MutableList<SearchData>)
}