package com.moneytree.app.ui.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class NSWelcomeFragment : Fragment() {


    companion object {
        val LAYOUT_ID = "layoutId"

        fun newInstance(layoutId: Int): NSWelcomeFragment {
            val pane = NSWelcomeFragment()
            val bundle = Bundle()
            bundle.putInt(LAYOUT_ID, layoutId)
            pane.arguments = bundle
            return pane
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            requireArguments().getInt(LAYOUT_ID, -1),
            container,
            false
        ) as ViewGroup
    }
}