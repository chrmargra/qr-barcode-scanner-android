package me.dm7.barcodescanner.core.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Checks whether the application has been granted the camera permission.
 *
 * This method only checks the current permission state and does not request
 * the permission. The consuming application is responsible for requesting
 * and managing the camera permission.
 *
 * @return `true` if the camera permission is granted, `false` otherwise.
 */
internal fun Context.hasCameraPermission(): Boolean =
    ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
