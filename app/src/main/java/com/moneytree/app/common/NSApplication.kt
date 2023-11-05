package com.moneytree.app.common

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.moneytree.app.database.MainDatabase
import com.moneytree.app.repository.network.manager.NSApiManager
import com.moneytree.app.repository.network.responses.NSAddressCreateResponse
import com.moneytree.app.repository.network.responses.NSBrandData
import com.moneytree.app.repository.network.responses.NSCategoryData
import com.moneytree.app.repository.network.responses.NSDiseasesData
import com.moneytree.app.repository.network.responses.ProductDataDTO
import com.onesignal.OneSignal

/**
 * The MoneyTree application class containing Preference, network manager and functionality
 * to be used across modules
 */
class NSApplication : Application() {
    private lateinit var preferences: NSPreferences
    private lateinit var preferencesLogin: NSLoginPreferences
    private lateinit var apiManager: NSApiManager
    private lateinit var walletBalance: String
	private val ONESIGNAL_APP_ID = "a31a4b92-4cf4-4768-ba20-904945d2159e"
	private var productList: HashMap<String, ProductDataDTO> = hashMapOf()
	private var orderList: HashMap<String, ProductDataDTO> = hashMapOf()
	private var filterProduct: HashMap<String, String> = hashMapOf()
	private var diseasesProduct: HashMap<String, String> = hashMapOf()
	private var brandProduct: HashMap<String, String> = hashMapOf()
	private var selectedAddress: NSAddressCreateResponse = NSAddressCreateResponse()
	private var kycKey: String = ""

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        initInstance()

		OneSignal.initWithContext(this)
		OneSignal.setAppId(ONESIGNAL_APP_ID)
		OneSignal.unsubscribeWhenNotificationsAreDisabled(true)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    /**
     * To initialize global instances that be used across modules
     */
    private fun initInstance() {
        instance = this
        preferences = NSPreferences(this)
        preferencesLogin = NSLoginPreferences(this)
        apiManager = NSApiManager()
        MainDatabase.appDatabase(applicationContext)
        isAlertShown = false
    }

    /**
     * To get instances of shared preferences
     */
    fun getPrefs(): NSPreferences = preferences

	/**
	 * To get instances of shared login preferences
	 */
	fun getLoginPrefs(): NSLoginPreferences = preferencesLogin

    /**
     * To get instance of Api manager
     */
    fun getApiManager(): NSApiManager = apiManager

	/**
	 * To get instances of shared preferences
	 */
	fun getWalletBalance(): String = walletBalance

	fun setWalletBalance(balance: String) {
		walletBalance = balance
	}

	fun setProductList(model: ProductDataDTO) {
		val key = model.productId + "_" + model.categoryId
		productList[key] = model
	}

	fun setKycKey(key: String) {
		kycKey = key
	}

	fun getKycKey(): String {
		return kycKey
	}

	fun clearProductList() {
		productList.clear()
	}

	fun clearOrderList() {
		orderList.clear()
	}

	fun addSelectedAddress(address: NSAddressCreateResponse) {
		selectedAddress = address
	}

	fun getSelectedAddress(): NSAddressCreateResponse {
		return selectedAddress
	}

	fun setOrderList(model: ProductDataDTO) {
		val key = model.productId + "_" + model.categoryId
		orderList[key] = model
	}

	fun clearSelectedOrderProductList(isFromOrder: Boolean) {
		val orderList: HashMap<String, ProductDataDTO> = hashMapOf()
		for ((key, value) in productList.entries) {
			if (isFromOrder && value.isFromOrder) {
				orderList[key] = value
			} else if (!isFromOrder && !value.isFromOrder) {
				orderList[key] = value
			}
		}
		productList.clear()
		productList.putAll(orderList)
	}

	fun getProductList(): MutableList<ProductDataDTO> {
		val list: MutableList<ProductDataDTO> = arrayListOf()
		for ((key, value) in productList.entries) {
			list.add(value)
		}
		return list
	}

	fun getOrderList(): MutableList<ProductDataDTO> {
		val list: MutableList<ProductDataDTO> = arrayListOf()
		for ((key, value) in orderList.entries) {
			list.add(value)
		}
		return list
	}

	fun getOrder(model: ProductDataDTO): ProductDataDTO? {
		val key = model.productId + "_" + model.categoryId
		return orderList[key]
	}

