package me.dm7.barcodescanner.core

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Rect
import android.hardware.Camera
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat

private const val NINETY_DEGREES_ROTATION = 90

abstract class BarcodeScannerView : FrameLayout, Camera.PreviewCallback {

    private var cameraWrapper: CameraWrapper? = null
    private var preview: CameraPreview? = null
    private var viewFinderView: ViewFinder? = null
    private var framingRectInPreview: Rect? = null
    private var cameraHandlerThread: CameraHandlerThread? = null
    private var flashState: Boolean? = null
    private var autoFocusState = true
    private var scaleToFillState = true

    private var laserEnabledState = true

    private var storedLaserColor = ContextCompat.getColor(context, R.color.viewfinder_laser)

    private var storedBorderColor = ContextCompat.getColor(context, R.color.viewfinder_border)

    private var storedMaskColor = ContextCompat.getColor(context, R.color.viewfinder_mask)

    private var storedBorderWidth = resources.getInteger(R.integer.viewfinder_border_width)

    private var storedBorderLength = resources.getInteger(R.integer.viewfinder_border_length)

    private var borderCornersRounded = false
    private var storedCornerRadius = 0
    private var squareFinder = false
    private var storedBorderAlpha = 1.0f
    private var storedViewFinderOffset = 0
    private var storedAspectTolerance = 0.1f

    private val activeCameraWrapper: CameraWrapper
        get() = cameraWrapper ?: throw NullPointerException()

    private val activePreview: CameraPreview
        get() = preview ?: throw NullPointerException()

    private val activeViewFinderView: ViewFinder
        get() = viewFinderView ?: throw NullPointerException()

    open val rotationCount: Int
        get() {
            val displayOrientation = activePreview.displayOrientation
            return displayOrientation / NINETY_DEGREES_ROTATION
        }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(
        context: Context,
        attributeSet: AttributeSet?
    ) : super(context, attributeSet) {
        val attributes: TypedArray =
            context.theme.obtainStyledAttributes(
                attributeSet,
                R.styleable.BarcodeScannerView,
                0,
                0,
            )

        try {
            setShouldScaleToFill(
                attributes.getBoolean(
                    R.styleable.BarcodeScannerView_shouldScaleToFill,
                    true,
                ),
            )

            laserEnabledState = attributes.getBoolean(
                R.styleable.BarcodeScannerView_laserEnabled,
                laserEnabledState,
            )

            storedLaserColor = attributes.getColor(
                R.styleable.BarcodeScannerView_laserColor,
                storedLaserColor,
            )

            storedBorderColor = attributes.getColor(
                R.styleable.BarcodeScannerView_borderColor,
                storedBorderColor,
            )

            storedMaskColor = attributes.getColor(
                R.styleable.BarcodeScannerView_maskColor,
                storedMaskColor,
            )

            storedBorderWidth = attributes.getDimensionPixelSize(
                R.styleable.BarcodeScannerView_borderWidth,
                storedBorderWidth,
            )

            storedBorderLength = attributes.getDimensionPixelSize(
                R.styleable.BarcodeScannerView_borderLength,
                storedBorderLength,
            )

            borderCornersRounded = attributes.getBoolean(
                R.styleable.BarcodeScannerView_roundedCorner,
                borderCornersRounded,
            )

            storedCornerRadius = attributes.getDimensionPixelSize(
                R.styleable.BarcodeScannerView_cornerRadius,
                storedCornerRadius,
            )

            squareFinder = attributes.getBoolean(
                R.styleable.BarcodeScannerView_squaredFinder,
                squareFinder,
            )

            storedBorderAlpha = attributes.getFloat(
                R.styleable.BarcodeScannerView_borderAlpha,
                storedBorderAlpha,
            )

            storedViewFinderOffset = attributes.getDimensionPixelSize(
                R.styleable.BarcodeScannerView_finderOffset,
                storedViewFinderOffset,
            )
        } finally {
            attributes.recycle()
        }

        init()
    }

    private fun init() {
        viewFinderView = createViewFinderView(context = context)
    }

    fun setupLayout(cameraWrapper: CameraWrapper?) {
        removeAllViews()

        val newPreview = CameraPreview(
            context = context,
            cameraWrapper = cameraWrapper,
            previewCallback = this
        )
        preview = newPreview

        newPreview.setAspectTolerance(aspectTolerance = storedAspectTolerance)
        newPreview.setShouldScaleToFill(scaleToFill = scaleToFillState)

        if (!scaleToFillState) {
            val relativeLayout = RelativeLayout(context)
            relativeLayout.gravity = Gravity.CENTER
            relativeLayout.setBackgroundColor(Color.BLACK)
            relativeLayout.addView(newPreview)
            addView(relativeLayout)
        } else {
            addView(newPreview)
        }

        val currentViewFinderView = activeViewFinderView
        if (currentViewFinderView is View) {
            addView(currentViewFinderView)
        } else {
            throw IllegalArgumentException(
                "IViewFinder object returned by " +
                        "'createViewFinderView()' should be instance of android.view.View",
            )
        }
    }

