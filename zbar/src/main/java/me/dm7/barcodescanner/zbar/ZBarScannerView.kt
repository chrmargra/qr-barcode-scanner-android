package me.dm7.barcodescanner.zbar

import android.content.Context
import android.content.res.Configuration
import android.hardware.Camera
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.AttributeSet
import me.dm7.barcodescanner.core.BarcodeScannerView
import me.dm7.barcodescanner.core.logger.QRBarcodeLogger
import me.dm7.barcodescanner.core.util.DisplayUtils
import net.sourceforge.zbar.Config
import net.sourceforge.zbar.Image
import net.sourceforge.zbar.ImageScanner
import net.sourceforge.zbar.Symbol

private const val TAG = "ZBarScannerView"
private const val ICONV_LIBRARY_NAME = "iconv"
private const val ROTATION_COUNT_90_DEGREES = 1
private const val ROTATION_COUNT_270_DEGREES = 3
private const val ZBAR_IMAGE_FORMAT_Y800 = "Y800"

/**
 * Camera-based barcode scanner view backed by the ZBar decoding engine.
 *
 * By default, the scanner attempts to decode every format included in
 * [BarcodeFormat.ALL_FORMATS]. Use [setFormats] to restrict scanning to specific
 * barcode formats and [setResultHandler] to receive decoded results.
 *
 * When a barcode is detected, the camera preview is stopped and the result is
 * delivered on the Android main thread. Call [resumeCameraPreview] to continue
 * scanning.
 */
open class ZBarScannerView : BarcodeScannerView {

    private var scanner: ImageScanner? = null
    private var storedFormats: List<BarcodeFormat>? = null
    private var storedResultHandler: ResultHandler? = null

    private val activeScanner: ImageScanner
        get() = scanner ?: throw NullPointerException()

    constructor(context: Context) : super(context) {
        setupScanner()
    }

    constructor(
        context: Context,
        attributeSet: AttributeSet?,
    ) : super(context, attributeSet) {
        setupScanner()
    }

    /**
     * Restricts scanning to the specified [formats].
     *
     * Passing `null` restores the default formats defined in
     * [BarcodeFormat.ALL_FORMATS]. Passing an empty list disables every barcode
     * format.
     */
    open fun setFormats(formats: List<BarcodeFormat>?) {
        storedFormats = formats
        setupScanner()
    }

    /**
     * Sets the handler that receives decoded results.
     *
     * Passing `null` clears the current handler, causing subsequent preview frames
     * to be ignored.
     */
    open fun setResultHandler(resultHandler: ResultHandler?) {
        storedResultHandler = resultHandler
    }

    /**
     * Returns the formats configured through [setFormats], or
     * [BarcodeFormat.ALL_FORMATS] if no custom formats have been configured.
     */
    open fun getFormats(): Collection<BarcodeFormat> = storedFormats ?: BarcodeFormat.ALL_FORMATS

    /**
     * Recreates and configures the underlying ZBar scanner.
     *
     * All barcode formats are initially disabled before enabling the formats returned
     * by [getFormats]. The horizontal and vertical scan densities are configured
     * automatically.
     *
     * This method is called during view construction and whenever [setFormats] is
     * invoked. Overrides must not depend on subclass properties having already been
     * initialized.
     */
    open fun setupScanner() {
        scanner = ImageScanner()
        activeScanner.setConfig(0, Config.X_DENSITY, 3)
        activeScanner.setConfig(0, Config.Y_DENSITY, 3)

        activeScanner.setConfig(Symbol.NONE, Config.ENABLE, 0)
        for (format in getFormats()) {
            activeScanner.setConfig(format.id, Config.ENABLE, 1)
        }
    }

    /**
     * Processes a camera preview frame and attempts to decode a barcode inside the
     * scanner's framing rectangle.
     *
     * Preview data is supplied to ZBar as a grayscale image and decoded contents are
     * read as UTF-8. When no barcode is found, another one-shot preview frame is
     * requested. When detection succeeds, the preview is stopped and the result is
     * delivered to the registered handler on the Android main thread.
     *
     * This callback is invoked by the camera and should not normally be called
     * directly by applications.
     */
    override fun onPreviewFrame(
        data: ByteArray?,
        camera: Camera?
    ) {
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
        } catch (exception: RuntimeException) {
            // TODO: Terrible hack. It is possible that this method is invoked after camera is released.
            QRBarcodeLogger.error(
                tag = TAG,
                message = exception.toString(),
                throwable = exception
            )
        }
    }

    /**
     * Registers the handler and resumes the camera preview.
     *
     * Call this method after receiving a result to continue scanning. Passing
     * `null` resumes the preview without processing scan results.
     */
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
