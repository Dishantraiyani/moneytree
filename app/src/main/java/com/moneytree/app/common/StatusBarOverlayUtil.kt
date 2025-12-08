package com.moneytree.app.common

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

object StatusBarOverlayUtil {
	
	private const val TAG = "status_bar_overlay_view"
	
	fun apply(
		activity: Activity,
		views: ViewGroup,
		@ColorRes colorRes: Int,
		isPaddingBottom: Boolean = true
	) {
		val window = activity.window
		val decorView = window.decorView as ViewGroup
		val color = ContextCompat.getColor(activity, colorRes)
		
		WindowCompat.setDecorFitsSystemWindows(window, false)
		
		val overlay = decorView.findViewWithTag<View>(TAG) ?: View(activity).apply {
			tag = TAG
			setBackgroundColor(color)
			layoutParams = ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				0
			)
			decorView.addView(this)   // ✅ now inside real system bar layer
		}
		
		overlay.setBackgroundColor(color)
		overlay.bringToFront()
		
		val contentRoot =
			activity.findViewById<ViewGroup>(android.R.id.content)
		
		ViewCompat.setOnApplyWindowInsetsListener(decorView) { _, insets ->
			
			val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
			
			val lp = overlay.layoutParams
			if (lp.height != bars.top) {
				lp.height = bars.top
				overlay.layoutParams = lp
			}
			
			contentRoot.setPadding(
				bars.left,
				bars.top,
				bars.right,
				if (isPaddingBottom) bars.bottom else 0
			)
			
			insets
		}
	}
}
