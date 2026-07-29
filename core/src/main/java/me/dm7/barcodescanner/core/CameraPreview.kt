package me.dm7.barcodescanner.core

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.hardware.Camera
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import kotlin.math.abs
import kotlin.math.max

private const val TAG = "CameraPreview"
private const val ROTATION_DEGREES_0 = 0
private const val ROTATION_DEGREES_90 = 90
private const val ROTATION_DEGREES_180 = 180
private const val ROTATION_DEGREES_270 = 270
private const val FULL_ROTATION_DEGREES = 360
private const val HALF_ROTATION_DEGREES = 180
private const val NO_ROTATION_DEGREES = 0
private const val DELAY = 1000L
private const val DEFAULT_CAMERA_ID = -1

open class CameraPreview : SurfaceView, SurfaceHolder.Callback {

    private var cameraWrapper: CameraWrapper? = null
    private var autoFocusHandler: Handler? = null
    private var previewing = true
    private var autoFocusState = true
    private var surfaceCreatedState = false
    private var scaleToFillState = true
    private var previewCallback: Camera.PreviewCallback? = null
    private var storedAspectTolerance = 0.1f

    private val activeCameraWrapper: CameraWrapper
        get() = cameraWrapper ?: throw NullPointerException()

    private val activeAutoFocusHandler: Handler
        get() = autoFocusHandler ?: throw NullPointerException()

    private val doAutoFocus = Runnable {
        if (
            cameraWrapper != null &&
            previewing &&
            autoFocusState &&
            surfaceCreatedState
        ) {
            safeAutoFocus()
        }
    }

    // Mimic continuous auto-focusing
    private var autoFocusCB = Camera.AutoFocusCallback { _, _ ->
        scheduleAutoFocus()
    }

    open val displayOrientation: Int
        get() {
            val currentCameraWrapper = cameraWrapper
                ?: return 0 // If we don't have a camera set there is no orientation so return dummy value


            val info = Camera.CameraInfo()

            if (currentCameraWrapper.cameraId == DEFAULT_CAMERA_ID) {
                Camera.getCameraInfo(
                    Camera.CameraInfo.CAMERA_FACING_BACK,
                    info
                )
            } else {
                Camera.getCameraInfo(
                    currentCameraWrapper.cameraId,
                    info
                )
            }

            val service =
                context.getSystemService(Context.WINDOW_SERVICE) ?: throw NullPointerException()
            val windowManager = service as WindowManager

            return getDisplayOrientation(
                windowManager = windowManager,
                info = info
            )
        }

    constructor(
        context: Context,
        cameraWrapper: CameraWrapper?,
        previewCallback: Camera.PreviewCallback?
    ) : super(context) {
        init(cameraWrapper, previewCallback)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        cameraWrapper: CameraWrapper?,
        previewCallback: Camera.PreviewCallback?
    ) : super(context, attrs) {
        init(cameraWrapper, previewCallback)
    }