	fun removeOrder(model: ProductDataDTO): MutableList<ProductDataDTO> {
		val key = model.productId + "_" + model.categoryId
		orderList.remove(key)
		return getOrderList()
	}

	fun isOrderAdded(model: ProductDataDTO) : Boolean {
		val key = model.productId + "_" + model.categoryId
		return orderList.contains(key)
	}

	fun getProduct(model: ProductDataDTO): ProductDataDTO? {
		val key = model.productId + "_" + model.categoryId
		return productList[key]
	}

	fun removeProduct(model: ProductDataDTO): MutableList<ProductDataDTO> {
		val key = model.productId + "_" + model.categoryId
		productList.remove(key)
		return getProductList()
	}

	fun isProductAdded(model: ProductDataDTO) : Boolean {
		val key = model.productId + "_" + model.categoryId
		return productList.contains(key)
	}

	fun getFilterList(): ArrayList<String> {
		val list: ArrayList<String> = arrayListOf()
		for ((key, value) in filterProduct.entries) {
			list.add(key)
		}
		return list
	}

	fun setFilterList(categoryData: NSCategoryData) {
		val key = categoryData.categoryId!!
		filterProduct[key] = categoryData.categoryName!!
	}

	fun removeFilter(model: NSCategoryData): ArrayList<String> {
		val key = model.categoryId
		filterProduct.remove(key)
		return getFilterList()
	}

	fun isFilterAvailable(model: NSCategoryData) : Boolean {
		val key = model.categoryId
		return filterProduct.contains(key)
	}

	fun clearFilter(){
		filterProduct.clear()
	}

	fun clearDiseasesFilter(){
		diseasesProduct.clear()
	}

	fun clearBrandFilter(){
		brandProduct.clear()
	}

	fun getDiseasesFilterList(): ArrayList<String> {
		val list: ArrayList<String> = arrayListOf()
		for ((key, value) in diseasesProduct.entries) {
			list.add(key)
		}
		return list
	}

	fun setDiseasesFilterList(diseasesData: NSDiseasesData) {
		val key = diseasesData.diseasesId!!
		diseasesProduct[key] = diseasesData.diseasesName!!
	}

	fun removeDiseasesFilter(model: NSDiseasesData): ArrayList<String> {
		val key = model.diseasesId
		diseasesProduct.remove(key)
		return getDiseasesFilterList()
	}

	fun isDiseasesFilterAvailable(model: NSDiseasesData) : Boolean {
		val key = model.diseasesId
		return diseasesProduct.contains(key)
	}

	fun isBrandFilterAvailable(model: NSBrandData) : Boolean {
		val key = model.brandId
		return brandProduct.contains(key)
	}

	fun removeBrandFilter(model: NSBrandData): ArrayList<String> {
		val key = model.brandId
		brandProduct.remove(key)
		return getBrandFilterList()
	}

	fun getBrandFilterList(): ArrayList<String> {
		val list: ArrayList<String> = arrayListOf()
		for ((key, value) in brandProduct.entries) {
			list.add(key)
		}
		return list
	}

	fun setBrandFilterList(diseasesData: NSBrandData) {
		val key = diseasesData.brandId!!
		brandProduct[key] = diseasesData.brandName!!
	}

    companion object {
        private lateinit var instance: NSApplication
        private var isNetworkConnected: Boolean = false

        // App opening alert shown status
        var isAlertShown: Boolean = false

        /**
         * To get the application instance
         *
         * @return The application instance
         */
        fun getInstance(): NSApplication = instance

        /**
         * To check the internet connection status
         *
         * @return Whether internet connected or not
         */
        fun isNetworkConnected(): Boolean = isOnline(getInstance().applicationContext)

		/**
		 * To start the network connection listener
		 */
		private fun isOnline(context: Context): Boolean {
			val connectivityManager =
				context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
			val capabilities =
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
				} else {
					@Suppress("DEPRECATION") val networkInfo =
						connectivityManager.activeNetworkInfo ?: return false
					@Suppress("DEPRECATION")
					return networkInfo.isConnected
				}
			if (capabilities != null) {
				if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
					return true
				} else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
					return true
				} else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
					return true
				}
			}
			return false
		}
    }
}
