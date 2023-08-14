package com.moneytree.app.common

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.anilokcun.uwmediapicker.UwMediaPicker
import com.anilokcun.uwmediapicker.model.UwMediaPickerMediaModel
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URLConnection

object SelectImageFiles {
    fun selectClassifiedImage(
        activity: AppCompatActivity,
        maxImages: Int,
        callback: ((MutableList<String>) -> Unit)
    ) {
        try {
            UwMediaPicker
                .with(activity)
                .setGalleryMode(UwMediaPicker.GalleryMode.ImageGallery)
                .setGridColumnCount(4)
                .setMaxSelectableMediaCount(maxImages)
                .setLightStatusBar(true)
                .enableImageCompression(true)
                .setCompressionMaxWidth(1280f)
                .setCompressionMaxHeight(720f)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setCompressionQuality(85)
                .setCompressedFileDestinationPath(activity.getExternalFilesDir(null)!!.path + "/" + Environment.DIRECTORY_DOCUMENTS)
                .setCancelCallback { null }
                .launch { uwMediaPickerMediaModels: List<UwMediaPickerMediaModel?>? ->
                    val list = ArrayList<String>()
                    if (uwMediaPickerMediaModels != null) {
                        for (media in uwMediaPickerMediaModels) {
                            media?.mediaPath?.let { list.add(it) }
                        }
                    }
                    callback.invoke(list)
                    null
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun requestImage(listImage: List<String?>, activity: Activity?, files: String): List<MultipartBody.Part> {
        val surveyImagesParts: MutableList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()
        for (index in listImage.indices) {
            val file = File(listImage[index])
            var mediaType = "*/*"
            mediaType = if (isImageFile(file.absolutePath)) {
                "image/jpeg"
            } else {
                "video/mp4"
            }
            val surveyBody: RequestBody = RequestBody.create(mediaType.toMediaTypeOrNull(), file)
            surveyImagesParts.add(
                MultipartBody.Part.createFormData(
                    "$files[$index]",
                    file.name,
                    surveyBody
                )
            )
        }
        return surveyImagesParts
    }

    fun isImageFile(path: String?): Boolean {
        val mimeType = URLConnection.guessContentTypeFromName(path)
        return mimeType != null && mimeType.startsWith("image")
    }

    fun getRealPathFromURIPath(contentURI: Uri, activity: Activity): String? {
        return try {
            if (contentURI.scheme != null) {
                @SuppressLint("Recycle") val cursor =
                    activity.contentResolver.query(contentURI, null, null, null, null)
                if (cursor == null) {
                    contentURI.path
                } else {
                    cursor.moveToFirst()
                    val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    cursor.getString(idx)
                }
            } else {
                ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Share Post",
            null
        )
        return Uri.parse(path)
    }
}