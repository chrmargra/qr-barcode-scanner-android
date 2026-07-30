package me.dm7.barcodescanner.core.logger

import android.util.Log
import me.dm7.barcodescanner.core.BuildConfig

private const val DEFAULT_TAG = "QRBarcodeScanner"

object QRBarcodeLogger {

    fun verbose(
        tag: String,
        message: String = DEFAULT_TAG
    ) {
        if (BuildConfig.DEBUG) Log.v(tag, message)
    }

    fun error(
        tag: String = DEFAULT_TAG,
        message: String,
        throwable: Throwable
    ) {
        if (BuildConfig.DEBUG) Log.e(tag, message, throwable)
    }
}
