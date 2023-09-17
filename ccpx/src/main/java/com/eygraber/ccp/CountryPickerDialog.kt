package com.eygraber.ccp

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

@Suppress("DEPRECATION")
internal class CountryPickerDialog(
        countryCodePicker: CountryCodePicker,
        countries: List<Country>
) {
  private val context = countryCodePicker.context
  private val ccpAttrs = countryCodePicker.ccpAttrs

  private val dialog: AlertDialog =
          AlertDialog
                  .Builder(context).apply {
                    setCancelable(true)
                    setView(R.layout.dialog_picker)
                  }.show()


  private val searchView = dialog.findViewById<EditText>(R.id.search_edt)!!
  private val recyclerView = dialog.findViewById<RecyclerView>(R.id.countryList)!!
  private val emptyView = dialog.findViewById<TextView>(R.id.emptyResults)!!
  private val countryAdapter = CountryAdapter(context, countryCodePicker, countries, ccpAttrs, CountryUtils()) { newList ->
    recyclerView.isVisible = newList.isNotEmpty()

    emptyView.isVisible = newList.isEmpty()
  }.apply {
    setHasStableIds(true)
  }

  private val cancelRunnable = CancelSearchRunnable()

  init {
    val metrics = DisplayMetrics()
    (context as Activity).windowManager.defaultDisplay.getMetrics(metrics)
    dialog.window!!.setGravity(Gravity.BOTTOM)
    dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (metrics.heightPixels * 0.97).toInt()) // here i have fragment height 30% of window's height you can set it as per your requirement
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.window!!.attributes.windowAnimations = R.style.DialogAnimationUpDown

    with(searchView) {
      isVisible = ccpAttrs.dialogShowSearch
      hint = ccpAttrs.dialogSearchHint

      if(ccpAttrs.dialogShowSearch) {
        observeSearch()
      }
    }

    with(recyclerView) {
      post {
        layoutManager = LinearLayoutManager(context)
        adapter = countryAdapter
        itemAnimator = null
      }
    }

    emptyView.text = ccpAttrs.dialogEmptyViewText
  }

  fun close() {
    dialog.cancel()
  }

  private fun EditText.observeSearch() {
    addTextChangedListener {
      removeCallbacks(cancelRunnable)

      val search = it?.trim() ?: ""


      postDelayed(
              SearchRunnable {
                countryAdapter.updateSearch(search)
              }, 500
      )
    }
  }

  private class SearchRunnable(
          private val updateSearch: () -> Unit
  ) : Runnable {
    override fun run() {
      updateSearch()
    }

    override fun hashCode(): Int = 0
    override fun equals(other: Any?): Boolean = other is SearchRunnable || other is CancelSearchRunnable
  }

  private class CancelSearchRunnable : Runnable {
    override fun run() {}
    override fun hashCode(): Int = 0
    override fun equals(other: Any?): Boolean = other is SearchRunnable || other is CancelSearchRunnable
  }
}

private fun RecyclerView.smoothSnapToPosition(position: Int, snapMode: Int = LinearSmoothScroller.SNAP_TO_START) {
  val smoothScroller = object : LinearSmoothScroller(this.context) {
    override fun getVerticalSnapPreference(): Int {
      return snapMode
    }

    override fun getHorizontalSnapPreference(): Int {
      return snapMode
    }
  }
  smoothScroller.targetPosition = position
  layoutManager?.startSmoothScroll(smoothScroller)
}
