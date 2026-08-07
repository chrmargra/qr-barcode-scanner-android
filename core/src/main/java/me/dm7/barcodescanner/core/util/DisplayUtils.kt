package me.dm7.barcodescanner.core.util

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.view.WindowManager

/**
 * Utility functions for obtaining display dimensions and orientation.
 */
object DisplayUtils {

    /**
     * Returns the current display size in pixels.
     *
     * @return A point containing the display width and height, or `(0, 0)` if the
     * window service is unavailable.
     */
    fun getScreenResolution(context: Context): Point {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        val display = windowManager?.defaultDisplay
        val screenResolution = Point()
        display?.getSize(screenResolution)

        return screenResolution
    }

    /**
     * Determines the current screen orientation from the display dimensions.
     *
     * @return Portrait, landscape or square orientation, or
     * [Configuration.ORIENTATION_UNDEFINED] if the window service is unavailable.
     */
    fun getScreenOrientation(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            ?: return Configuration.ORIENTATION_UNDEFINED

        val display = windowManager.defaultDisplay

        return if (display.width == display.height) {
            Configuration.ORIENTATION_SQUARE
        } else if (display.width < display.height) {
            Configuration.ORIENTATION_PORTRAIT
        } else {
            Configuration.ORIENTATION_LANDSCAPE
        }
    }
}
