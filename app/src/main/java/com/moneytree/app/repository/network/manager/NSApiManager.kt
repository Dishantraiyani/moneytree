package com.moneytree.app.repository.network.manager

import android.os.Build
import com.google.gson.GsonBuilder
import com.moneytree.app.BuildConfig
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSUserManager
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.requests.*
import com.moneytree.app.repository.network.responses.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * The manager class through which we can access the web service.
 */
class NSApiManager {
	companion object {
		private const val KEY_CONTENT_TYPE = "content-type"
		private const val KEY_ACCEPT = "Accept"
		private const val ACCEPT_VALUE = "*/*"
		private const val MULTIPART_JSON = "multipart/form-data"
		private const val APPLICATION_JSON = "application/json"
		private const val AUTHORISATION_KEY = "Authorization"
		private const val BEARER = "Bearer "

		private const val TIMEOUT: Long = 30

		val unAuthorised3020Client: RTApiInterface by lazy {
			buildRetrofit(unAuthorisedOkHttpClient, "").create(
				RTApiInterface::class.java
			)
		}

		val youtubeClient: RTApiInterface by lazy {
			buildRetrofitYoutube(unAuthorisedOkHttpClient, "").create(
				RTApiInterface::class.java
			)
		}

		/**
		 * OkHttpClient for the Authorised user
		 */
		private val authorizedOkHttpClient by lazy {
			generateOkHttpClient(
				isAuthorizedClient = true,
				isMultiPart = false
			)
		}


		/**
		 * OkHttpClient for the unAuthorised user
		 */
		private val unAuthorisedOkHttpClient by lazy {
			generateOkHttpClient(
				isAuthorizedClient = false,
				isMultiPart = false
			)
		}

		/**
		 * To provide a http client to send requests to authenticated API
		 *
		 * @param isAuthorizedClient Whether the client is needed for making authenticated or un authenticated API
		 *
		 * @return The http client
		 */
		private fun generateOkHttpClient(
			isAuthorizedClient: Boolean,
			isMultiPart: Boolean
		): OkHttpClient =
			OkHttpClient().newBuilder().apply {
				readTimeout(TIMEOUT, TimeUnit.SECONDS)
				connectTimeout(TIMEOUT, TimeUnit.SECONDS)
				addInterceptor { chain ->
					chain.proceed(
						getRequest(
							chain.request(), isAuthorizedClient, isMultiPart
						)
					)
				}
				if (isAuthorizedClient) {
					addInterceptor(RTAuthorizationInterceptor())
				}
				if (BuildConfig.DEBUG) {
					addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
				}
			}.build()

		/**
		 * To builds the Request header
		 *
		 * @param request            request sent by user
		 * @param isAuthorizedClient boolean to create appropriate header with or without authorisation token
		 * @return Request with the header loaded
		 * @throws IOException possibility of throwing IOException so handled
		 */
		@Throws(IOException::class)
		fun getRequest(
			request: Request,
			isAuthorizedClient: Boolean,
			isMultiPart: Boolean
		): Request =
			request.newBuilder().apply {
				if (isMultiPart) {
					header(KEY_CONTENT_TYPE, MULTIPART_JSON)
				} else {
					header(KEY_CONTENT_TYPE, APPLICATION_JSON)
				}
				header(KEY_ACCEPT, ACCEPT_VALUE)
				if (isAuthorizedClient) {
					header(
						AUTHORISATION_KEY, BEARER + NSUserManager.getAuthToken()
					)
				}
			}.build()

		/**
		 * To builds the retrofit client with baseUrl and Client sent
		 *
		 * @param okHttpClient Client with request and header details
		 * @return Retrofit reference retrofit builder
		 */
		private fun buildRetrofit(okHttpClient: OkHttpClient, endpoint: String): Retrofit =
			Retrofit.Builder().apply {
				baseUrl(NSUtilities.decrypt(BuildConfig.BASE_URL) + endpoint)
				client(okHttpClient)
				addConverterFactory(
					GsonConverterFactory.create(
						GsonBuilder().setLenient().create()
					)
				)
			}.build()

		/**
		 * To builds the retrofit client with baseUrl and Client sent
		 *
		 * @param okHttpClient Client with request and header details
		 * @return Retrofit reference retrofit builder
		 */
		private fun buildRetrofitYoutube(okHttpClient: OkHttpClient, endpoint: String): Retrofit =
			Retrofit.Builder().apply {
				baseUrl(BuildConfig.YOUTUBE_URL + endpoint)
				client(okHttpClient)
				addConverterFactory(
					GsonConverterFactory.create(
						GsonBuilder().setLenient().create()
					)
				)
			}.build()
	}

	/**
	 * To check the availability of the network before making the API call and handle no network scenarios
	 *
	 * @param call     represents the API endpoint call defined in the RTApiInterface
	 * @param callback represents the callback via which we communicate back with the caller
	 * @param <T>      the type to accept
	</T> */
	private fun <T> request(call: Call<T>, callback: NSRetrofitCallback<T>) {
		if (NSApplication.isNetworkConnected()) {
			call.enqueue(callback)
		} else {
			callback.onNoNetwork()
		}
	}

