package me.dm7.barcodescanner.core.camera

import android.hardware.Camera
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import me.dm7.barcodescanner.core.BarcodeScannerView

// This code is mostly based on the top answer here: http://stackoverflow.com/questions/18149964/best-use-of-handlerthread-over-other-similar-classes
private const val HANDLER_THREAD_NAME = "CameraHandlerThread"

/**
 * Background thread used to open a camera without blocking the Android main
 * thread.
 *
 * The thread starts during construction. Camera-open results are delivered back
 * to the associated [BarcodeScannerView] on the main thread.
 */
open class CameraHandlerThread(
    private val scannerView: BarcodeScannerView
) : HandlerThread(HANDLER_THREAD_NAME) {

    init {
        start()
    }

    /**
     * Attempts to open [cameraId] asynchronously.
     *
     * If the camera cannot be opened, the scanner receives a `null` camera wrapper
     * and no preview is initialized.
     */
    open fun startCamera(cameraId: Int) {
        val localHandler = Handler(looper)
        localHandler.post {
            val camera: Camera? = CameraUtils.getCameraInstance(cameraId = cameraId)
            val mainHandler = Handler(Looper.getMainLooper())
            mainHandler.post {
                scannerView.setupCameraPreview(
                    cameraWrapper = CameraWrapper.getWrapper(
                        camera = camera,
                        cameraId = cameraId
                    )
                )
            }
        }
    }
}
