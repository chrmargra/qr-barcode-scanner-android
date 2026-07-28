package me.dm7.barcodescanner.core

import android.hardware.Camera
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper

// This code is mostly based on the top answer here: http://stackoverflow.com/questions/18149964/best-use-of-handlerthread-over-other-similar-classes
class CameraHandlerThread(
    private val scannerView: BarcodeScannerView,
) : HandlerThread("CameraHandlerThread") {

    init {
        start()
    }

    fun startCamera(cameraId: Int) {
        val localHandler = Handler(looper)
        localHandler.post {
            val camera: Camera? = CameraUtils.getCameraInstance(cameraId)
            val mainHandler = Handler(Looper.getMainLooper())
            mainHandler.post {
                scannerView.setupCameraPreview(
                    CameraWrapper.getWrapper(camera, cameraId)
                )
            }
        }
    }
}