	/**
	 * To cancel all the existing requests at once
	 */
	fun cancelAllRequests() {
		authorizedOkHttpClient.dispatcher.cancelAll()
	}

	/**
	 * An interceptor to handle the authentication issue. An authentication issue occurs when the API throws error code like 401.
	 */
	class RTAuthorizationInterceptor : Interceptor {
		override fun intercept(chain: Interceptor.Chain): Response {
			val request = chain.request()
			return chain.proceed(request)
		}
	}

	/**
	 * To call the login API endpoint to authenticate the user
	 *
	 * @param loginRequest The request body
	 * @param callback     The callback for the result
	 */
	fun login(loginRequest: NSLoginRequest, callback: NSRetrofitCallback<NSUserResponse>) {
		request(
			unAuthorised3020Client.login(
				loginRequest.userName!!,
				loginRequest.password!!,
				NSApplication.getInstance().getLoginPrefs().notificationToken!!,
				NSUtilities.getDeviceInfo(NSApplication.getInstance().applicationContext)
			), callback
		)

	}

	/**
	 * To call the update profile API endpoint to update profile
	 *
	 * @param updateRequest The request body
	 * @param callback     The callback for the result
	 */
	fun updateProfile(
		updateRequest: NSUpdateProfileRequest,
		callback: NSRetrofitCallback<NSUserResponse>
	) {
		request(
			unAuthorised3020Client.updateProfile(
				NSUserManager.getAuthToken()!!,
				updateRequest.fullName!!,
				updateRequest.address!!,
				updateRequest.email!!,
				updateRequest.mobile!!,
				updateRequest.panno!!,
				updateRequest.ifscCode!!,
				updateRequest.bankName!!,
				updateRequest.acNo!!
			), callback
		)
	}

	/**
	 * To call the logout API endpoint
	 *
	 * @param callback     The callback for the result
	 */
	fun logout(callback: NSRetrofitCallback<NSLogoutResponse>) {
		request(unAuthorised3020Client.logout(NSUserManager.getAuthToken()!!), callback)
	}

