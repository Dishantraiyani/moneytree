package maulik.barcodescanner.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Size
import android.view.OrientationEventListener
import android.view.Surface
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.github.drjacky.imagepicker.ImagePicker
import com.google.common.util.concurrent.ListenableFuture
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import maulik.barcodescanner.OnScannerResponse
import maulik.barcodescanner.R
import maulik.barcodescanner.analyzer.MLKitBarcodeAnalyzer
import maulik.barcodescanner.analyzer.ScanningResultListener
import maulik.barcodescanner.analyzer.ZXingBarcodeAnalyzer
import maulik.barcodescanner.databinding.ActivityBarcodeScanningBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

const val ARG_SCANNING_SDK = "scanning_SDK"

class BarcodeScanningActivity : AppCompatActivity() {

    companion object {
        private var scanCallback: OnScannerResponse? = null

        @JvmStatic
        fun start(context: Context, scannerSDK: ScannerSDK, scan: OnScannerResponse) {
            val starter = Intent(context, BarcodeScanningActivity::class.java).apply {
                putExtra(ARG_SCANNING_SDK, scannerSDK)
            }
            scanCallback = scan
            context.startActivity(starter)
        }
    }

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var binding: ActivityBarcodeScanningBinding
    /** Blocking camera operations are performed using this executor */
    private lateinit var cameraExecutor: ExecutorService
    private var flashEnabled = false
    private var scannerSDK: ScannerSDK = ScannerSDK.MLKIT //default is MLKit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeScanningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scannerSDK = intent?.getSerializableExtra(ARG_SCANNING_SDK) as ScannerSDK

        when (scannerSDK) {
           // ScannerSDK.MLKIT -> binding.ivScannerLogo.setImageResource(R.drawable.mlkit_icon)
           // ScannerSDK.ZXING -> binding.ivScannerLogo.setImageResource(R.drawable.zxing)
			else -> {}
		}

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))

        binding.overlay.post {
            binding.overlay.setViewFinder()
        }

        binding.ivCloseDocument.setOnClickListener {
            onBackPressed()
        }

		binding.ivGallery.setOnClickListener {
			launcher.launch(ImagePicker.with(this).galleryOnly().createIntent())
		}
    }

    override fun onBackPressed() {
        scanCallback?.onScan(false, "")
        super.onBackPressed()
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider?) {

        if (isDestroyed || isFinishing) {
            //This check is to avoid an exception when trying to re-bind use cases but user closes the activity.
            //java.lang.IllegalArgumentException: Trying to create use case mediator with destroyed lifecycle.
            return
        }

        cameraProvider?.unbindAll()

        val preview: Preview = Preview.Builder()
            .build()

        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(binding.cameraPreview.width, binding.cameraPreview.height))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        val orientationEventListener = object : OrientationEventListener(this as Context) {
            override fun onOrientationChanged(orientation : Int) {
                // Monitors orientation values to determine the target rotation value
                val rotation : Int = when (orientation) {
                    in 45..134 -> Surface.ROTATION_270
                    in 135..224 -> Surface.ROTATION_180
                    in 225..314 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageAnalysis.targetRotation = rotation
            }
        }
        orientationEventListener.enable()

        //switch the analyzers here, i.e. MLKitBarcodeAnalyzer, ZXingBarcodeAnalyzer
        class ScanningListener : ScanningResultListener {
            override fun onScanned(result: String) {
                runOnUiThread {
                    imageAnalysis.clearAnalyzer()
                    cameraProvider?.unbindAll()
                    scanCallback?.onScan(true, result)
                    finish()
                    /*ScannerResultDialog.newInstance(
                        result,
                        object : ScannerResultDialog.DialogDismissListener {
                            override fun onDismiss() {
                                bindPreview(cameraProvider)
                            }
                        })
                        .show(supportFragmentManager, ScannerResultDialog::class.java.simpleName)*/
                }
            }
        }

        var analyzer: ImageAnalysis.Analyzer = MLKitBarcodeAnalyzer(ScanningListener())

        if (scannerSDK == ScannerSDK.ZXING) {
            analyzer = ZXingBarcodeAnalyzer(ScanningListener())
        }

        imageAnalysis.setAnalyzer(cameraExecutor, analyzer)

        preview.setSurfaceProvider(binding.cameraPreview.surfaceProvider)

        val camera =
            cameraProvider?.bindToLifecycle(this, cameraSelector, imageAnalysis, preview)

        if (camera?.cameraInfo?.hasFlashUnit() == true) {
            //binding.ivFlashControl.visibility = View.VISIBLE

           /* binding.ivFlashControl.setOnClickListener {
                camera.cameraControl.enableTorch(!flashEnabled)
            }*/

            /*camera.cameraInfo.torchState.observe(this) {
                it?.let { torchState ->
                    if (torchState == TorchState.ON) {
                        flashEnabled = true
                        binding.ivFlashControl.setImageResource(R.drawable.ic_round_flash_on)
                    } else {
                        flashEnabled = false
                        binding.ivFlashControl.setImageResource(R.drawable.ic_round_flash_off)
                    }
                }
            }*/
        }


    }

	private val launcher =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
			if (it.resultCode == Activity.RESULT_OK) {
				val uri = it.data?.data!!
				Glide.with(this@BarcodeScanningActivity)
					.asBitmap()
					.load(FilePath.getPathFromURI(this, uri))
					.into(object : CustomTarget<Bitmap?>() {
						override fun onResourceReady(
							bMap: Bitmap,
							transition: com.bumptech.glide.request.transition.Transition<in Bitmap?>?
						) {
							var contents: String? = null
							val intArray = IntArray(bMap.width * bMap.height)
							bMap.getPixels(
								intArray,
								0,
								bMap.width,
								0,
								0,
								bMap.width,
								bMap.height
							)
							val source: LuminanceSource =
								RGBLuminanceSource(bMap.width, bMap.height, intArray)
							val bitmap = BinaryBitmap(HybridBinarizer(source))
							val reader: Reader = MultiFormatReader()
							val result: Result?
							try {
								result = reader.decode(bitmap)
								contents = result.text

							} catch (e: NotFoundException) {
								e.printStackTrace()
							} catch (e: ChecksumException) {
								e.printStackTrace()
							} catch (e: FormatException) {
								e.printStackTrace()
							}
							if (contents.isNullOrEmpty()) {
								scanCallback?.onScan(false, "")
							} else {
								scanCallback?.onScan(true, contents)
								finish()
							}
						}

						override fun onLoadCleared(placeholder: Drawable?) {
							//mDecodeImageCallback.decodeFail(0, "Decode image failed3.")
							scanCallback?.onScan(false, "")
						}
					})
			}
		}

    override fun onDestroy() {
        super.onDestroy()
        // Shut down our background executor
        cameraExecutor.shutdown()
    }

    enum class ScannerSDK {
        MLKIT,
        ZXING
    }
}