    /**
     * <p>Method that creates view that represents visual appearance of a barcode scanner</p>
     * <p>Override it to provide your own view for visual appearance of a barcode scanner</p>
     *
     * @param context {@link Context}
     * @return {@link android.view.View} that implements {@link ViewFinderView}
     */
    protected open fun createViewFinderView(context: Context): ViewFinder {
        val newViewFinderView = ViewFinderView(context = context)
        newViewFinderView.setBorderColor(borderColor = storedBorderColor)
        newViewFinderView.setLaserColor(laserColor = storedLaserColor)
        newViewFinderView.setLaserEnabled(isEnabled = laserEnabledState)
        newViewFinderView.setBorderStrokeWidth(borderStrokeWidth = storedBorderWidth)
        newViewFinderView.setBorderLineLength(borderLineLength = storedBorderLength)
        newViewFinderView.setMaskColor(maskColor = storedMaskColor)

        newViewFinderView.setBorderCornerRounded(isBorderCornersRounded = borderCornersRounded)
        newViewFinderView.setBorderCornerRadius(borderCornersRadius = storedCornerRadius)
        newViewFinderView.setSquareViewFinder(isSquareViewFinder = squareFinder)
        newViewFinderView.setViewFinderOffset(offset = storedViewFinderOffset)
        return newViewFinderView
    }

    open fun setLaserColor(laserColor: Int) {
        storedLaserColor = laserColor
        activeViewFinderView.setLaserColor(laserColor = storedLaserColor)
        activeViewFinderView.setupViewFinder()
    }

    open fun setMaskColor(maskColor: Int) {
        storedMaskColor = maskColor
        activeViewFinderView.setMaskColor(maskColor = storedMaskColor)
        activeViewFinderView.setupViewFinder()
    }

    open fun setBorderColor(borderColor: Int) {
        storedBorderColor = borderColor
        activeViewFinderView.setBorderColor(borderColor = storedBorderColor)
        activeViewFinderView.setupViewFinder()
    }

    open fun setBorderStrokeWidth(borderStrokeWidth: Int) {
        storedBorderWidth = borderStrokeWidth
        activeViewFinderView.setBorderStrokeWidth(borderStrokeWidth = storedBorderWidth)
        activeViewFinderView.setupViewFinder()
    }

    open fun setBorderLineLength(borderLineLength: Int) {
        storedBorderLength = borderLineLength
        activeViewFinderView.setBorderLineLength(borderLineLength = storedBorderLength)
        activeViewFinderView.setupViewFinder()
    }

    open fun setLaserEnabled(isLaserEnabled: Boolean) {
        laserEnabledState = isLaserEnabled
        activeViewFinderView.setLaserEnabled(isEnabled = laserEnabledState)
        activeViewFinderView.setupViewFinder()
    }

    open fun setIsBorderCornerRounded(isBorderCornerRounded: Boolean) {
        borderCornersRounded = isBorderCornerRounded
        activeViewFinderView.setBorderCornerRounded(isBorderCornersRounded = borderCornersRounded)
        activeViewFinderView.setupViewFinder()
    }

    open fun setBorderCornerRadius(borderCornerRadius: Int) {
        storedCornerRadius = borderCornerRadius
        activeViewFinderView.setBorderCornerRadius(borderCornersRadius = storedCornerRadius)
        activeViewFinderView.setupViewFinder()
    }

    open fun setSquareViewFinder(isSquareViewFinder: Boolean) {
        squareFinder = isSquareViewFinder
        activeViewFinderView.setSquareViewFinder(isSquareViewFinder = squareFinder)
        activeViewFinderView.setupViewFinder()
    }

    open fun setBorderAlpha(borderAlpha: Float) {
        storedBorderAlpha = borderAlpha
        activeViewFinderView.setBorderAlpha(alpha = storedBorderAlpha)
        activeViewFinderView.setupViewFinder()
    }

    open fun startCamera(cameraId: Int) {
        if (cameraHandlerThread == null) {
            cameraHandlerThread = CameraHandlerThread(scannerView = this)
        }

        val currentCameraHandlerThread = cameraHandlerThread ?: throw NullPointerException()

        currentCameraHandlerThread.startCamera(cameraId = cameraId)
    }

    open fun setupCameraPreview(cameraWrapper: CameraWrapper?) {
        this.cameraWrapper = cameraWrapper

        if (this.cameraWrapper != null) {
            setupLayout(cameraWrapper = activeCameraWrapper)
            activeViewFinderView.setupViewFinder()

            val currentFlashState = flashState
            if (currentFlashState != null) {
                setFlash(isEnabled = currentFlashState)
            }

            setAutoFocus(autoFocusState)
        }
    }

    open fun startCamera() {
        startCamera(cameraId = CameraUtils.getDefaultCameraId())
    }

