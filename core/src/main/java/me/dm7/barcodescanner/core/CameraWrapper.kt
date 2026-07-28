package me.dm7.barcodescanner.core

import android.hardware.Camera

class CameraWrapper private constructor(
    @JvmField
    val camera: Camera,
    @JvmField
    val cameraId: Int
) {

    companion object {
        @JvmStatic
        fun getWrapper(
            camera: Camera?,
            cameraId: Int
        ): CameraWrapper? {
            return if (camera == null) {
                null
            } else {
                CameraWrapper(camera, cameraId)
            }
        }
    }
}
