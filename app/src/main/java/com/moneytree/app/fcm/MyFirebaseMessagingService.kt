package com.moneytree.app.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.moneytree.app.R
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSLog
import com.moneytree.app.common.NSLoginPreferences
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.database.MainDatabase
import com.moneytree.app.repository.network.responses.NSFcmResponse
import com.moneytree.app.ui.main.NSMainActivity
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MyFirebaseMessagingService : FirebaseMessagingService() {

    val pref = NSApplication.getInstance().getPrefs()
    private var fcmResponse: NSFcmResponse? = null
	private var loginPref: NSLoginPreferences? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
			val map = remoteMessage.data
			val title = map["title"]
			val body = map["body"]
			val channelId = map["channelId"]
			val sound = map["sound"]
			val imageUrl = map["img"]
			val orderId = map["order_id"]
			val type = map["type"]
			fcmResponse = NSFcmResponse(title, body, channelId, sound, if (imageUrl == null) null else imageUrl.toString(), orderId, type)
			sendNotification(fcmResponse!!)
        } else {
            remoteMessage.notification?.let {
				fcmResponse = NSFcmResponse(it.title, it.body, it.channelId, it.sound, if (it.imageUrl == null) null else it.imageUrl.toString())
				sendNotification(fcmResponse!!)
            }
        }
    }

    override fun onNewToken(token: String) {
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        NSLog.d(TAG, "sendRegistrationTokenToServer($token)")
		loginPref = NSLoginPreferences(this)
		loginPref?.notificationToken = token
    }

    private fun sendNotification(fcmResponse: NSFcmResponse) {
		val intent: Intent = Intent(this, NSMainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT else PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        var channelId = getString(R.string.default_notification_channel_id)
        if (!fcmResponse.channelId.isNullOrEmpty()) {
            channelId = fcmResponse.channelId!!
        }
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(if (fcmResponse.title == null) "" else fcmResponse.title)
            .setContentText(if (fcmResponse.body == null) "" else fcmResponse.body)
            .setAutoCancel(true)
            .setSound(if (fcmResponse.sound.isNullOrEmpty()) defaultSoundUri else Uri.parse(fcmResponse.sound))
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                resources.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        if (!fcmResponse.imageUrl.isNullOrEmpty()) {
            val bitmap: Bitmap = getBitmapFromUrl(fcmResponse.imageUrl.toString())!!
            notificationBuilder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap)
                    .bigLargeIcon(null)
            ).setLargeIcon(bitmap)
        }

        notificationManager.notify(0 , notificationBuilder.build())
    }

    private fun getBitmapFromUrl(imageUrl: String?): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            NSLog.e("awesome", "Error in getting notification image: " + e.localizedMessage)
            null
        }
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService1"
    }
}
