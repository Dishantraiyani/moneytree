package com.moneytree.app.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.callbacks.NSUserDataCallback
import com.moneytree.app.database.MainDatabase
import com.moneytree.app.databinding.FragmentMainBinding
import com.moneytree.app.repository.network.responses.NSDataUser
import com.moneytree.app.ui.home.NSHomeFragment
import com.moneytree.app.ui.mycart.products.NSProductFragment
import com.moneytree.app.ui.productCategory.MTProductCategoryFragment
import com.moneytree.app.ui.products.MTProductFragment
import com.moneytree.app.ui.profile.NSProfileFragment
import com.moneytree.app.ui.register.NSRegisterFragment
import com.moneytree.app.ui.wallets.NSWalletFragment
import com.onesignal.OneSignal
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainFragment : NSFragment() {
    private var _binding: FragmentMainBinding? = null
    private val mainBinding get() = _binding!!

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
		getUserDetail()
        viewCreated()
        setListener()
        return mainBinding.root
    }

	fun getUserDetail() {
		MainDatabase.getUserData(object : NSUserDataCallback {
			override fun onResponse(userDetail: NSDataUser) {
				OneSignal.sendTag("user_id_sponsor", userDetail.sponsorId)
                OneSignal.sendTag("user_id", userDetail.userName)
            }
		})
	}

    /**
     * View created
     */
    private fun viewCreated() {
        replaceFragment(NSHomeFragment.newInstance(), false, mainBinding.fragmentMainContainer.id)

        if (NSConstants.IS_LOGIN_SUCCESS) {
            NSConstants.IS_LOGIN_SUCCESS = false
            showSuccessDialog(
                requireActivity().resources.getString(R.string.app_name),
                "Welcome to MoneyTree"
            )
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(mainBinding) {
            navigationMenu.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.tb_home -> {
                        replaceFragment(NSHomeFragment.newInstance(), false, fragmentMainContainer.id)
                    }
                    R.id.tb_register -> {
                        replaceFragment(NSRegisterFragment.newInstance(), false, fragmentMainContainer.id)
					}
                    R.id.tb_shop -> {
						if (NSConstants.SOCKET_TYPE == null) {
							replaceFragment(
								MTProductCategoryFragment.newInstance(),
								false,
								fragmentMainContainer.id
							)
						} else {
							replaceFragment(
								NSProductFragment.newInstance(),
								false,
								fragmentMainContainer.id
							)
						}
                    }
                    R.id.tb_wallets -> {
                        replaceFragment(NSWalletFragment.newInstance(), false, fragmentMainContainer.id)
                    }
                    R.id.tb_profile -> {
                        replaceFragment(NSProfileFragment.newInstance(), false, fragmentMainContainer.id)
                    }
                }
                true
            }
        }
    }

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onNavigationMenuNameChange(event: NSChangeNavigationMenuNameEvent) {
		with(mainBinding) {
			if (NSConstants.SOCKET_TYPE == null) {
				navigationMenu.menu[2].setIcon(R.drawable.ic_product)
				navigationMenu.menu[2].setTitle(R.string.products)
			} else {
				navigationMenu.menu[2].setIcon(R.drawable.ic_store)
				navigationMenu.menu[2].setTitle(R.string.shop)
			}
		}
	}

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFragmentEvent(event: NSFragmentChange) {
        with(mainBinding) {
            replaceFragment(event.fragment, event.isBackStack, fragmentMainContainer.id)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTabChange(event: NSTabChange) {
        with(mainBinding) {
            navigationMenu.selectedItemId = event.tab
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBackPress(event: BackPressEvent) {
        NSLog.d(tags, "onBackPress: $event")
        with(mainBinding) {
            navigationMenu.selectedItemId = R.id.tb_home
        }
    }
}
