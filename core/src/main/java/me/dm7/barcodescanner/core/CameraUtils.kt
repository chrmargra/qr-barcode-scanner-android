package me.dm7.barcodescanner.core

import android.hardware.Camera

object CameraUtils {

    /**
     * A safe way to get an instance of the Camera object.
     */
    @JvmStatic
    fun getCameraInstance(): Camera? = getCameraInstance(cameraId = getDefaultCameraId())

    /**
     * Favor back-facing camera by default. If none exists, fallback to whatever camera is available.
     */
    @JvmStatic
    fun getDefaultCameraId(): Int {
        val numberOfCameras = Camera.getNumberOfCameras()
        val cameraInfo = Camera.CameraInfo()
        var defaultCameraId = -1

        for (cameraId in 0 until numberOfCameras) {
            defaultCameraId = cameraId
            Camera.getCameraInfo(cameraId, cameraInfo)
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return cameraId
            }
        }

        return defaultCameraId
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    @JvmStatic
    fun getCameraInstance(cameraId: Int): Camera? {
        return try {
            if (cameraId == -1) {
                Camera.open() // Attempt to get a Camera instance
            } else {
                Camera.open(cameraId) // Attempt to get a Camera instance
            }
        } catch (_: Exception) {
            // Camera is not available, in use, or does not exist.
            null
        }
    }

    @JvmStatic
    fun isFlashSupported(camera: Camera?): Boolean {
        // Credits: Top answer at http://stackoverflow.com/a/19599365/868173
        if (camera == null) return false

        val parameters = camera.parameters

        if (parameters.flashMode == null) return false

        val supportedFlashModes = parameters.supportedFlashModes
        return supportedFlashModes != null &&
                supportedFlashModes.isNotEmpty() &&
                (supportedFlashModes.size != 1 ||
                        supportedFlashModes[0] != Camera.Parameters.FLASH_MODE_OFF)
    }
}