	/**
	 * To call the order list API
	 *
	 * @param callback     The callback for the result
	 */
	fun getDashboard(callback: NSRetrofitCallback<NSDashboardResponse>) {
		request(unAuthorised3020Client.dashboard(NSUserManager.getAuthToken()!!), callback)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getRegisterListData(
		pageIndex: String,
		search: String,
		callback: NSRetrofitCallback<NSRegisterListResponse>
	) {
		request(
			unAuthorised3020Client.getRegisterList(
				NSUserManager.getAuthToken()!!,
				pageIndex,
				search
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getJoiningVoucherPendingData(
		pageIndex: String,
		search: String,
		callback: NSRetrofitCallback<NSVoucherListResponse>
	) {
		request(
			unAuthorised3020Client.getJoiningVoucherPending(
				NSUserManager.getAuthToken()!!,
				pageIndex,
				search
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getJoiningVoucherReceiveData(
		pageIndex: String,
		search: String,
		callback: NSRetrofitCallback<NSVoucherListResponse>
	) {
		request(
			unAuthorised3020Client.getJoiningVoucherReceive(
				NSUserManager.getAuthToken()!!,
				pageIndex,
				search
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getJoiningVoucherTransferData(
		pageIndex: String,
		search: String,
		callback: NSRetrofitCallback<NSVoucherListResponse>
	) {
		request(
			unAuthorised3020Client.getJoiningVoucherTransfer(
				NSUserManager.getAuthToken()!!,
				pageIndex,
				search
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getRePurchaseListData(
		pageIndex: String,
		search: String,
		callback: NSRetrofitCallback<NSRePurchaseListResponse>
	) {
		request(
			unAuthorised3020Client.getRePurchaseList(
				NSUserManager.getAuthToken()!!,
				pageIndex,
				search
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getRePurchaseInfoData(
		pageIndex: String,
		rePurchaseId: String,
		callback: NSRetrofitCallback<NSRePurchaseInfoResponse>
	) {
		request(
			unAuthorised3020Client.getRePurchaseInfo(
				NSUserManager.getAuthToken()!!,
				rePurchaseId,
				pageIndex
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getRetailListData(
		pageIndex: String,
		search: String,
		callback: NSRetrofitCallback<NSRetailListResponse>
	) {
		request(
			unAuthorised3020Client.getRetailList(
				NSUserManager.getAuthToken()!!,
				pageIndex,
				search
			), callback
		)
	}


	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getRetailInfoData(
		pageIndex: String,
		retailId: String,
		callback: NSRetrofitCallback<NSRetailInfoResponse>
	) {
		request(
			unAuthorised3020Client.getRetailInfo(
				NSUserManager.getAuthToken()!!,
				retailId,
				pageIndex
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getRoyaltyInfoData(
		pageIndex: String,
		royalMainId: String,
		callback: NSRetrofitCallback<NSRoyaltyInfoResponse>
	) {
		request(
			unAuthorised3020Client.getRoyaltyInfo(
				NSUserManager.getAuthToken()!!,
				royalMainId,
				pageIndex
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getRoyaltyListData(
		pageIndex: String,
		search: String,
		callback: NSRetrofitCallback<NSRoyaltyListResponse>
	) {
		request(
			unAuthorised3020Client.getRoyaltyList(
				NSUserManager.getAuthToken()!!,
				pageIndex,
				search
			), callback
		)
	}

	/**
	 * To call the downline direct reOffer API
	 *
	 * @param callback  The callback for the result
	 */
	fun getDownlineMemberDirectReOffer(callback: NSRetrofitCallback<NSDownlineMemberDirectReOfferResponse>) {
		request(
			unAuthorised3020Client.getDownLineMemberDirectReOffer(NSUserManager.getAuthToken()!!),
			callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getMemberTreeData(callback: NSRetrofitCallback<NSMemberTreeResponse>) {
		request(unAuthorised3020Client.getMemberTree(NSUserManager.getAuthToken()!!), callback)
	}

	/**
	 * To call the downline direct reOffer API
	 *
	 * @param callback  The callback for the result
	 */
	fun getLevelWiseTree(callback: NSRetrofitCallback<NSLevelMemberTreeResponse>) {
		request(
			unAuthorised3020Client.getLevelWiseMemberReportList(NSUserManager.getAuthToken()!!),
			callback
		)
	}

	/**
	 * To call the downline direct reOffer API
	 *
	 * @param callback  The callback for the result
	 */
	fun getLevelWiseDetailTree(
		levelNo: String,
		callback: NSRetrofitCallback<NSLevelMemberTreeDetailResponse>
	) {
		request(
			unAuthorised3020Client.getLevelWiseMemberReportListDetail(
				NSUserManager.getAuthToken()!!,
				levelNo
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun saveRegisterApi(
		fullName: String,
		email: String,
		mobile: String,
		password: String,
		callback: NSRetrofitCallback<NSSuccessResponse>
	) {
		request(
			unAuthorised3020Client.saveRegistrationApi(
				NSUserManager.getAuthToken()!!,
				fullName,
				email,
				mobile,
				password
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun saveDirectRegisterApi(
		referalCode: String,
		fullName: String,
		email: String,
		mobile: String,
		password: String,
		callback: NSRetrofitCallback<NSSuccessResponse>
	) {
		request(
			unAuthorised3020Client.saveRegistrationDirectApi(
				referalCode,
				fullName,
				email,
				mobile,
				password,
				NSApplication.getInstance().getLoginPrefs().notificationToken!!
			), callback
		)
	}

	/**
	 * To call the change password API endpoint to change password
	 *
	 * @param changePasswordRequest The request body
	 * @param callback     The callback for the result
	 */
	fun changePassword(
		changePasswordRequest: NSChangePasswordRequest,
		callback: NSRetrofitCallback<NSChangePasswordResponse>
	) {
		request(
			unAuthorised3020Client.changePassword(
				NSUserManager.getAuthToken()!!,
				changePasswordRequest.currentPassword!!,
				changePasswordRequest.newPassword!!
			), callback
		)
	}

	/**
	 * To call the change tran password API endpoint to change tran password
	 *
	 * @param changePasswordRequest The request body
	 * @param callback     The callback for the result
	 */
	fun changeTranPassword(
		changePasswordRequest: NSChangePasswordRequest,
		callback: NSRetrofitCallback<NSChangePasswordResponse>
	) {
		request(
			unAuthorised3020Client.changeTransPassword(
				NSUserManager.getAuthToken()!!,
				changePasswordRequest.currentPassword!!,
				changePasswordRequest.newPassword!!
			), callback
		)
	}

	/**
	 * To call the wallet list data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getWalletList(
		pageIndex: String,
		search: String,
		startDate: String,
		endDate: String,
		type: String,
		callback: NSRetrofitCallback<NSWalletListResponse>
	) {
		request(
			unAuthorised3020Client.getWalletList(
				NSUserManager.getAuthToken(),
				pageIndex,
				search, startDate, endDate, type
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun walletRedeemList(
		pageIndex: String,
		search: String,
		startDate: String,
		endDate: String,
		callback: NSRetrofitCallback<NSRedeemListResponse>
	) {
		request(
			unAuthorised3020Client.walletRedemptionList(
				NSUserManager.getAuthToken()!!,
				pageIndex,
				search, startDate, endDate
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun redeemWalletSave(
		amount: String,
		password: String,
		callback: NSRetrofitCallback<NSSuccessResponse>
	) {
		request(
			unAuthorised3020Client.walletRedeemMoney(
				NSUserManager.getAuthToken()!!,
				amount,
				password
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun transferWalletAmount(
		transactionId: String,
		amount: String,
		remark: String,
		password: String,
		callback: NSRetrofitCallback<NSSuccessResponse>
	) {
		request(
			unAuthorised3020Client.transferWalletMoney(
				NSUserManager.getAuthToken()!!,
				transactionId,
				amount,
				remark,
				password
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getMemberDetail(memberId: String, callback: NSRetrofitCallback<NSMemberDetailResponse>) {
		request(
			unAuthorised3020Client.verifyWalletMember(NSUserManager.getAuthToken()!!, memberId),
			callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun checkStockList(stockType: String, stockId: String, callback: NSRetrofitCallback<NSMemberDetailResponse>) {
		request(
			unAuthorised3020Client.checkStockList(NSUserManager.getAuthToken()!!, stockType, stockId),
			callback
		)
	}

	/**
	 * To call the package list data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getPackageMasterList(callback: NSRetrofitCallback<NSPackageResponse>) {
		request(unAuthorised3020Client.packageMaster(NSUserManager.getAuthToken()!!), callback)
	}

	/**
	 * To call the package vise Voucher Quantity data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getPackageViseVoucherQty(
		packageId: String,
		callback: NSRetrofitCallback<NSPackageVoucherQntResponse>
	) {
		request(
			unAuthorised3020Client.packageViseVoucherQuantity(
				NSUserManager.getAuthToken()!!,
				packageId
			), callback
		)
	}

	/**
	 * To call the package vise Voucher Quantity data API
	 *
	 * @param callback  The callback for the result
	 */
	fun joiningVoucherTransfer(
		transferId: String,
		packageId: String,
		voucherQty: String,
		callback: NSRetrofitCallback<NSSuccessResponse>
	) {
		request(
			unAuthorised3020Client.joiningVoucherTransferSave(
				NSUserManager.getAuthToken()!!,
				transferId,
				packageId,
				voucherQty
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getProductCategory(callback: NSRetrofitCallback<NSCategoryListResponse>) {
		request(unAuthorised3020Client.productCategory(NSUserManager.getAuthToken()!!), callback)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getProductList(
		pageIndex: String,
		search: String,
		categoryId: String,
		callback: NSRetrofitCallback<NSProductListResponse>
	) {
		request(
			unAuthorised3020Client.productList(
				NSUserManager.getAuthToken()!!,
				pageIndex,
				search,
				categoryId
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getProductStockList(
		pageIndex: String,
		search: String,
		categoryId: String,
		diseasesId: String,
		callback: NSRetrofitCallback<NSProductListResponse>
	) {
		request(
			unAuthorised3020Client.productStockList(
				NSUserManager.getAuthToken(),
				pageIndex,
				search,
				categoryId,
				diseasesId
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getActivationList(
		pageIndex: String,
		search: String,
		callback: NSRetrofitCallback<NSActivationListResponse>
	) {
		request(
			unAuthorised3020Client.activateList(
				NSUserManager.getAuthToken()!!,
				pageIndex,
				search
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getActivationPackageList(callback: NSRetrofitCallback<NSActivationPackageResponse>) {
		request(unAuthorised3020Client.activationPackage(NSUserManager.getAuthToken()!!), callback)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getMemberActivationPackageList(
		memberId: String,
		callback: NSRetrofitCallback<NSActivationPackageResponse>
	) {
		request(
			unAuthorised3020Client.memberActivationPackage(
				NSUserManager.getAuthToken()!!,
				memberId
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun activationSave(
		registrationType: String,
		packageId: String,
		callback: NSRetrofitCallback<NSSuccessResponse>
	) {
		request(
			unAuthorised3020Client.activationSave(
				NSUserManager.getAuthToken()!!,
				registrationType,
				packageId
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun activationDirectSave(
		memberId: String,
		registrationType: String,
		packageId: String,
		callback: NSRetrofitCallback<NSSuccessResponse>
	) {
		request(
			unAuthorised3020Client.activationDirectSave(
				NSUserManager.getAuthToken()!!,
				memberId,
				registrationType,
				packageId
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getUpLineMembers(callback: NSRetrofitCallback<NSUpLineListResponse>) {
		request(unAuthorised3020Client.upLineMemberList(NSUserManager.getAuthToken()!!), callback)
	}

	fun checkVersion(callback: NSRetrofitCallback<NSCheckVersionResponse>) {
		request(unAuthorised3020Client.checkVersion(), callback)
	}

	fun getServiceProvider(
		rechargeType: String,
		rechargeMemberId: String,
		callback: NSRetrofitCallback<NSServiceProviderResponse>
	) {
		request(unAuthorised3020Client.getServiceProvider(rechargeType, rechargeMemberId), callback)
	}

	fun rechargeSave(rsR: NSRechargeSaveRequest, callback: NSRetrofitCallback<NSSuccessResponse>) {
		request(
			unAuthorised3020Client.rechargeSave(
				NSUserManager.getAuthToken()!!,
				rsR.rechargeType!!,
				rsR.serviceProvider!!,
				rsR.accountDisplay!!,
				rsR.amount!!,
				rsR.note!!,
				rsR.ad1!!,
				rsR.ad2!!,
				rsR.ad3!!
			), callback
		)
	}

	fun rechargeFetchData(
		rsR: NSRechargeSaveRequest,
		callback: NSRetrofitCallback<NSRechargeFetchListResponse>
	) {
		request(
			unAuthorised3020Client.rechargeFetchData(
				NSUserManager.getAuthToken()!!,
				rsR.serviceProvider!!,
				rsR.accountDisplay!!,
				rsR.ad1!!,
				rsR.ad2!!,
				rsR.ad3!!
			), callback
		)
	}

	fun qrScan(
		qrUserId: String,
		amount: String,
		note: String = "",
		name: String,
		upiId: String,
		callback: NSRetrofitCallback<NSSuccessResponse>
	) {
		request(
			unAuthorised3020Client.qrScan(
				NSUserManager.getAuthToken(),
				amount,
				note,
				qrUserId,
				name,
				upiId
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getRechargeListData(
		pageIndex: String,
		search: String,
		rechargeType: String,
		statusType: String,
		callback: NSRetrofitCallback<NSRechargeListResponse>
	) {
		request(
			unAuthorised3020Client.getRechargeList(
				NSUserManager.getAuthToken()!!,
				pageIndex,
				search,
				rechargeType,
				statusType
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun saveMyCart(
		memberId: String, walletType: String, remark: String, productList: String,
		callback: NSRetrofitCallback<NSSuccessResponse>
	) {
		request(
			unAuthorised3020Client.saveMyCart(
				NSUserManager.getAuthToken()!!,
				memberId, walletType, remark, productList
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun saveSocketStockTransferMyCart(
		memberId: String, walletType: String, remark: String, productList: String,
		callback: NSRetrofitCallback<NSSuccessResponse>
	) {
		request(
			unAuthorised3020Client.saveSocketStockTransferMyCart(
				NSUserManager.getAuthToken()!!,
				memberId, walletType, remark, productList
			), callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getRepurchaseHistory(pageIndex: String, search: String, callback: NSRetrofitCallback<NSRepurchaseStockModel>) {
		request(
			unAuthorised3020Client.getStockRepurchaseHistoryList(NSUserManager.getAuthToken()!!, pageIndex, search),
			callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getStockTransferList(pageIndex: String, search: String, callback: NSRetrofitCallback<NSRepurchaseStockModel>) {
		request(
			unAuthorised3020Client.getStockTransferHistoryList(NSUserManager.getAuthToken()!!, pageIndex, search),
			callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getStockTransferInfo(stockId: String, callback: NSRetrofitCallback<NSRePurchaseInfoResponse>) {
		request(
			unAuthorised3020Client.getStockTransferInfo(NSUserManager.getAuthToken()!!, stockId),
			callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getYoutubeVideos(youtubeRequestMap: Map<String, String>, callback: NSRetrofitCallback<YoutubeResponse>) {
		request(
			youtubeClient.getYoutubeVideos(youtubeRequestMap),
			callback
		)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getNotifications(pageIndex: String, callback: NSRetrofitCallback<NSNotificationListResponse>) {
		request(unAuthorised3020Client.getNotifications(NSUserManager.getAuthToken()!!, pageIndex), callback)
	}

	/**
	 * To call the user detail data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getDownloadList(callback: NSRetrofitCallback<DownloadListResponse>) {
		request(unAuthorised3020Client.getDownloadList(NSUserManager.getAuthToken()), callback)
	}

	/**
	 * To call the set default register data API
	 *
	 * @param callback  The callback for the result
	 */
	fun setDefault(userId: String, callback: NSRetrofitCallback<NSSetDefaultResponse>) {
		request(unAuthorised3020Client.setDefaultAPi(NSUserManager.getAuthToken(), userId), callback)
	}

	/**
	 * To call the set default register data API
	 *
	 * @param callback  The callback for the result
	 */
	fun sendMessage(userId: String, callback: NSRetrofitCallback<NSSendMessageResponse>) {
		request(unAuthorised3020Client.sendMessage(NSUserManager.getAuthToken(), userId), callback)
	}

	/**
	 * To call the set default register data API
	 *
	 * @param callback  The callback for the result
	 */
	fun getDiseasesMasterList(callback: NSRetrofitCallback<NSDiseasesResponse>) {
		request(unAuthorised3020Client.diseasesMasterApi(NSUserManager.getAuthToken()), callback)
	}

	/**
	 * To call the set default register data API
	 *
	 * @param callback  The callback for the result
	 */
	fun searchList(search: String, callback: NSRetrofitCallback<NSSearchListResponse>) {
		request(unAuthorised3020Client.searchList(NSUserManager.getAuthToken(), search), callback)
	}
}

/**
 * The interface defining the API endpoints
 */
interface RTApiInterface {

	@FormUrlEncoded
	@POST("login-api")
	fun login(
		@Field("username") username: String,
		@Field("password") password: String,
		@Field("notification_token") token: String,
		@Field("device_detail") deviceDetail: String
	): Call<NSUserResponse>

	@FormUrlEncoded
	@POST("logout-api")
	fun logout(@Field("token_id") token: String): Call<NSLogoutResponse>

	@FormUrlEncoded
	@POST("dashboard-api")
	fun dashboard(@Field("token_id") token: String): Call<NSDashboardResponse>

	@FormUrlEncoded
	@POST("registration-list-api")
	fun getRegisterList(
		@Field("token_id") token: String,
		@Field("page_index") pageIndex: String,
		@Field("search") search: String
	): Call<NSRegisterListResponse>

	@FormUrlEncoded
	@POST("joining-voucher-pending")
	fun getJoiningVoucherPending(
		@Field("token_id") token: String,
		@Field("page_index") pageIndex: String,
		@Field("search") search: String
	): Call<NSVoucherListResponse>

	@FormUrlEncoded
	@POST("joining-voucher-receive")
	fun getJoiningVoucherReceive(
		@Field("token_id") token: String,
		@Field("page_index") pageIndex: String,
		@Field("search") search: String
	): Call<NSVoucherListResponse>

	@FormUrlEncoded
	@POST("joining-voucher-transfer")
	fun getJoiningVoucherTransfer(
		@Field("token_id") token: String,
		@Field("page_index") pageIndex: String,
		@Field("search") search: String
	): Call<NSVoucherListResponse>

	@FormUrlEncoded
	@POST("repurchase-list")
	fun getRePurchaseList(
		@Field("token_id") token: String,
		@Field("page_index") pageIndex: String,
		@Field("search") search: String
	): Call<NSRePurchaseListResponse>

	@FormUrlEncoded
	@POST("repurchase-info")
	fun getRePurchaseInfo(
		@Field("token_id") token: String,
		@Field("repurchase_id") repurchaseInfo: String,
		@Field("page_index") pageIndex: String
	): Call<NSRePurchaseInfoResponse>

	@FormUrlEncoded
	@POST("direct-retail-offer-list")
	fun getRetailList(
		@Field("token_id") token: String,
		@Field("page_index") pageIndex: String,
		@Field("search") search: String
	): Call<NSRetailListResponse>

	@FormUrlEncoded
	@POST("royalty-offer-list")
	fun getRoyaltyList(
		@Field("token_id") token: String,
		@Field("page_index") pageIndex: String,
		@Field("search") search: String
	): Call<NSRoyaltyListResponse>

	@FormUrlEncoded
	@POST("royalty-offer-info")
	fun getRoyaltyInfo(
		@Field("token_id") token: String,
		@Field("royalty_offer_main_id") royaltyMainId: String,
		@Field("page_index") pageIndex: String
	): Call<NSRoyaltyInfoResponse>

	@FormUrlEncoded
	@POST("direct-retail-offer-info")
	fun getRetailInfo(
		@Field("token_id") token: String,
		@Field("direct_retail_offer_main_id") retailMainId: String,
		@Field("page_index") pageIndex: String
	): Call<NSRetailInfoResponse>

	@FormUrlEncoded
	@POST("downline-member-direct-reoffer")
	fun getDownLineMemberDirectReOffer(@Field("token_id") token: String): Call<NSDownlineMemberDirectReOfferResponse>

	@FormUrlEncoded
	@POST("member-tree")
	fun getMemberTree(@Field("token_id") token: String): Call<NSMemberTreeResponse>

	@FormUrlEncoded
	@POST("level-wise-member-report-list")
	fun getLevelWiseMemberReportList(@Field("token_id") token: String): Call<NSLevelMemberTreeResponse>

	@FormUrlEncoded
	@POST("level-wise-member-report-list")
	fun getLevelWiseMemberReportListDetail(
		@Field("token_id") token: String,
		@Field("levelno") levelNo: String
	): Call<NSLevelMemberTreeDetailResponse>

	/*Change Password*/
	@FormUrlEncoded
	@POST("change-password-api")
	fun changePassword(
		@Field("token_id") token: String,
		@Field("current_password") currentPassword: String,
		@Field("new_password") newPassword: String
	): Call<NSChangePasswordResponse>

	@FormUrlEncoded
	@POST("change-transaction-password-api")
	fun changeTransPassword(
		@Field("token_id") token: String,
		@Field("current_password") currentPassword: String,
		@Field("new_password") newPassword: String
	): Call<NSChangePasswordResponse>

	@FormUrlEncoded
	@POST("update-profile-api")
	fun updateProfile(
		@Field("token_id") token: String,
		@Field("fullname") fullname: String,
		@Field("address") address: String,
		@Field("email") email: String,
		@Field("mobile") mobile: String,
		@Field("panno") panno: String,
		@Field("ifsc_code") ifscCode: String,
		@Field("bank_name") bankName: String,
		@Field("ac_no") acNo: String
	): Call<NSUserResponse>

	@FormUrlEncoded
	@POST("wallet-list-api")
	fun getWalletList(
		@Field("token_id") token: String,
		@Field("page_index") pageIndex: String,
		@Field("search") search: String,
		@Field("start_date") startDate: String,
		@Field("end_date") endDate: String,
		@Field("type") type: String
	): Call<NSWalletListResponse>

	@FormUrlEncoded
	@POST("joining-voucher-transfer-info")
	fun getJoiningVoucherTransferInfo(
		@Field("token_id") token: String,
		@Field("memberid") memberId: String
	): Call<NSJoiningVoucherTransferResponse>

	@FormUrlEncoded
	@POST("wallet-redemption-api")
	fun walletRedemptionList(
		@Field("token_id") token: String,
		@Field("page_index") pageIndex: String,
		@Field("search") search: String,
		@Field("start_date") startDate: String,
		@Field("end_date") endDate: String
	): Call<NSRedeemListResponse>

	@FormUrlEncoded
	@POST("save-wallet-redemption-api")
	fun walletRedeemMoney(
		@Field("token_id") token: String,
		@Field("amount") amount: String,
		@Field("transaction_password") transactionPassword: String
	): Call<NSSuccessResponse>

	@FormUrlEncoded
	@POST("wallet-transfer-api")
	fun transferWalletMoney(
		@Field("token_id") token: String,
		@Field("transfer_id") transferId: String,
		@Field("amount") amount: String,
		@Field("remark") remark: String,
		@Field("transaction_password") transactionPassword: String
	): Call<NSSuccessResponse>

	@FormUrlEncoded
	@POST("save-registration-api")
	fun saveRegistrationApi(
		@Field("token_id") token: String,
		@Field("fullname") fullName: String,
		@Field("email") email: String,
		@Field("mobile") mobile: String,
		@Field("password") password: String
	): Call<NSSuccessResponse>

	@FormUrlEncoded
	@POST("save-registration-direct-api")
	fun saveRegistrationDirectApi(
		@Field("referral_code") referalCode: String,
		@Field("fullname") fullName: String,
		@Field("email") email: String,
		@Field("mobile") mobile: String,
		@Field("password") password: String,
		@Field("notification_token") token: String
	): Call<NSSuccessResponse>

	@FormUrlEncoded
	@POST("member-info-api")
	fun verifyWalletMember(
		@Field("token_id") token: String,
		@Field("memberid") memberId: String
	): Call<NSMemberDetailResponse>

	@FormUrlEncoded
	@POST("package-master-api")
	fun packageMaster(@Field("token_id") token: String): Call<NSPackageResponse>

	@FormUrlEncoded
	@POST("package-wise-voucher-qty-api")
	fun packageViseVoucherQuantity(
		@Field("token_id") token: String,
		@Field("package_id") packageId: String
	): Call<NSPackageVoucherQntResponse>

	@FormUrlEncoded
	@POST("joining-voucher-transfer-save")
	fun joiningVoucherTransferSave(
		@Field("token_id") token: String,
		@Field("transfer_id") transferId: String,
		@Field("package_id") packageId: String,
		@Field("voucher_qty") voucherQty: String
	): Call<NSSuccessResponse>

	/*Products*/
	@FormUrlEncoded
	@POST("category-master-api")
	fun productCategory(@Field("token_id") token: String): Call<NSCategoryListResponse>

	@FormUrlEncoded
	@POST("product-master-api")
	fun productList(
		@Field("token_id") token: String,
		@Field("page_index") pageIndex: String,
		@Field("search") search: String,
		@Field("category_id") categoryId: String
	): Call<NSProductListResponse>

	@FormUrlEncoded
	@POST("product-master-stock-api")
	fun productStockList(
		@Field("token_id") token: String,
		@Field("page_index") pageIndex: String,
		@Field("search") search: String,
		@Field("category_id") categoryId: String,
		@Field("diseases_id") diseasesId: String,
	): Call<NSProductListResponse>

	@FormUrlEncoded
	@POST("activation-list-api")
	fun activateList(
		@Field("token_id") token: String,
		@Field("page_index") pageIndex: String,
		@Field("search") search: String
	): Call<NSActivationListResponse>

	@FormUrlEncoded
	@POST("activation-package-api")
	fun activationPackage(@Field("token_id") token: String): Call<NSActivationPackageResponse>

	@FormUrlEncoded
	@POST("activation-save-api")
	fun activationSave(
		@Field("token_id") token: String,
		@Field("registration_type") registrationType: String,
		@Field("package_id") packageId: String
	): Call<NSSuccessResponse>

	@FormUrlEncoded
	@POST("upline-member-list-api")
	fun upLineMemberList(@Field("token_id") token: String): Call<NSUpLineListResponse>

	@FormUrlEncoded
	@POST("activation-direct-package-api")
	fun memberActivationPackage(
		@Field("token_id") token: String,
		@Field("memberid") memberId: String
	): Call<NSActivationPackageResponse>

	@FormUrlEncoded
	@POST("activation-direct-save-api")
	fun activationDirectSave(
		@Field("token_id") token: String,
		@Field("memberid") memberId: String,
		@Field("registration_type") registrationType: String,
		@Field("package_id") packageId: String
	): Call<NSSuccessResponse>

	@GET("check-version")
	fun checkVersion(): Call<NSCheckVersionResponse>

	@FormUrlEncoded
	@POST("get-service-provider")
	fun getServiceProvider(
		@Field("recharge_type") rechargeType: String,
		@Field("recharge_master_id") rechargeMasterId: String
	): Call<NSServiceProviderResponse>

	@FormUrlEncoded
	@POST("recharge-save")
	fun rechargeSave(
		@Field("token_id") token: String,
		@Field("recharge_type") rechargeType: String,
		@Field("service_provider") serviceProvider: String,
		@Field("account_display") accountDisplay: String,
		@Field("amount") amount: String,
		@Field("note") note: String,
		@Field("ad1") ad1: String,
		@Field("ad2") ad2: String,
		@Field("ad3") ad3: String
	): Call<NSSuccessResponse>

	@FormUrlEncoded
	@POST("recharge-list")
	fun getRechargeList(
		@Field("token_id") token: String,
		@Field("page_index") pageIndex: String,
		@Field("search") search: String,
		@Field("recharge_type") rechargeType: String,
		@Field("status_type") statusType: String
	): Call<NSRechargeListResponse>

	@FormUrlEncoded
	@POST("recharge-fetch-data")
	fun rechargeFetchData(
		@Field("token_id") token: String,
		@Field("service_provider") serviceProvider: String,
		@Field("account_display") accountDisplay: String,
		@Field("ad1") ad1: String,
		@Field("ad2") ad2: String,
		@Field("ad3") ad3: String
	): Call<NSRechargeFetchListResponse>

	@FormUrlEncoded
	@POST("recharge-qr-scan")
	fun qrScan(
		@Field("token_id") token: String,
		@Field("amount") amount: String,
		@Field("note") note: String,
		@Field("qr_user_id") qrUserId: String,
		@Field("name") name: String,
		@Field("upi_id") upi_id: String,
	): Call<NSSuccessResponse>

	@FormUrlEncoded
	@POST("save-my-cart")
	fun saveMyCart(
		@Field("token_id") token: String,
		@Field("member_id") memberId: String, @Field("wallet_type") walletType: String, @Field("remark") remark: String, @Field("product_list") productList: String,
	): Call<NSSuccessResponse>

	@FormUrlEncoded
	@POST("save-stockiest-stock-transfer")
	fun saveSocketStockTransferMyCart(
		@Field("token_id") token: String,
		@Field("member_id") memberId: String, @Field("stock_type") walletType: String, @Field("remark") remark: String, @Field("product_list") productList: String,
	): Call<NSSuccessResponse>

	@FormUrlEncoded
	@POST("stockiest-check-api")
	fun checkStockList(
		@Field("token_id") token: String,
		@Field("stockiest_type") stockListType: String,
		@Field("stockiest_id") stockListId: String
	): Call<NSMemberDetailResponse>

	@FormUrlEncoded
	@POST("stockiest-repurchase-list")
	fun getStockRepurchaseHistoryList(
		@Field("token_id") token: String,
		@Field("page_index") pageIndex: String,
		@Field("search") search: String
	): Call<NSRepurchaseStockModel>

	@FormUrlEncoded
	@POST("stockiest-stock-transfer-list")
	fun getStockTransferHistoryList(
		@Field("token_id") token: String,
		@Field("page_index") pageIndex: String,
		@Field("search") search: String
	): Call<NSRepurchaseStockModel>

	@FormUrlEncoded
	@POST("stockiest-stock-transfer-info")
	fun getStockTransferInfo(
		@Field("token_id") token: String,
		@Field("stock_transfer_id") stockTransferId: String
	): Call<NSRePurchaseInfoResponse>

	@GET("search")
	fun getYoutubeVideos(
		@QueryMap param: Map<String, String>
	): Call<YoutubeResponse>

	@FormUrlEncoded
	@POST("notification-list")
	fun getNotifications(
		@Field("token_id") token: String,
		@Field("page_index") pageIndex: String
	): Call<NSNotificationListResponse>

	@FormUrlEncoded
	@POST("download-list")
	fun getDownloadList(
		@Field("token_id") token: String
	): Call<DownloadListResponse>

	@FormUrlEncoded
	@POST("set_default_register")
	fun setDefaultAPi(
		@Field("token_id") token: String,
		@Field("user_id") userId: String
	): Call<NSSetDefaultResponse>

	@FormUrlEncoded
	@POST("send_message_user")
	fun sendMessage(
		@Field("token_id") token: String,
		@Field("user_id") userId: String
	): Call<NSSendMessageResponse>

	@FormUrlEncoded
	@POST("diseases-master-api")
	fun diseasesMasterApi(
		@Field("token_id") token: String,
	): Call<NSDiseasesResponse>

	@FormUrlEncoded
	@POST("search-list-api")
	fun searchList(
		@Field("token_id") token: String,
		@Field("search") search: String
	): Call<NSSearchListResponse>
}
