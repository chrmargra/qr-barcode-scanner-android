package me.dm7.barcodescanner.zbar

/**
 * Receives barcode scan results produced by [ZBarScannerView].
 *
 * Results are delivered on the Android main thread. After a result is delivered,
 * the camera preview is stopped and the registered handler is cleared. Call
 * [ZBarScannerView.resumeCameraPreview] to continue scanning.
 */
interface ResultHandler {

    fun handleResult(rawResult: Result)
}
