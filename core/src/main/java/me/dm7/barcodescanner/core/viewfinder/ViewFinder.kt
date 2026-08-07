package me.dm7.barcodescanner.core.viewfinder

import android.graphics.Rect

/**
 * Defines the appearance and framing area of the scanner viewfinder.
 *
 * Implementations returned by
 * [me.dm7.barcodescanner.core.BarcodeScannerView.createViewFinderView] must also
 * be Android [android.view.View] instances.
 */
interface ViewFinder {

    /** Sets the ARGB color used by the scanner laser. */
    fun setLaserColor(laserColor: Int)

    /** Sets the ARGB color drawn outside the framing rectangle. */
    fun setMaskColor(maskColor: Int)

    /** Sets the ARGB color of the framing border. */
    fun setBorderColor(borderColor: Int)

    /** Sets the framing border stroke width in pixels. */
    fun setBorderStrokeWidth(borderStrokeWidth: Int)

    /** Sets the length of each framing border corner in pixels. */
    fun setBorderLineLength(borderLineLength: Int)

    /** Enables or disables the animated scanner laser. */
    fun setLaserEnabled(isEnabled: Boolean)

    /** Enables or disables rounded framing border corners. */
    fun setBorderCornerRounded(isBorderCornersRounded: Boolean)

    /**
     * Sets the framing border opacity.
     *
     * The expected range is `0.0f` to `1.0f`.
     */
    fun setBorderAlpha(alpha: Float)

    /** Sets the framing border corner radius in pixels. */
    fun setBorderCornerRadius(borderCornersRadius: Int)

    /**
     * Insets the framing rectangle by [offset] pixels on every side.
     *
     * Positive values reduce the usable scanning area.
     */
    fun setViewFinderOffset(offset: Int)

    /** Selects whether the framing rectangle should be square. */
    fun setSquareViewFinder(isSquareViewFinder: Boolean)

    /**
     * Recalculates the framing rectangle and refreshes the viewfinder.
     *
     * This method is called when the camera preview is initialized or viewfinder
     * settings change.
     */
    fun setupViewFinder()

    /**
     * Returns the area in which preview data should be decoded.
     *
     * Coordinates are expressed as absolute pixels relative to the viewfinder.
     *
     * @return The framing rectangle, or `null` before the view has been laid out.
     */
    fun getFramingRect(): Rect?

    /**
     * Width of a [android.view.View] that implements this interface.
     *
     * Note: this is already implemented in [android.view.View], so you don't need to override this
     * method and provide your own implementation.
     *
     * @return width of a view.
     */
    fun getWidth(): Int

    /**
     * Height of a [android.view.View] that implements this interface.
     *
     * Note: this is already implemented in [android.view.View], so you don't need to override this
     * method and provide your own implementation.
     *
     * @return height of a view.
     */
    fun getHeight(): Int
}
