package me.dm7.barcodescanner.core

import android.hardware.Camera

class CameraWrapper private constructor(
    val camera: Camera,
    val cameraId: Int
) {

    companion object {
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
