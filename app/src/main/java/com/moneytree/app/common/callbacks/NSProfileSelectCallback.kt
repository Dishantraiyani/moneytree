package com.moneytree.app.common.callbacks

/**
 * The interface to listen the click on profile item
 */
interface NSProfileSelectCallback {

    /**
     * Invoked when the profile item click
     */
    fun onPosition(position: Int)
}