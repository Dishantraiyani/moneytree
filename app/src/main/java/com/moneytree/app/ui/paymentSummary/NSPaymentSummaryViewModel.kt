package com.moneytree.app.ui.paymentSummary

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSAddressRepository
import com.moneytree.app.repository.NSDoctorRepository
import com.moneytree.app.repository.NSWalletRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.DoctorHistoryResponse
import com.moneytree.app.repository.network.responses.NSWalletData
import com.moneytree.app.repository.network.responses.NSWalletListResponse
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * The view model class for transaction. It handles the business logic to communicate with the model for the transaction and provides the data to the observing UI component.
 */
class NSPaymentSummaryViewModel(application: Application) : NSViewModel(application) {

    var selectedDateValue: String = ""

    /**
     * Get transaction list data
     *
     */
    fun paymentSummary(context: Context, month: String, callback: (String) -> Unit) {
        showProgress()
        callCommonApi({ obj ->
            NSAddressRepository.paymentSummary(month, obj)
        }, { data, isSuccess ->

            if (data is ResponseBody) {
                savePdfToTempFile(context, data, callback)
            } else {
                hideProgress()
            }
        })
    }

    private fun savePdfToTempFile(context: Context, pdfData: ResponseBody?, callback: (String) -> Unit) {
        if (pdfData != null) {
            try {
                val tempDir = context.cacheDir // Use the app's cache directory
                val file = File.createTempFile("summary", ".pdf", tempDir)
                val output = FileOutputStream(file)

                val buffer = ByteArray(4096)
                var bytesRead: Int
                while (pdfData.byteStream().read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                }

                output.close()
                callback.invoke(file.path)
            } catch (e: IOException) {
                e.printStackTrace()
                hideProgress()
            }
        }
    }
}
