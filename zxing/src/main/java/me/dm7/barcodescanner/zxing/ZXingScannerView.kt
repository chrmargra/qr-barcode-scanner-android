package me.dm7.barcodescanner.zxing

import android.content.Context
import android.content.res.Configuration
import android.hardware.Camera
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.ReaderException
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import me.dm7.barcodescanner.core.BarcodeScannerView
import me.dm7.barcodescanner.core.logger.QRBarcodeLogger
import me.dm7.barcodescanner.core.util.DisplayUtils
import java.util.EnumMap

private const val TAG = "ZXingScannerView"
private const val ROTATION_COUNT_90_DEGREES = 1
private const val ROTATION_COUNT_270_DEGREES = 3

/**
 * Camera-based barcode scanner view backed by the ZXing decoding engine.
 *
 * By default, the scanner attempts to decode every format included in [ALL_FORMATS].
 * Use [setFormats] to restrict scanning to specific barcode formats and
 * [setResultHandler] to receive decoded results.
 *
 * When a barcode is successfully decoded, the camera preview is stopped and the
 * result is delivered on the Android main thread. Call [resumeCameraPreview] to
 * continue scanning.
 */
open class ZXingScannerView : BarcodeScannerView {

    private var multiFormatReader: MultiFormatReader? = null
    private var storedFormats: List<BarcodeFormat>? = null
    private var storedResultHandler: ResultHandler? = null

    private val activeMultiFormatReader: MultiFormatReader
        get() = multiFormatReader ?: throw NullPointerException()

    constructor(context: Context) : super(context) {
        initMultiFormatReader()
    }

    constructor(
        context: Context,
        attributeSet: AttributeSet?,
    ) : super(context, attributeSet) {
        initMultiFormatReader()
    }

    /**
     * Restricts scanning to the specified [formats].
     *
     * Passing `null` restores the default formats defined in [ALL_FORMATS].
     * An empty list does not disable scanning because ZXing falls back to its
     * default readers.
     */
    open fun setFormats(formats: List<BarcodeFormat>?) {
        storedFormats = formats
        initMultiFormatReader()
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
     * Returns the formats configured through [setFormats], or [ALL_FORMATS] if no
     * custom formats have been configured.
     */
    open fun getFormats(): Collection<BarcodeFormat> = storedFormats ?: ALL_FORMATS

    private fun initMultiFormatReader() {
        val hints = EnumMap<DecodeHintType, Any>(DecodeHintType::class.java)
        hints[DecodeHintType.POSSIBLE_FORMATS] = getFormats()

        multiFormatReader = MultiFormatReader()
        activeMultiFormatReader.setHints(hints)
    }

    /**
     * Processes a camera preview frame and attempts to decode a barcode inside the
     * scanner's framing rectangle.
     *
     * Decoding is attempted using both the original and inverted luminance data.
     * When no barcode is found, another one-shot preview frame is requested. When
     * decoding succeeds, the preview is stopped and the result is delivered to the
     * registered handler on the Android main thread.
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
                previewData = getRotatedData(
                    data = previewData,
                    camera = activeCamera
                )
            }

            var rawResult: Result? = null
            val source = buildLuminanceSource(
                data = previewData,
                width = width,
                height = height
            )

            if (source != null) {
                var bitmap = BinaryBitmap(HybridBinarizer(source))
                try {
                    rawResult = activeMultiFormatReader.decodeWithState(bitmap)
                } catch (_: ReaderException) {
                    // Continue
                } catch (_: NullPointerException) {
                    // This is terrible
                } catch (_: ArrayIndexOutOfBoundsException) {
                    // Ignored
                } finally {
                    activeMultiFormatReader.reset()
                }

                if (rawResult == null) {
                    val invertedSource = source.invert()
                    bitmap = BinaryBitmap(HybridBinarizer(invertedSource))
                    try {
                        rawResult = activeMultiFormatReader.decodeWithState(bitmap)
                    } catch (_: NotFoundException) {
                        // Continue
                    } finally {
                        activeMultiFormatReader.reset()
                    }
                }
            }

            val finalRawResult = rawResult

            if (finalRawResult != null) {
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
                    tmpResultHandler?.handleResult(rawResult = finalRawResult)
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

    /**
     * Creates a luminance source cropped to the scanner's framing rectangle.
     *
     * Override this method to customize how camera preview data is prepared for
     * decoding.
     *
     * @return The cropped luminance source, or `null` if it cannot be created.
     */
    open fun buildLuminanceSource(
        data: ByteArray?,
        width: Int,
        height: Int
    ): PlanarYUVLuminanceSource? {
        val rect = getFramingRectInPreview(
            previewWidth = width,
            previewHeight = height
        ) ?: return null

        // Go ahead and assume it's YUV rather than die.
        var source: PlanarYUVLuminanceSource? = null

        try {
            source = PlanarYUVLuminanceSource(
                data,
                width,
                height,
                rect.left,
                rect.top,
                rect.width(),
                rect.height(),
                false,
            )
        } catch (_: Exception) {
            // Ignored
        }

        return source
    }

    companion object {

        /** Default barcode formats used when no custom formats have been configured. */
        val ALL_FORMATS: MutableList<BarcodeFormat> = arrayListOf(
            BarcodeFormat.AZTEC,
            BarcodeFormat.CODABAR,
            BarcodeFormat.CODE_39,
            BarcodeFormat.CODE_93,
            BarcodeFormat.CODE_128,
            BarcodeFormat.DATA_MATRIX,
            BarcodeFormat.EAN_8,
            BarcodeFormat.EAN_13,
            BarcodeFormat.ITF,
            BarcodeFormat.MAXICODE,
            BarcodeFormat.PDF_417,
            BarcodeFormat.QR_CODE,
            BarcodeFormat.RSS_14,
            BarcodeFormat.RSS_EXPANDED,
            BarcodeFormat.UPC_A,
            BarcodeFormat.UPC_E,
            BarcodeFormat.UPC_EAN_EXTENSION
        )
    }
}
