package com.moneytree.app.ui.mycart.placeOrder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSActivity
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSRequestCodes
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.rozerpay.RazorpayUtility
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.buildAlertDialog
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.common.utils.switchResultActivity
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.LayoutCustomAlertDialogBinding
import com.moneytree.app.databinding.LayoutPlaceOrderOptionsBinding
import com.moneytree.app.databinding.NsActivityPlaceOrderBinding
import com.moneytree.app.repository.network.responses.NSAddressCreateResponse
import com.moneytree.app.repository.network.responses.NSDataUser
import com.moneytree.app.repository.network.responses.NSErrorPaymentResponse
import com.moneytree.app.repository.network.responses.NSSuccessResponse
import com.moneytree.app.repository.network.responses.RozerModel
import com.moneytree.app.ui.mycart.address.NSAddressActivity
import com.moneytree.app.ui.success.SuccessActivity
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener

class NSPlaceOrderActivity : NSActivity(), PaymentResultWithDataListener {
    private val pref = NSApplication.getInstance().getPrefs()
    private var activity = this
    private lateinit var binding: NsActivityPlaceOrderBinding
    private val productModel: NSPlaceOrderViewModel by lazy {
        ViewModelProvider(this)[NSPlaceOrderViewModel::class.java]
    }
    private val model = RozerModel()
    var successResponse: NSSuccessResponse? = null
    var userDetail: NSDataUser? = null
    private var paymentOptionBottomSheet: BottomSheetDialog? = null

