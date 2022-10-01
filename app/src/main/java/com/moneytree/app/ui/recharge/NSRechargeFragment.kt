package com.moneytree.app.ui.recharge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.google.android.material.tabs.TabLayout
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.databinding.NsFragmentRechargeBinding
import com.moneytree.app.ui.recharge.mobile.NSMobileRechargeFragment


class NSRechargeFragment : NSFragment() {
    private var _binding: NsFragmentRechargeBinding? = null
    private val rgBinding get() = _binding!!
    var fieldName: Array<String> = arrayOf()
	var rechargeSelectedType: String? = ""

	companion object {
		fun newInstance(bundle: Bundle?) = NSRechargeFragment().apply {
			arguments = bundle
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			rechargeSelectedType = it.getString(NSConstants.KEY_RECHARGE_TYPE)
		}
	}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentRechargeBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return rgBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(rgBinding) {
            with(layoutHeader) {
                clBack.visibility = View.VISIBLE
                tvHeaderBack.text = activity.resources.getString(R.string.recharge)
                ivBack.visibility = View.VISIBLE
            }
            setFragmentData()
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(rgBinding) {
            with(layoutHeader) {
                clBack.setOnClickListener {
                    onBackPress()
                }
            }
        }
    }

    private fun setFragmentData() {
        fieldName = resources.getStringArray(R.array.recharge_list_final)
		rgBinding.tabLayout.removeAllTabs()

		var indexValue = 0
		fieldName.forEachIndexed { index, strData ->
			rgBinding.tabLayout.addTab(rgBinding.tabLayout.newTab().setText(strData))
			if (rechargeSelectedType.equals(strData)) {
				indexValue = index
				replaceFragment(NSMobileRechargeFragment.newInstance(bundleOf(NSConstants.KEY_RECHARGE_TYPE to fieldName[index])), false, R.id.recharge_container_view)
			}
		}
		rgBinding.tabLayout.getTabAt(indexValue)?.select()
		rgBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
			override fun onTabSelected(tab: TabLayout.Tab) {
				replaceFragment(NSMobileRechargeFragment.newInstance(bundleOf(NSConstants.KEY_RECHARGE_TYPE to fieldName[tab.position])), false, R.id.recharge_container_view)
			}

			override fun onTabUnselected(tab: TabLayout.Tab?) {

			}

			override fun onTabReselected(tab: TabLayout.Tab?) {

			}

		})
    }
}
