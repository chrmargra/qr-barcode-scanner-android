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
import me.dm7.barcodescanner.core.camera.CameraHandlerThread
import me.dm7.barcodescanner.core.camera.CameraPreview
import me.dm7.barcodescanner.core.camera.CameraUtils
import me.dm7.barcodescanner.core.camera.CameraWrapper
import me.dm7.barcodescanner.core.viewfinder.ViewFinder
import me.dm7.barcodescanner.core.viewfinder.ViewFinderView

private const val NINETY_DEGREES_ROTATION = 90
private const val ROTATION_COUNT_90_DEGREES = 1
private const val ROTATION_COUNT_270_DEGREES = 3

/**
 * Base camera scanner view shared by the concrete barcode scanner implementations.
 *
 * This class manages the legacy Android camera lifecycle, preview orientation,
 * viewfinder appearance, flash, autofocus and preview-frame rotation. Applications
 * should normally use a concrete scanner implementation rather than instantiate
 * this class directly.
 *
 * The camera permission must be granted at runtime before calling [startCamera].
 * Call [stopCamera] when the hosting activity or fragment is paused.
 */
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

    /**
     * Number of 90-degree rotations required to align camera data with the display.
     *
     * This property is only available after the camera preview has been created.
     */
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
                0
            )

        try {
            setShouldScaleToFill(
                attributes.getBoolean(
                    R.styleable.BarcodeScannerView_shouldScaleToFill,
                    true
                ),
            )

            laserEnabledState = attributes.getBoolean(
                R.styleable.BarcodeScannerView_laserEnabled,
                laserEnabledState
            )

            storedLaserColor = attributes.getColor(
                R.styleable.BarcodeScannerView_laserColor,
                storedLaserColor
            )

            storedBorderColor = attributes.getColor(
                R.styleable.BarcodeScannerView_borderColor,
                storedBorderColor
            )

            storedMaskColor = attributes.getColor(
                R.styleable.BarcodeScannerView_maskColor,
                storedMaskColor
            )

            storedBorderWidth = attributes.getDimensionPixelSize(
                R.styleable.BarcodeScannerView_borderWidth,
                storedBorderWidth
            )

            storedBorderLength = attributes.getDimensionPixelSize(
                R.styleable.BarcodeScannerView_borderLength,
                storedBorderLength
            )

            borderCornersRounded = attributes.getBoolean(
                R.styleable.BarcodeScannerView_roundedCorner,
                borderCornersRounded
            )

            storedCornerRadius = attributes.getDimensionPixelSize(
                R.styleable.BarcodeScannerView_cornerRadius,
                storedCornerRadius
            )

            squareFinder = attributes.getBoolean(
                R.styleable.BarcodeScannerView_squaredFinder,
                squareFinder
            )

            storedBorderAlpha = attributes.getFloat(
                R.styleable.BarcodeScannerView_borderAlpha,
                storedBorderAlpha
            )

            storedViewFinderOffset = attributes.getDimensionPixelSize(
                R.styleable.BarcodeScannerView_finderOffset,
                storedViewFinderOffset
            )
        } finally {
            attributes.recycle()
        }

        init()
    }

    private fun init() {
        viewFinderView = createViewFinderView(context = context)
    }

    /**
     * Rebuilds the camera preview and places the viewfinder overlay above it.
     *
     * This method is part of the scanner's camera infrastructure and should not
     * normally be called directly by applications.
     *
     * @throws IllegalArgumentException if [createViewFinderView] returns an object
     * that is not also an Android [View].
     */
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
     * Creates the viewfinder overlay displayed above the camera preview.
     *
     * Override this method to provide a custom viewfinder. The returned object must
     * implement [ViewFinder] and must also be an Android [View].
     *
     * @param context Context used to create the viewfinder.
     * @return The viewfinder displayed above the camera preview.
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
        newViewFinderView.setBorderAlpha(alpha = storedBorderAlpha)
        return newViewFinderView
    }

    /** Sets the ARGB color used by the animated scanner laser. */
    open fun setLaserColor(laserColor: Int) {
        storedLaserColor = laserColor
        activeViewFinderView.setLaserColor(laserColor = storedLaserColor)
        activeViewFinderView.setupViewFinder()
    }

    /** Sets the ARGB color drawn outside the scanner's framing rectangle. */
    open fun setMaskColor(maskColor: Int) {
        storedMaskColor = maskColor
        activeViewFinderView.setMaskColor(maskColor = storedMaskColor)
        activeViewFinderView.setupViewFinder()
    }

    /** Sets the ARGB color used to draw the framing rectangle border. */
    open fun setBorderColor(borderColor: Int) {
        storedBorderColor = borderColor
        activeViewFinderView.setBorderColor(borderColor = storedBorderColor)
        activeViewFinderView.setupViewFinder()
    }

    /** Sets the framing border stroke width in pixels. */
    open fun setBorderStrokeWidth(borderStrokeWidth: Int) {
        storedBorderWidth = borderStrokeWidth
        activeViewFinderView.setBorderStrokeWidth(borderStrokeWidth = storedBorderWidth)
        activeViewFinderView.setupViewFinder()
    }

    /** Sets the length of each framing border corner in pixels. */
    open fun setBorderLineLength(borderLineLength: Int) {
        storedBorderLength = borderLineLength
        activeViewFinderView.setBorderLineLength(borderLineLength = storedBorderLength)
        activeViewFinderView.setupViewFinder()
    }

    /** Enables or disables the animated scanner laser. */
    open fun setLaserEnabled(isLaserEnabled: Boolean) {
        laserEnabledState = isLaserEnabled
        activeViewFinderView.setLaserEnabled(isEnabled = laserEnabledState)
        activeViewFinderView.setupViewFinder()
    }

    /** Enables or disables rounded joins on the framing border corners. */
    open fun setIsBorderCornerRounded(isBorderCornerRounded: Boolean) {
        borderCornersRounded = isBorderCornerRounded
        activeViewFinderView.setBorderCornerRounded(isBorderCornersRounded = borderCornersRounded)
        activeViewFinderView.setupViewFinder()
    }

    /** Sets the framing border corner radius in pixels. */
    open fun setBorderCornerRadius(borderCornerRadius: Int) {
        storedCornerRadius = borderCornerRadius
        activeViewFinderView.setBorderCornerRadius(borderCornersRadius = storedCornerRadius)
        activeViewFinderView.setupViewFinder()
    }

    /** Selects whether the framing rectangle should use a square aspect ratio. */
    open fun setSquareViewFinder(isSquareViewFinder: Boolean) {
        squareFinder = isSquareViewFinder
        activeViewFinderView.setSquareViewFinder(isSquareViewFinder = squareFinder)
        activeViewFinderView.setupViewFinder()
    }

    /**
     * Sets the framing border opacity.
     *
     * The expected range is `0.0f` for fully transparent to `1.0f` for fully opaque.
     * Values are not validated by this method.
     */
    open fun setBorderAlpha(borderAlpha: Float) {
        storedBorderAlpha = borderAlpha
        activeViewFinderView.setBorderAlpha(alpha = storedBorderAlpha)
        activeViewFinderView.setupViewFinder()
    }

    /**
     * Opens the camera identified by [cameraId] on a background thread.
     *
     * The camera permission must already be granted. If the camera cannot be opened,
     * no preview is created.
     */
    open fun startCamera(cameraId: Int) {
        if (cameraHandlerThread == null) {
            cameraHandlerThread = CameraHandlerThread(scannerView = this)
        }

        val currentCameraHandlerThread = cameraHandlerThread ?: throw NullPointerException()

        currentCameraHandlerThread.startCamera(cameraId = cameraId)
    }

    /**
     * Receives an asynchronously opened camera and initializes its preview.
     *
     * Stored flash and autofocus settings are applied when the camera is available.
     * This method is camera infrastructure and should not normally be called directly.
     */
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

    /**
     * Opens the preferred camera on a background thread.
     *
     * A back-facing camera is preferred. If none exists, another available camera is
     * used. The camera permission must already be granted.
     */
    open fun startCamera() {
        startCamera(cameraId = CameraUtils.getDefaultCameraId())
    }

    /**
     * Stops the preview, releases the active camera and terminates the camera thread.
     *
     * Call this method when the hosting activity or fragment is paused.
     */
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

    /**
     * Stops the camera preview without releasing the camera.
     *
     * The preview can subsequently be restarted by the scanner implementation.
     */
    open fun stopCameraPreview() {
        preview?.stopCameraPreview()
    }

    /**
     * Restarts the camera preview using the currently opened camera.
     *
     * Concrete scanner implementations use this method when scanning resumes after
     * delivering a result.
     */
    protected open fun resumeCameraPreview() {
        preview?.showCameraPreview()
    }

    /**
     * Maps the viewfinder's framing rectangle into camera-preview coordinates.
     *
     * The calculated rectangle is cached for subsequent preview frames.
     *
     * @return The framing rectangle in preview pixels, or `null` if the viewfinder
     * has not been laid out or has no valid dimensions.
     */
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

    /**
     * Enables or disables torch mode.
     *
     * The requested state is remembered and applied when the camera becomes
     * available. No action is performed if the active camera does not support flash.
     */
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

    /**
     * Returns whether the active camera is currently using torch mode.
     *
     * @return `false` if no camera is active or flash is not supported.
     */
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

    /**
     * Toggles torch mode on the active camera.
     *
     * No action is performed if no camera is active or flash is not supported.
     */
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

    /**
     * Enables or disables autofocus.
     *
     * The requested state is remembered and applied when the camera preview becomes
     * available.
     */
    open fun setAutoFocus(isEnabled: Boolean) {
        autoFocusState = isEnabled
        preview?.setAutoFocus(isEnabled = isEnabled)
    }

    /**
     * Selects whether the camera preview should fill its parent.
     *
     * When enabled, part of the preview may be cropped to preserve its aspect ratio.
     * When disabled, the complete preview is centered with unused space shown in
     * black. Call this method before starting the camera.
     */
    open fun setShouldScaleToFill(shouldScaleToFill: Boolean) {
        scaleToFillState = shouldScaleToFill
    }

    /**
     * Sets the maximum aspect-ratio difference accepted when selecting a camera
     * preview size.
     *
     * If no supported size falls within this tolerance, the size with the closest
     * height is used. Call this method before starting the camera.
     */
    open fun setAspectTolerance(aspectTolerance: Float) {
        storedAspectTolerance = aspectTolerance
    }

    /**
     * Rotates raw camera preview data to match the current display orientation.
     *
     * This method is used by scanner implementations before decoding portrait camera
     * frames.
     *
     * @return The rotated preview data when [rotationCount] is `1` or `3`, the
     * original data for any other rotation count, or `null` when the supplied data
     * is `null` and no rotation is applied.
     *
     * @throws NullPointerException if [camera] is `null`, or if [data] is `null`
     * when rotation is required.
     */
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

        if (
            rotations == ROTATION_COUNT_90_DEGREES ||
            rotations == ROTATION_COUNT_270_DEGREES
        ) {
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
