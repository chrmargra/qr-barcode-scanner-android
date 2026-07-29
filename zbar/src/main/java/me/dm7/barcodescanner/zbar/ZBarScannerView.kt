package me.dm7.barcodescanner.zbar

import android.content.Context
import android.content.res.Configuration
import android.hardware.Camera
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import me.dm7.barcodescanner.core.BarcodeScannerView
import me.dm7.barcodescanner.core.DisplayUtils
import net.sourceforge.zbar.Config
import net.sourceforge.zbar.Image
import net.sourceforge.zbar.ImageScanner
import net.sourceforge.zbar.Symbol

private const val TAG = "ZBarScannerView"
private const val ICONV_LIBRARY_NAME = "iconv"
private const val ROTATION_COUNT_90_DEGREES = 1
private const val ROTATION_COUNT_270_DEGREES = 3
private const val ZBAR_IMAGE_FORMAT_Y800 = "Y800"

open class ZBarScannerView : BarcodeScannerView {

    private var scanner: ImageScanner? = null
    private var storedFormats: List<BarcodeFormat>? = null
    private var storedResultHandler: ResultHandler? = null

    private val activeScanner: ImageScanner get() = scanner ?: throw NullPointerException()

    constructor(context: Context) : super(context) {
        setupScanner()
    }

    constructor(
        context: Context,
        attributeSet: AttributeSet?,
    ) : super(context, attributeSet) {
        setupScanner()
    }

    open fun setFormats(formats: List<BarcodeFormat>?) {
        storedFormats = formats
        setupScanner()
    }

    open fun setResultHandler(resultHandler: ResultHandler?) {
        storedResultHandler = resultHandler
    }

    open fun getFormats(): Collection<BarcodeFormat> = storedFormats ?: BarcodeFormat.ALL_FORMATS

    open fun setupScanner() {
        scanner = ImageScanner()
        activeScanner.setConfig(0, Config.X_DENSITY, 3)
        activeScanner.setConfig(0, Config.Y_DENSITY, 3)

        activeScanner.setConfig(Symbol.NONE, Config.ENABLE, 0)
        for (format in getFormats()) {
            activeScanner.setConfig(format.id, Config.ENABLE, 1)
        }
    }

    override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
        if (storedResultHandler == null) return

        try {
            val activeCamera = camera ?: throw NullPointerException()
            val parameters = activeCamera.parameters
            val size = parameters.previewSize
            var width = size.width
            var height = size.height
            var previewData = data

            if (DisplayUtils.getScreenOrientation(context) == Configuration.ORIENTATION_PORTRAIT) {
                val rotationCount = rotationCount
                if (
                    rotationCount == ROTATION_COUNT_90_DEGREES ||
                    rotationCount == ROTATION_COUNT_270_DEGREES
                ) {
                    val tmp = width
                    width = height
                    height = tmp
                }
                previewData = getRotatedData(previewData, activeCamera)
            }

            val rect = getFramingRectInPreview(
                previewWidth = width,
                previewHeight = height
            ) ?: throw NullPointerException()
            val barcode = Image(width, height, ZBAR_IMAGE_FORMAT_Y800)
            barcode.data = previewData
            barcode.setCrop(rect.left, rect.top, rect.width(), rect.height())

            val result = activeScanner.scanImage(barcode)

            if (result != 0) {
                val syms = activeScanner.results
                val rawResult = Result()
                for (sym in syms) {
                    /*
                        In order to retreive QR codes containing null bytes we need to
                        use getDataBytes() rather than getData() which uses C strings.
                        Weirdly ZBar transforms all data to UTF-8, even the data returned
                        by getDataBytes() so we have to decode it as UTF-8.
                    */
                    val symData = String(sym.dataBytes, Charsets.UTF_8)
                    if (!TextUtils.isEmpty(symData)) {
                        rawResult.contents = symData
                        rawResult.barcodeFormat = BarcodeFormat.getFormatById(sym.type)
                        break
                    }
                }

                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    /*
                        Stopping the preview can take a little long.
                        So we want to set result handler to null to discard subsequent calls to
                        onPreviewFrame.
                    */
                    val tmpResultHandler = storedResultHandler
                    storedResultHandler = null

                    stopCameraPreview()
                    tmpResultHandler?.handleResult(rawResult = rawResult)
                }
            } else {
                activeCamera.setOneShotPreviewCallback(this)
            }
        } catch (e: RuntimeException) {
            // TODO: Terrible hack. It is possible that this method is invoked after camera is released.
            Log.e(TAG, e.toString(), e)
        }
    }

    open fun resumeCameraPreview(resultHandler: ResultHandler?) {
        storedResultHandler = resultHandler
        super.resumeCameraPreview()
    }

    companion object {
        init {
            System.loadLibrary(ICONV_LIBRARY_NAME)
        }
    }
}
