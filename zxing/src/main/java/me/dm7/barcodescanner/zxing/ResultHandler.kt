package me.dm7.barcodescanner.zxing

import com.google.zxing.Result

/**
 * Receives barcode scan results produced by [ZXingScannerView].
 *
 * Results are delivered on the Android main thread. After a result is delivered,
 * the camera preview is stopped and the registered handler is cleared. Call
 * [ZXingScannerView.resumeCameraPreview] to continue scanning.
 */
interface ResultHandler {

    fun handleResult(rawResult: Result)
}