    open fun stopCamera() {
        val currentCameraWrapper = cameraWrapper

        if (currentCameraWrapper != null) {
            activePreview.stopCameraPreview()
            activePreview.setCamera(
                cameraWrapper = null,
                previewCallback = null
            )
            currentCameraWrapper.camera.release()
            cameraWrapper = null
        }

        val currentCameraHandlerThread = cameraHandlerThread
        if (currentCameraHandlerThread != null) {
            currentCameraHandlerThread.quit()
            cameraHandlerThread = null
        }
    }

    open fun stopCameraPreview() {
        preview?.stopCameraPreview()
    }

    protected open fun resumeCameraPreview() {
        preview?.showCameraPreview()
    }

    @Synchronized
    open fun getFramingRectInPreview(
        previewWidth: Int,
        previewHeight: Int
    ): Rect? {
        if (framingRectInPreview == null) {
            val currentViewFinderView = activeViewFinderView
            val framingRect = currentViewFinderView.getFramingRect()
            val viewFinderViewWidth = currentViewFinderView.getWidth()
            val viewFinderViewHeight = currentViewFinderView.getHeight()

            if (framingRect == null || viewFinderViewWidth == 0 || viewFinderViewHeight == 0) {
                return null
            }

            val rect = Rect(framingRect)

            if (previewWidth < viewFinderViewWidth) {
                rect.left = rect.left * previewWidth / viewFinderViewWidth
                rect.right = rect.right * previewWidth / viewFinderViewWidth
            }

            if (previewHeight < viewFinderViewHeight) {
                rect.top = rect.top * previewHeight / viewFinderViewHeight
                rect.bottom = rect.bottom * previewHeight / viewFinderViewHeight
            }

            framingRectInPreview = rect
        }

        return framingRectInPreview
    }

    open fun setFlash(isEnabled: Boolean) {
        flashState = isEnabled

        val currentCameraWrapper = cameraWrapper
        if (
            currentCameraWrapper != null &&
            CameraUtils.isFlashSupported(camera = currentCameraWrapper.camera)
        ) {
            val parameters = currentCameraWrapper.camera.parameters

            if (isEnabled) {
                val flashMode = parameters.flashMode ?: throw NullPointerException()

                if (flashMode == Camera.Parameters.FLASH_MODE_TORCH) return

                parameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
            } else {
                val flashMode = parameters.flashMode ?: throw NullPointerException()

                if (flashMode == Camera.Parameters.FLASH_MODE_OFF) return

                parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
            }

            currentCameraWrapper.camera.parameters = parameters
        }
    }

    open fun getFlash(): Boolean {
        val currentCameraWrapper = cameraWrapper

        if (
            currentCameraWrapper != null &&
            CameraUtils.isFlashSupported(camera = currentCameraWrapper.camera)
        ) {
            val parameters = currentCameraWrapper.camera.parameters
            val flashMode = parameters.flashMode ?: throw NullPointerException()

            return flashMode == Camera.Parameters.FLASH_MODE_TORCH
        }

        return false
    }

    open fun toggleFlash() {
        val currentCameraWrapper = cameraWrapper

        if (
            currentCameraWrapper != null &&
            CameraUtils.isFlashSupported(camera = currentCameraWrapper.camera)
        ) {
            val parameters = currentCameraWrapper.camera.parameters
            val flashMode = parameters.flashMode ?: throw NullPointerException()

            if (flashMode == Camera.Parameters.FLASH_MODE_TORCH) {
                parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
            } else {
                parameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
            }

            currentCameraWrapper.camera.parameters = parameters
        }
    }

    open fun setAutoFocus(isEnabled: Boolean) {
        autoFocusState = isEnabled
        preview?.setAutoFocus(isEnabled = isEnabled)
    }

    open fun setShouldScaleToFill(shouldScaleToFill: Boolean) {
        scaleToFillState = shouldScaleToFill
    }

    open fun setAspectTolerance(aspectTolerance: Float) {
        storedAspectTolerance = aspectTolerance
    }

    open fun getRotatedData(
        data: ByteArray?,
        camera: Camera?,
    ): ByteArray? {
        val activeCamera = camera ?: throw NullPointerException()
        val parameters = activeCamera.parameters
        val size = parameters.previewSize
        var width = size.width
        var height = size.height
        var previewData = data

        val rotations = rotationCount

        if (rotations == 1 || rotations == 3) {
            for (rotationIndex in 0 until rotations) {
                val currentData = previewData ?: throw NullPointerException()

                val rotatedData = ByteArray(currentData.size)

                for (row in 0 until height) {
                    for (column in 0 until width) {
                        rotatedData[
                            column * height + height - row - 1
                        ] = currentData[column + row * width]
                    }
                }

                previewData = rotatedData

                val previousWidth = width
                width = height
                height = previousWidth
            }
        }

        return previewData
    }
}
