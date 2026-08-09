package me.dm7.barcodescanner.core.logger

import android.util.Log
import me.dm7.barcodescanner.core.BuildConfig

private const val DEFAULT_TAG = "QRBarcodeScanner"

/**
 * Logging utility used internally by the barcode scanner modules.
 *
 * Messages are written only when the core module is built in debug mode. All
 * logging methods are no-ops in release builds.
 */
object QRBarcodeLogger {

    /**
     * Writes a verbose message in debug builds.
     *
     * @param tag Android log tag. Defaults to the library logger name.
     * @param message Message to write.
     */
    fun verbose(
        tag: String = DEFAULT_TAG,
        message: String
    ) {
        if (BuildConfig.DEBUG) Log.v(tag, message)
    }

    /**
     * Writes an error and its associated throwable in debug builds.
     *
     * @param tag Android log tag. Defaults to the library logger name.
     * @param message Error message.
     * @param throwable Error associated with the message.
     */
    fun error(
        tag: String = DEFAULT_TAG,
        message: String,
        throwable: Throwable
    ) {
        if (BuildConfig.DEBUG) Log.e(tag, message, throwable)
    }
}
