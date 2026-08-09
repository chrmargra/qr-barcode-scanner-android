package me.dm7.barcodescanner.core.camera

import android.hardware.Camera

/**
 * Associates an opened legacy [Camera] instance with the identifier used to open
 * it.
 *
 * @property camera Opened camera instance.
 * @property cameraId Camera identifier used for orientation and configuration.
 */
class CameraWrapper private constructor(
    val camera: Camera,
    val cameraId: Int
) {

    companion object {

        /**
         * Creates a camera wrapper when [camera] is available.
         *
         * @return A new wrapper, or `null` when [camera] is `null`.
         */
        fun getWrapper(
            camera: Camera?,
            cameraId: Int
        ): CameraWrapper? = if (camera == null) {
            null
        } else {
            CameraWrapper(
                camera = camera,
                cameraId = cameraId
            )
        }
    }
}
