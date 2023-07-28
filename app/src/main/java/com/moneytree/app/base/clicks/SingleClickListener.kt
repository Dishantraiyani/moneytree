package com.moneytree.app.base.clicks

import android.view.View

class SingleClickListener(private val onClickListener: () -> Unit) : View.OnClickListener {
    private var isClicked = false

    override fun onClick(v: View?) {
        if (!isClicked) {
            isClicked = true
            onClickListener.invoke()
        }
    }
}