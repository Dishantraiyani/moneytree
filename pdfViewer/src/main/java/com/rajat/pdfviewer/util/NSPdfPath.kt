package com.rajat.pdfviewer.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File


/**
 * Class that contains constants that used across modules
 */
class NSPdfPath {
    companion object {
        var PDF_PATH: File? = null

		fun sharePdf(activity: Activity, link: String?) {
			val pm = activity.packageManager
			try {
				if (PDF_PATH != null) {

					val share = Intent()
					share.action = Intent.ACTION_SEND
					share.type = "application/pdf"
					share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(PDF_PATH!!))
					share.setPackage("com.whatsapp")

					activity.startActivity(share)
				}
				/*val uri: Uri = Uri.parse(link)
				val waIntent = Intent(Intent.ACTION_SEND)
				waIntent.type = "application/pdf"
				waIntent.putExtra(Intent.EXTRA_STREAM, uri)
				activity.startActivity(Intent.createChooser(waIntent, "share"))*/
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}
}