    private val cartAddressResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            if (resultCode == Activity.RESULT_OK) {
                productModel.isDefaultAddress = false
                setAddress()
            }
        }

    private val kycLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            if (resultCode == Activity.RESULT_OK) {
                productModel.apply {
                    val instance = NSApplication.getInstance()
                    val productList = if (isFromOrder) instance.getOrderList() else instance.getProductList()
                    if (productList.size > 0) {
                        if (pref.selectedAddress == null) {
                            Toast.makeText(activity, "Please Add Address Detail", Toast.LENGTH_SHORT).show()
                        } else {
                            val razorpayUtility = RazorpayUtility(activity)
                            model.productName = "Orders"
                            model.price = productModel.finalAmount
                            model.email = userDetail?.email
                            model.mobile = userDetail?.mobile
                            model.address = Gson().toJson(pref.selectedAddress)
                            model.productDetail = Gson().toJson(productList)
                            razorpayUtility.startOrderPayment(model)
                        }
                        //saveMyCart(memberId, selectedWalletType, remark, Gson().toJson(productList), true)
                    } else {
                        Toast.makeText(activity, "Please Select Product", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NsActivityPlaceOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Checkout.preload(this)
        loadInitialFragment(intent.extras)
    }

    /**
     * To initialize product fragment
     *
     */
    private fun loadInitialFragment(bundle: Bundle?) {
        productModel.isDefaultAddress = bundle?.getBoolean(NSConstants.KEY_IS_DEFAULT_ADDRESS)?:false
        productModel.isFromOrder = bundle?.getBoolean(NSConstants.KEY_IS_FROM_ORDER)?:false
        viewCreated()
        setListener()
       // replaceCurrentFragment(NSPlaceOrderFragment.newInstance(bundle), false, productsBinding.addressContainer.id)
    }

    private fun viewCreated() {
        with(binding) {
            HeaderUtils(layoutHeader, this@NSPlaceOrderActivity, clBackView = true, headerTitle = resources.getString(
                R.string.orders))
            setAddress()
        }
        observeViewModel()
        setTotalAmount()
    }

    private fun setListener() {
        with(productModel) {
            with(binding) {
                with(layoutHeader) {


                    getUserDetail {
                        userDetail = it
                    }


                    btnSubmit.setOnClickListener(object : SingleClickListener() {
                        override fun performClick(v: View?) {
                            placeOrder()

                            //switchResultActivity(kycLauncher, NSKycActivity::class.java)
                            /*val instance = NSApplication.getInstance()
                            val productList = if (isFromOrder) instance.getOrderList() else instance.getProductList()
                            if (productList.size > 0) {
                                if (pref.selectedAddress == null) {
                                    Toast.makeText(activity, "Please Add Address Detail", Toast.LENGTH_SHORT).show()
                                    return
                                } else {
                                    val razorpayUtility = RazorpayUtility(activity)
                                    model.productName = "Orders"
                                    model.price = productModel.finalAmount
                                    model.email = userDetail?.email
                                    model.mobile = userDetail?.mobile
                                    model.address = Gson().toJson(pref.selectedAddress)
                                    model.productDetail = Gson().toJson(productList)
                                    razorpayUtility.startOrderPayment(model)
                                }
                                //saveMyCart(memberId, selectedWalletType, remark, Gson().toJson(productList), true)
                            } else {
                                Toast.makeText(activity, "Please Select Product", Toast.LENGTH_SHORT).show()
                            }*/
                        }
                    })
                }
            }
        }
    }

    private fun placeOrder() {
        binding.apply {
            productModel.apply {
                val instance = NSApplication.getInstance()
                val productList = if (isFromOrder) instance.getOrderList() else instance.getProductList()
                if (productList.size > 0) {
                    if (pref.selectedAddress == null) {
                        Toast.makeText(activity, "Please Add Address Detail", Toast.LENGTH_SHORT).show()
                        return
                    } else {

                        model.productName = "Orders"
                        model.price = productModel.finalAmount
                        model.email = userDetail?.email
                        model.mobile = userDetail?.mobile
                        model.address = Gson().toJson(pref.selectedAddress)
                        model.productDetail = Gson().toJson(productList)

                        showPaymentOption() {
                            if (it == NSConstants.PAYMENT_WALLET) {
                                if ((model.price?:"0.0").toDouble() > NSApplication.getInstance().getWalletBalance().toDouble()) {
                                    productModel.showError("You don't have enough balance to get this service kindly recharge your wallet.")
                                    return@showPaymentOption
                                } else {
                                    onPaymentSuccess("", PaymentData())
                                }
                            } else if (it == NSConstants.PAYMENT_GATEWAY) {
                                Toast.makeText(activity, "Coming Soon", Toast.LENGTH_SHORT).show()

                                //Uncomment when it's need
                                /*val razorpayUtility = RazorpayUtility(activity)
                                razorpayUtility.startOrderPayment(model)*/

                            } else if (it == NSConstants.PAYMENT_MT_COIN) {
                                Toast.makeText(activity, "Coming Soon", Toast.LENGTH_SHORT).show()
                            }
                        }



                    }
                    //saveMyCart(memberId, selectedWalletType, remark, Gson().toJson(productList), true)
                } else {
                    Toast.makeText(activity, "Please Select Product", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun setAddress() {
        binding.apply {
            val model: NSAddressCreateResponse = if (productModel.isDefaultAddress) pref.selectedAddress?: NSAddressCreateResponse() else NSApplication.getInstance().getSelectedAddress()
            etUserName.text = model.fullName

            val list: MutableList<String> = arrayListOf()
            list.add(model.flatHouse)
            list.add(model.area)
            list.add(model.landMark)
            list.add(model.city + "," + model.state + "," + model.country)

            var address = ""
            for (data in list) {
                if (data.isNotEmpty()) {
                    address = if (address.isEmpty()) {
                        data
                    } else {
                        address + "\n" + data
                    }
                }
            }

            tvAddress.text = address
            if (address.isNotEmpty()) {
                tvAddress.visible()
                btnEditAddress.text = "Edit Address"
            } else {
                btnEditAddress.text = "Add Address"
            }
            btnEditAddress.visible()

            btnEditAddress.setOnClickListener {
                switchResultActivity(
                    cartAddressResult,
                    NSAddressActivity::class.java,
                    bundleOf(NSConstants.KEY_IS_FROM_ORDER to productModel.isFromOrder, NSConstants.KEY_IS_ADD_ADDRESS to false, NSConstants.KEY_IS_SELECTED_ADDRESS to Gson().toJson(model))
                )
            }
        }
    }

    private fun setTotalAmount() {
        with(binding) {
            var totalAmountValue = 0
            for (data in NSApplication.getInstance().getOrderList()) {
                val amount1: Int = data.sdPrice?.toInt() ?: 0
                val finalAmount1 = data.itemQty * amount1
                totalAmountValue += finalAmount1
            }
            tvProductTitle.text = "${NSApplication.getInstance().getOrderList().size} Item Selected"
            tvAmount.text = addText(activity, R.string.price_value, totalAmountValue.toString())
            productModel.finalAmount = totalAmountValue.toString()
        }
    }

    private fun observeViewModel() {
        with(productModel) {
            with(binding) {
                isProgressShowing.observe(
                    this@NSPlaceOrderActivity
                ) { shouldShowProgress ->
                    updateProgress(shouldShowProgress)
                }

                isProductSendDataAvailable.observe(
                    this@NSPlaceOrderActivity
                ) { isProductSend ->
                    if (isProductSend) {
                        if (successResponse?.status == true) {
                            showAlertDialog(
                                activity.resources.getString(R.string.app_name),
                                successResponse?.message?:"",
                                true
                            )
                        } else {
                            showAlertDialog(
                                activity.resources.getString(R.string.app_name),
                                successResponse?.message?:""
                            )
                        }
                    }
                }

                failureErrorMessage.observe(this@NSPlaceOrderActivity) { errorMessage ->
                    showAlertDialog(activity.resources.getString(R.string.app_name), errorMessage?:"")
                }

                apiErrors.observe(this@NSPlaceOrderActivity) { apiErrors ->
                    showAlertDialog(activity.resources.getString(R.string.app_name), NSUtilities.parseApiErrorList(this@NSPlaceOrderActivity, apiErrors))
                }

                noNetworkAlert.observe(this@NSPlaceOrderActivity) {
                    showAlertDialog(
                        getString(R.string.no_network_available),
                        getString(R.string.network_unreachable)
                    )
                }

                validationErrorId.observe(this@NSPlaceOrderActivity) { errorId ->
                    showAlertDialog(activity.resources.getString(R.string.app_name), getString(errorId))
                }
            }
        }
    }

    private fun showAlertDialog(title: String, message: String, isSuccess: Boolean = false) {
        buildAlertDialog(activity, LayoutCustomAlertDialogBinding::inflate) { dialog, binding ->
            binding.apply {
                tvTitle.text = title
                tvSubTitle.text = message
                tvOk.text = activity.resources.getString(R.string.ok)
                tvCancel.text = activity.resources.getString(R.string.cancel)

                tvOk.setOnClickListener {
                    dialog.dismiss()
                    if (isSuccess) {
                        val intent = Intent()
                        activity.setResult(NSRequestCodes.REQUEST_PRODUCT_STOCK_UPDATE_DETAIL, intent)
                        NSConstants.STOCK_UPDATE = NSRequestCodes.REQUEST_PRODUCT_STOCK_UPDATE_DETAIL
                        finish()
                    }
                }

                tvCancel.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        productModel.placeOrder( p0?:"", Gson().toJson(p1), model.address?:"", model.productDetail?:"", model.price?:"0", true)
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        successResponse = if ((p1?:"").contains("description")) {
            val gson = Gson().fromJson(p1?:"", NSErrorPaymentResponse::class.java)
            NSSuccessResponse(false, gson.error?.description)
        } else {
            NSSuccessResponse(false, p1?:"")
        }

        //Error message
        openSuccessFail(activity)
    }

    private fun openSuccessFail(activity: Activity) {
        if (successResponse == null) {
            successResponse = NSSuccessResponse(false, activity.resources.getString(R.string.something_went_wrong))
        }
        successResponse?.isPaymentMode = true
        activity.switchActivity(
            SuccessActivity::class.java,
            flags = intArrayOf(Intent.FLAG_ACTIVITY_CLEAR_TOP), bundle = bundleOf(NSConstants.KEY_SUCCESS_FAIL to if (successResponse == null) "" else Gson().toJson(successResponse))
        )
        activity.finish()
    }

    private fun showPaymentOption(callback: (String) -> Unit) {
        try {
            val sheetView: View = activity.layoutInflater
                .inflate(R.layout.layout_place_order_options, null)
            paymentOptionBottomSheet = BottomSheetDialog(activity, R.style.MyBottomSheetDialogTheme)
            paymentOptionBottomSheet!!.setContentView(sheetView)
            paymentOptionBottomSheet!!.setCanceledOnTouchOutside(false)
            paymentOptionBottomSheet!!.show()
            val bind = LayoutPlaceOrderOptionsBinding.bind(sheetView)

            bind.tvCancel.setOnClickListener {
                paymentOptionBottomSheet!!.dismiss()
            }

            bind.btnWallet.setOnClickListener {
                paymentOptionBottomSheet!!.dismiss()
                callback.invoke(NSConstants.PAYMENT_WALLET)
            }

            bind.btnPaymentGateway.setOnClickListener {
                paymentOptionBottomSheet!!.dismiss()
                callback.invoke(NSConstants.PAYMENT_GATEWAY)
            }

            bind.btnMtCoin.setOnClickListener {
                paymentOptionBottomSheet!!.dismiss()
                callback.invoke(NSConstants.PAYMENT_MT_COIN)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
