package com.rajat.pdfviewer.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import java.io.*

object FileUtils {
    @Throws(IOException::class)
    fun fileFromAsset(context: Context, assetName: String): File {
        val outFile = File(context.cacheDir, "$assetName")
        if (assetName.contains("/")) {
            outFile.parentFile.mkdirs()
        }
        copy(context.assets.open(assetName), outFile)
        return outFile
    }

    @Throws(IOException::class)
    fun copy(inputStream: InputStream?, output: File?) {
        var outputStream: OutputStream? = null
        try {
                outputStream = FileOutputStream(output)
            var read = 0
            val bytes = ByteArray(1024)
            while (inputStream!!.read(bytes).also { read = it } != -1) {
                outputStream.write(bytes, 0, read)
            }
        } finally {
            try {
                inputStream?.close()
            } finally {
                outputStream?.close()
            }
        }
    }

    @Throws(IOException::class)
    fun downloadFile(context: Context, assetName: String, filePath: String, fileName: String?){

       val dirPath = "${Environment.getExternalStorageDirectory()}/${filePath}"
        val outFile = File(dirPath)
        //Create New File if not present
        if (!outFile.exists()) {
            outFile.mkdirs()
        }
        val outFile1 = File(dirPath, "/${fileName?.replace(".pdf", "")}.pdf")
        try {
            copy(FileInputStream(assetName), outFile1)
            showDownloadCompleteNotification(context, outFile1)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun showDownloadCompleteNotification(context: Context, file: File) {
        val channelId = "download_channel"
        val channelName = "Download Channel"
        val notificationId = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Create an intent to open the file
        val openFileIntent = Intent(Intent.ACTION_VIEW)
        val fileUri = FileProvider.getUriForFile(context,context.packageName + ".provider",file)
        openFileIntent.setDataAndType(fileUri, "application/pdf")
        openFileIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openFileIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("Download Complete")
            .setContentText("File: ${file.name}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManagerCompat = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManagerCompat.notify(notificationId, builder.build())
    }
}