package maulik.barcodescanner.ui

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.yalantis.ucrop.util.FileUtils.*

open class FilePath {

	companion object {
		fun getPathFromURI(context: Context, uri: Uri): String? {
			val isKitKat: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

			// DocumentProvider
			if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
				// ExternalStorageProvider
				if (isExternalStorageDocument(uri)) {
					val docId: String = DocumentsContract.getDocumentId(uri)
					val split = docId.split(":").toTypedArray()
					val type = split[0]
					if ("primary".equals(type, ignoreCase = true)) {
						return "${Environment.getExternalStorageDirectory()}/" + split[1]
					}
				} else if (isDownloadsDocument(uri)) {
					val id: String = DocumentsContract.getDocumentId(uri)
					val contentUri: Uri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						java.lang.Long.valueOf(id)
					)
					return getDataColumn(context, contentUri, null, null)
				} else if (isMediaDocument(uri)) {
					val docId: String = DocumentsContract.getDocumentId(uri)
					val split = docId.split(":").toTypedArray()
					val type = split[0]
					var contentUri: Uri? = null
					if ("image" == type) {
						contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
					} else if ("video" == type) {
						contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
					} else if ("audio" == type) {
						contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
					}
					val selection = "_id=?"
					val selectionArgs = arrayOf(
						split[1]
					)
					return contentUri?.let { getDataColumn(context, it, selection, selectionArgs) }
				}
			} else if ("content".equals(uri.scheme, ignoreCase = true)) {
				return getDataColumn(context, uri, null, null)
			} else if ("file".equals(uri.scheme, ignoreCase = true)) {
				return uri.path
			}
			return null
		}
	}
}