    open fun init(
        cameraWrapper: CameraWrapper?,
        previewCallback: Camera.PreviewCallback?
    ) {
        setCamera(
            cameraWrapper = cameraWrapper,
            previewCallback = previewCallback
        )
        autoFocusHandler = Handler()
        holder.addCallback(this)
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    open fun setCamera(
        cameraWrapper: CameraWrapper?,
        previewCallback: Camera.PreviewCallback?
    ) {
        this.cameraWrapper = cameraWrapper
        this.previewCallback = previewCallback
    }

    open fun setShouldScaleToFill(scaleToFill: Boolean) {
        scaleToFillState = scaleToFill
    }

    open fun setAspectTolerance(aspectTolerance: Float) {
        storedAspectTolerance = aspectTolerance
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        surfaceCreatedState = true
    }

    override fun surfaceChanged(
        surfaceHolder: SurfaceHolder,
        i: Int,
        i2: Int,
        i3: Int
    ) {
        if (surfaceHolder.surface == null) return

        stopCameraPreview()
        showCameraPreview()
    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
        surfaceCreatedState = false
        stopCameraPreview()
    }

    open fun showCameraPreview() {
        if (cameraWrapper != null) {
            try {
                holder.addCallback(this)
                previewing = true
                setupCameraParameters()

                activeCameraWrapper.camera.setPreviewDisplay(holder)
                activeCameraWrapper.camera.setDisplayOrientation(displayOrientation)
                activeCameraWrapper.camera.setOneShotPreviewCallback(previewCallback)
                activeCameraWrapper.camera.startPreview()

                if (autoFocusState) {
                    if (surfaceCreatedState) {
                        // check if surface created before using autofocus
                        safeAutoFocus()
                    } else {
                        // wait 1 sec and then do check again
                        scheduleAutoFocus()
                    }
                }
            } catch (exception: Exception) {
                Log.e(TAG, exception.toString(), exception)
            }
        }
    }

    open fun safeAutoFocus() {
        try {
            activeCameraWrapper.camera.autoFocus(autoFocusCB)
        } catch (exception: RuntimeException) {
            // Horrible hack to deal with autofocus errors on Sony devices
            // See https://github.com/dm77/barcodescanner/issues/7 for example
            scheduleAutoFocus() // wait 1 sec and then do check again
        }
    }

    open fun stopCameraPreview() {
        if (cameraWrapper != null) {
            try {
                previewing = false
                holder.removeCallback(this)
                activeCameraWrapper.camera.cancelAutoFocus()
                activeCameraWrapper.camera.setOneShotPreviewCallback(null)
                activeCameraWrapper.camera.stopPreview()
            } catch (exception: Exception) {
                Log.e(TAG, exception.toString(), exception)
            }
        }
    }

    open fun setupCameraParameters() {
        val optimalSize = getOptimalPreviewSize()
        val parameters = activeCameraWrapper.camera.parameters
        val activeOptimalSize = optimalSize ?: throw NullPointerException()

        parameters.setPreviewSize(
            activeOptimalSize.width,
            activeOptimalSize.height
        )

        activeCameraWrapper.camera.parameters = parameters
        adjustViewSize(cameraSize = activeOptimalSize)
    }

    private fun adjustViewSize(cameraSize: Camera.Size) {
        val previewSize = convertSizeToLandscapeOrientation(
            size = Point(width, height)
        )

        val cameraRatio = cameraSize.width.toFloat() / cameraSize.height.toFloat()
        val screenRatio = previewSize.x.toFloat() / previewSize.y.toFloat()

        if (screenRatio > cameraRatio) {
            setViewSize(
                width = (previewSize.y * cameraRatio).toInt(),
                height = previewSize.y
            )
        } else {
            setViewSize(
                width = previewSize.x,
                height = (previewSize.x / cameraRatio).toInt()
            )
        }
    }

    private fun convertSizeToLandscapeOrientation(size: Point): Point =
        if (displayOrientation % HALF_ROTATION_DEGREES == NO_ROTATION_DEGREES) {
            size
        } else {
            Point(size.y, size.x)
        }

    private fun setViewSize(width: Int, height: Int) {
        val currentLayoutParams = layoutParams
        var tmpWidth: Int
        var tmpHeight: Int

        if (displayOrientation % HALF_ROTATION_DEGREES == NO_ROTATION_DEGREES) {
            tmpWidth = width
            tmpHeight = height
        } else {
            tmpWidth = height
            tmpHeight = width
        }

        if (scaleToFillState) {
            val currentParent = parent ?: throw NullPointerException()
            val parentView = currentParent as View
            val parentWidth = parentView.width
            val parentHeight = parentView.height
            val ratioWidth = parentWidth.toFloat() / tmpWidth.toFloat()
            val ratioHeight = parentHeight.toFloat() / tmpHeight.toFloat()

            val compensation = max(a = ratioWidth, b = ratioHeight)

            tmpWidth = Math.round(tmpWidth * compensation)
            tmpHeight = Math.round(tmpHeight * compensation)
        }

        val activeLayoutParams = currentLayoutParams ?: throw NullPointerException()

        activeLayoutParams.width = tmpWidth
        activeLayoutParams.height = tmpHeight
        layoutParams = activeLayoutParams
    }

    private fun getDisplayOrientation(
        windowManager: WindowManager,
        info: Camera.CameraInfo
    ): Int {
        val display = windowManager.defaultDisplay

        val degrees = when (display.rotation) {
            Surface.ROTATION_0 -> ROTATION_DEGREES_0
            Surface.ROTATION_90 -> ROTATION_DEGREES_90
            Surface.ROTATION_180 -> ROTATION_DEGREES_180
            Surface.ROTATION_270 -> ROTATION_DEGREES_270
            else -> ROTATION_DEGREES_0
        }

        val result: Int

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            val cameraRotation = (info.orientation + degrees) % FULL_ROTATION_DEGREES

            result =
                (FULL_ROTATION_DEGREES - cameraRotation) % FULL_ROTATION_DEGREES // Compensate the mirror
        } else {
            // Back-facing
            result = (info.orientation - degrees + FULL_ROTATION_DEGREES) % FULL_ROTATION_DEGREES
        }

        return result
    }

    private fun getOptimalPreviewSize(): Camera.Size? {
        if (cameraWrapper == null) return null

        val sizes: List<Camera.Size>? = activeCameraWrapper.camera.parameters.supportedPreviewSizes

        var width = width
        var height = height

        if (DisplayUtils.getScreenOrientation(context) == Configuration.ORIENTATION_PORTRAIT) {
            val portraitWidth = height
            height = width
            width = portraitWidth
        }

        val targetRatio = width.toDouble() / height.toDouble()

        if (sizes == null) return null

        var optimalSize: Camera.Size? = null
        var minDiff = Double.MAX_VALUE
        val targetHeight = height

        // Try to find an size match aspect ratio and size
        for (size in sizes) {
            val ratio =
                size.width.toDouble() / size.height.toDouble()

            if (abs(x = ratio - targetRatio) > storedAspectTolerance) continue

            val heightDifference = abs(n = size.height - targetHeight).toDouble()

            if (heightDifference < minDiff) {
                optimalSize = size
                minDiff = heightDifference
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE

            for (size in sizes) {
                val heightDifference = abs(n = size.height - targetHeight).toDouble()

                if (heightDifference < minDiff) {
                    optimalSize = size
                    minDiff = heightDifference
                }
            }
        }

        return optimalSize
    }

    open fun setAutoFocus(isEnabled: Boolean) {
        if (cameraWrapper != null && previewing) {
            if (isEnabled == autoFocusState) return

            autoFocusState = isEnabled

            if (autoFocusState) {
                if (surfaceCreatedState) {
                    // Check if surface created before using autofocus
                    Log.v(TAG, "Starting autofocus")
                    safeAutoFocus()
                } else {
                    // Wait 1 sec and then do check again
                    scheduleAutoFocus()
                }
            } else {
                Log.v(TAG, "Cancelling autofocus")
                activeCameraWrapper.camera.cancelAutoFocus()
            }
        }
    }

    private fun scheduleAutoFocus() {
        activeAutoFocusHandler.postDelayed(doAutoFocus, DELAY)
    }
}
