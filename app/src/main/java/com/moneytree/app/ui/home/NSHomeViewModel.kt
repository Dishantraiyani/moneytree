package com.moneytree.app.ui.home

import android.app.Application
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.callbacks.NSUserDataCallback
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.database.MainDatabase
import com.moneytree.app.repository.NSCheckVersionRepository
import com.moneytree.app.repository.NSDashboardRepository
import com.moneytree.app.repository.NSUserRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.*
import com.moneytree.app.ui.slide.SliderFragment


/**
 * The view model class for home. It handles the business logic to communicate with the model for the home and provides the data to the observing UI component.
 */
class NSHomeViewModel(application: Application) : NSViewModel(application) {
    var isDashboardDataAvailable = MutableLiveData<Boolean>()
    var dashboardData: NSDashboardResponse? = null
    var chekVersionLiveData = MutableLiveData<NSCheckVersionResponse>()
    var chekVersionResponse: NSCheckVersionResponse? = null
    var strHomeData : String? = null
    private var homeDetail: NSNotificationListData? = null
    var nsUserData: NSDataUser? = null
    var isUserDataAvailable = MutableLiveData<Boolean>()
    val mFragmentList: MutableList<String> = ArrayList()
    var fieldName: Array<String> = arrayOf()
	var isLogout = MutableLiveData<Boolean>()
    var fieldImage = arrayOf(
        R.drawable.ic_mobile_ico,
        R.drawable.ic_dth,
        R.drawable.ic_cable,
        R.drawable.ic_fast_tag,
        R.drawable.ic_broadband,
        R.drawable.ic_gas,
        R.drawable.ic_electricity,
        R.drawable.ic_emi,
        R.drawable.ic_insurance_ico,
        R.drawable.ic_lic_ico,
		R.drawable.ic_landline,
		R.drawable.ic_lpg,
		R.drawable.ic_water
    )

    /**
     * To get the home detail
     */
    fun getHomeDetail() {
        if (!strHomeData.isNullOrEmpty()) {
            homeDetail = Gson().fromJson(strHomeData, NSNotificationListData::class.java)
        }
    }

    fun getUserDetail() {
        MainDatabase.getUserData(object : NSUserDataCallback {
            override fun onResponse(userDetail: NSDataUser) {
                nsUserData = userDetail
                isUserDataAvailable.value = true
            }
        })
    }

    /**
     * Get dashboard data
     *
     * @param isShowProgress The progress dialog show check
     */
    fun getDashboardData(isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        NSDashboardRepository.dashboardData(object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                isProgressShowing.value = false
                val dashboardMainData = data as NSDashboardResponse
                dashboardData = dashboardMainData
				setFragmentData()

            }

            override fun onError(errors: List<Any>) {
                isDashboardDataAvailable.value = false
                handleError(errors)
            }

            override fun onFailure(failureMessage: String?) {
                isDashboardDataAvailable.value = false
                handleFailure(failureMessage)
            }

            override fun <T> onNoNetwork(localData: T) {
                isDashboardDataAvailable.value = false
                handleNoNetwork()
            }
        })
    }

    fun setFragmentData() {
		/*dashboardData!!.data!!.banners.add("https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg")
		dashboardData!!.data!!.banners.add("https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg")
		dashboardData!!.data!!.banners.add("https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg")
*/
        mFragmentList.clear()
		if (dashboardData != null) {
			if (dashboardData!!.data != null) {
				if (dashboardData!!.data!!.banners.isValidList()) {
					dashboardData!!.data!!.banners.forEachIndexed { index, data ->
						mFragmentList.add(data)
					}
				}
			}
		}
		isDashboardDataAvailable.value = true
    }

    fun setDownLine(): String {
        with(dashboardData) {
            with(this?.data!!) {
                return if (dwnTotal.isValidList()) {
                    dwnTotal?.get(0)?.cnt ?: "0"
                } else {
                    "0"
                }
            }
        }
    }

    fun setVoucher(): String {
        with(dashboardData) {
            with(this?.data!!) {
                return if (voucherTotal.isValidList()) {
                    voucherTotal?.get(0)?.cnt ?: "0"
                } else {
                    "0"
                }
            }
        }
    }

    fun setJoinVoucher(): String {
        with(dashboardData) {
            with(this?.data!!) {
                return if (availableJoiningVoucher.isValidList()) {
                    availableJoiningVoucher?.get(0)?.cnt ?: "0"
                } else {
                    "0"
                }
            }
        }
    }

    fun setWallet(): String {
        with(dashboardData) {
            with(this?.data!!) {
                return if (wltAmt.isValidList()) {
                    wltAmt?.get(0)?.amount ?: "0"
                } else {
                    "0"
                }
            }
        }
    }

	fun getPopUpImage(): String {
		with(dashboardData) {
			with(this?.data!!) {
				if (popupImg != null) {
					if (popupImg.isNotEmpty()) {
						return popupImg
					} else {
						return ""
					}
				} else {
					return ""
				}
			}
		}
	}

    fun setEarningAmount(): String {
        with(dashboardData) {
            with(this?.data!!) {
                return earningAmount.ifEmpty {
                    "0"
                }
            }
        }
    }

    fun setRoyaltyStatus(): String {
        with(dashboardData) {
            with(this?.data!!) {
                return royaltyName!!.ifEmpty {
                    "Not Available"
                }
            }
        }
    }

    /**
     * check version data
     *
     */
    fun checkVersion() {
        NSCheckVersionRepository.checkVersionData(object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                val checkVersionMainData = data as NSCheckVersionResponse
                chekVersionResponse = checkVersionMainData
                chekVersionLiveData.postValue(checkVersionMainData)
            }

            override fun onError(errors: List<Any>) {

            }

            override fun onFailure(failureMessage: String?) {

            }

            override fun <T> onNoNetwork(localData: T) {

            }
        })
    }

	/**
	 * logout data
	 *
	 */
	fun logout() {
		NSUserRepository.logout(object : NSGenericViewModelCallback {
			override fun <T> onSuccess(data: T) {
				isLogout.value = true
			}

			override fun onError(errors: List<Any>) {
				handleError(errors)
			}

			override fun onFailure(failureMessage: String?) {
				handleFailure(failureMessage)
			}

			override fun <T> onNoNetwork(localData: T) {
				handleNoNetwork()
			}
		})
	}
}
