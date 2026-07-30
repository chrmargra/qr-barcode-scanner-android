package me.dm7.barcodescanner.core.viewfinder

import android.graphics.Rect

interface ViewFinder {

    fun setLaserColor(laserColor: Int)

    fun setMaskColor(maskColor: Int)

    fun setBorderColor(borderColor: Int)

    fun setBorderStrokeWidth(borderStrokeWidth: Int)

    fun setBorderLineLength(borderLineLength: Int)

    fun setLaserEnabled(isEnabled: Boolean)

    fun setBorderCornerRounded(isBorderCornersRounded: Boolean)

    fun setBorderAlpha(alpha: Float)

    fun setBorderCornerRadius(borderCornersRadius: Int)

    fun setViewFinderOffset(offset: Int)

    fun setSquareViewFinder(isSquareViewFinder: Boolean)

    /**
     * Method that executes when Camera preview is starting.
     * It is recommended to update framing rect here and invalidate view after that.
     *
     * For example see: [ViewFinderView.setupViewFinder]
     */
    fun setupViewFinder()

    /**
     * Provides [Rect] that identifies the area where the barcode scanner can detect visual codes.
     *
     * Note: This rect is an area representation in absolute pixel values.
     * For example, if the View size is 1024x800, the framing rect might be 500x400.
     *
     * @return [Rect] that identifies the barcode scanner area.
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
