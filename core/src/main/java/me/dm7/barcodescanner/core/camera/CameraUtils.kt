package me.dm7.barcodescanner.core.camera

import android.hardware.Camera

private const val DEFAULT_CAMERA_ID = -1

/**
 * Utility functions for selecting, opening and inspecting legacy Android cameras.
 */
object CameraUtils {

    /**
     * Attempts to open the preferred camera.
     *
     * @return The opened camera, or `null` if it is unavailable, already in use,
     * missing or cannot be accessed.
     */
    fun getCameraInstance(): Camera? = getCameraInstance(cameraId = getDefaultCameraId())

    /**
     * Attempts to open the camera identified by [cameraId].
     *
     * Passing `-1` uses the system default camera.
     *
     * @return The opened camera, or `null` if it cannot be accessed.
     */
    fun getCameraInstance(cameraId: Int): Camera? =
        try {
            if (cameraId == DEFAULT_CAMERA_ID) {
                Camera.open() // Attempt to get a Camera instance
            } else {
                Camera.open(cameraId) // Attempt to get a Camera instance
            }
        } catch (_: Exception) {
            // Camera is not available, in use, or does not exist.
            null
        }

    /**
     * Returns the preferred camera identifier.
     *
     * A back-facing camera is preferred. If none exists, another available camera is
     * returned.
     *
     * @return The selected camera identifier, or `-1` when no camera is available.
     */
    fun getDefaultCameraId(): Int {
        val numberOfCameras = Camera.getNumberOfCameras()
        val cameraInfo = Camera.CameraInfo()
        var defaultCameraId = DEFAULT_CAMERA_ID

        for (cameraId in 0 until numberOfCameras) {
            defaultCameraId = cameraId
            Camera.getCameraInfo(cameraId, cameraInfo)
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) return cameraId
        }

        return defaultCameraId
    }

    /**
     * Checks whether [camera] supports a usable flash mode.
     *
     * @return `false` when the camera is `null`, has no flash mode, or only supports
     * flash-off mode.
     */
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
