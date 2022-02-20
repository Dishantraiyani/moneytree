package com.moneytree.app.ui.slots

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.databinding.NsFragmentSlotBinding
import com.moneytree.app.ui.royaltyInfo.NSRoyaltyInfoFragment

class NSSlotFragment : NSFragment() {
    private val slotListModel: NSSlotViewModel by lazy {
        ViewModelProvider(this).get(NSSlotViewModel::class.java)
    }
    private var _binding: NsFragmentSlotBinding? = null

    private val slotBinding get() = _binding!!
    private var slotListAdapter: NSSlotListRecycleAdapter? = null

    companion object {
        fun newInstance(bundle: Bundle?) = NSSlotFragment().apply {
            arguments = bundle
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            with(slotListModel) {
                strSlots = it.getString(NSConstants.KEY_SLOTS_INFO)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentSlotBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return slotBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(slotBinding) {
            with(layoutHeader) {
                clBack.visibility = View.VISIBLE
                tvHeaderBack.text = activity.resources.getString(R.string.slots)
                ivBack.visibility = View.VISIBLE
            }
            setSlotsAdapter()
        }
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(slotBinding) {
            with(layoutHeader) {
                clBack.setOnClickListener {
                    onBackPress()
                }
            }
        }
    }

    /**
     * To add data of register in list
     */
    private fun setSlotsAdapter() {
        with(slotBinding) {
            with(slotListModel) {
                rvSlotList.layoutManager = LinearLayoutManager(activity)
                slotListAdapter =
                    NSSlotListRecycleAdapter(activity)
                rvSlotList.adapter = slotListAdapter
                getSlotsListData(true)
            }
        }
    }

    /**
     * Set slot data
     *
     * @param isSlots when data available it's true
     */
    private fun setSlotsData(isSlots: Boolean) {
        with(slotListModel) {
            slotDataManage(isSlots)
            if (isSlots) {
                slotListAdapter!!.clearData()
                slotListAdapter!!.updateData(slotList)
            }
        }
    }

    /**
     * Slots data manage
     *
     * @param isSlotVisible when slots available it's visible
     */
    private fun slotDataManage(isSlotVisible: Boolean) {
        with(slotBinding) {
            rvSlotList.visibility = if (isSlotVisible) View.VISIBLE else View.GONE
            clSlotNotFound.visibility = if (isSlotVisible) View.GONE else View.VISIBLE
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(slotListModel) {
            isProgressShowing.observe(
                viewLifecycleOwner
            ) { shouldShowProgress ->
                updateProgress(shouldShowProgress)
            }

            isSlotDataAvailable.observe(
                viewLifecycleOwner
            ) { isSlot ->
                setSlotsData(isSlot)
            }
        }
    }
}