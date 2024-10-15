package com.moneytree.app.ui.home

import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.R
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSLoginRegisterEvent
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.callbacks.NSUserDataCallback
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.database.MainDatabase
import com.moneytree.app.repository.NSCheckVersionRepository
import com.moneytree.app.repository.NSDashboardRepository
import com.moneytree.app.repository.NSKycRepository
import com.moneytree.app.repository.NSUserRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.*
import org.greenrobot.eventbus.EventBus


/**
 * The view model class for home. It handles the business logic to communicate with the model for the home and provides the data to the observing UI component.
 */
class NSHomeViewModel(application: Application) : NSViewModel(application) {
    var isDashboardDataAvailable = MutableLiveData<Boolean>()
    var dashboardData: NSDashboardResponse? = null
    var chekVersionLiveData = MutableLiveData<NSCheckVersionResponse>()
    var chekVersionResponse: NSCheckVersionResponse? = null
    var isUserDataAvailable = MutableLiveData<NSDataUser>()
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


    fun getUserDetail() {
        MainDatabase.getUserData(object : NSUserDataCallback {
            override fun onResponse(userDetail: NSDataUser) {
                isUserDataAvailable.value = userDetail
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

                val pref = NSApplication.getInstance().getPrefs()
                pref.isRechargeDisplay = dashboardData?.data?.isRechargeDisplay
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

    fun getKycKey() {
        callCommonApi({ obj ->
            NSKycRepository.getKycKey(obj)
        }, { data, isSuccess ->
        })
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
                NSConstants.WALLET_BALANCE = if (wltAmt.isValidList()) {
                    wltAmt?.get(0)?.amount ?: "0"
                } else {
                    "0"
                }
				return NSConstants.WALLET_BALANCE
            }
        }
    }

	fun getSocketType(): String? {
		with(dashboardData) {
			with(this?.data!!) {
				return if (stockiestType?.isNotEmpty() == true) {
					stockiestType
				} else {
					null
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

    fun setKycStatus(activity: Activity) {
        with(dashboardData) {
            NSApplication.getInstance().getPrefs().isKycVerified = this?.data?.kycStatus
            NSUtilities.checkUserVerified(activity) {

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

    fun getDirectSettings() {
        callCommonApi({ obj ->
            NSUserRepository.getDirectSetting(obj)
        }, { data, isSuccess ->
            if (isSuccess) {
                if (data is NSDirectSettingResponse) {
                    NSApplication.getInstance().getPrefs().rewardCoinPeriod = data.rewardCoinPeriod
                }
            }
        })
    }
	
	fun getProfile(isShowProgress: Boolean, callback: (NSDataUser) -> Unit) {
		if (isShowProgress) showProgress()
		NSUserRepository.getProfile(object : NSGenericViewModelCallback {
			override fun <T> onSuccess(data: T) {
				hideProgress()
				val user = data as NSUserResponse
				val users = user.data?: NSDataUser()
				MainDatabase.insertUserData(users, object : NSUserDataCallback {
						override fun onResponse(userDetail: NSDataUser) {
						
						}
					})
				callback.invoke(users)
			}
			
			override fun onError(errors: List<Any>) {
				hideProgress()
				handleError(errors)
			}
			
			override fun onFailure(failureMessage: String?) {
				hideProgress()
				handleFailure(failureMessage)
			}
			
			override fun <T> onNoNetwork(localData: T) {
				hideProgress()
				handleNoNetwork()
			}
		})
	}
}
