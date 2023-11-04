package com.moneytree.app.ui.idCard

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.kal.rackmonthpicker.RackMonthPicker
import com.moneytree.app.BuildConfig
import com.moneytree.app.R
import com.moneytree.app.base.fragment.BaseViewModelFragment
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.databinding.NsFragmentIdCardBinding
import com.moneytree.app.databinding.NsFragmentPaymentSummaryBinding
import com.rajat.pdfviewer.PdfViewerActivity
import java.io.File
import java.io.FileOutputStream
import java.util.Locale


class NSIDCardFragment : BaseViewModelFragment<NSIDCardViewModel, NsFragmentIdCardBinding>() {

    override val viewModel: NSIDCardViewModel by lazy {
        ViewModelProvider(this)[NSIDCardViewModel::class.java]
    }

    companion object {
        fun newInstance() = NSIDCardFragment()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentIdCardBinding {
        return NsFragmentIdCardBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        viewCreated()
        setListener()
    }

    /**
     * View created
     */
    private fun viewCreated() {
        observeViewModel()
        with(binding) {
            HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.id_card))
            tvCustomerNumber.text = NSConstants.CUSTOMER_CARE
            tvCustomerEmail.text = NSConstants.CUSTOMER_CARE_EMAIL
            tvWebsite.text = NSConstants.CUSTOMER_CARE_WEB
            viewModel.apply {
                getUserDetail {
                    binding.apply {
                        tvIdValue.text = it.sponsorId
                        tvEmailValue.text = it.email
                        tvUserName.text = it.fullName
                        tvMobileValue.text = it.mobile
                        tvJoiningDateValue.text = it.createdAt
                    }
                }
            }
            Glide.with(requireContext()).load("https://moneytree.biz/upload/gallery/Moneytree.jpg").into(binding.ivQr)
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(binding) {
            with(viewModel) {

                btnSubmit.setSafeOnClickListener {
                    generateAndDownloadIdCard(requireContext())
                }
            }
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        with(viewModel) {
            isProgressShowing.observe(
                viewLifecycleOwner
            ) { shouldShowProgress ->
                updateProgress(shouldShowProgress)
            }

            failureErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                showAlertDialog(errorMessage)
            }

            apiErrors.observe(viewLifecycleOwner) { apiErrors ->
                parseAndShowApiError(apiErrors)
            }

            noNetworkAlert.observe(viewLifecycleOwner) {
                showNoNetworkAlertDialog(
                    getString(R.string.no_network_available),
                    getString(R.string.network_unreachable)
                )
            }

            validationErrorId.observe(viewLifecycleOwner) { errorId ->
                showAlertDialog(getString(errorId))
            }
        }
    }

    private fun generateAndDownloadIdCard(context: Context) {
        // Create a bitmap for the ID card
        val idCardBitmap = convertCardViewToBitmap(binding.viewId)

        val dirPath = "${Environment.getExternalStorageDirectory()}/${Environment.DIRECTORY_DOWNLOADS + "/" +NSConstants.DIRECTORY_PATH}"
        // Save the bitmap as a JPEG image
        val imageFileName = "id_card.jpg"
        val imageFile = File(dirPath, imageFileName)

        try {
            val fos = FileOutputStream(imageFile)
            idCardBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()

            // Trigger a download intent
            /*val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(imageFile.toUri(), "image/jpeg")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)*/
            showDownloadCompleteNotification(requireContext(), imageFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun convertCardViewToBitmap(cardView: LinearLayout): Bitmap {
        // Measure and layout the CardView
        cardView.measure(
            View.MeasureSpec.makeMeasureSpec(cardView.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(cardView.height, View.MeasureSpec.EXACTLY)
        )
        cardView.layout(0, 0, cardView.measuredWidth, cardView.measuredHeight)

        // Create a Bitmap with the same dimensions as the CardView
        val bitmap = Bitmap.createBitmap(
            cardView.width,
            cardView.height,
            Bitmap.Config.ARGB_8888
        )

        // Create a Canvas to render the CardView onto the Bitmap
        val canvas = Canvas(bitmap)

        // Clear the Canvas with a transparent background (optional)
        canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR)

        // Draw the CardView onto the Canvas
        cardView.draw(canvas)

        return bitmap
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
        openFileIntent.setDataAndType(fileUri, "image/jpeg")
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
