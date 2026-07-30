package me.dm7.barcodescanner.core.util

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.view.WindowManager

object DisplayUtils {

    fun getScreenResolution(context: Context): Point {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        val display = windowManager?.defaultDisplay
        val screenResolution = Point()
        display?.getSize(screenResolution)

        return screenResolution
    }

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
