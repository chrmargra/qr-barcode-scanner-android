package me.dm7.barcodescanner.zxing.encoder

import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

private const val DEFAULT_QR_CODE_WIDTH = 500

object QRCodeEncoder {

    fun encodeQRCode(
        value: String,
        resolution: Int = DEFAULT_QR_CODE_WIDTH,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE
    ): Bitmap? {
        val bitMatrix: BitMatrix
        try {
            bitMatrix = MultiFormatWriter().encode(
                value,
                BarcodeFormat.QR_CODE,
                resolution,
                resolution,
                null,
            )
        } catch (_: IllegalArgumentException) {
            return null
        }

        val bitMatrixWidth = bitMatrix.width
        val bitMatrixHeight = bitMatrix.height
        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)

        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth
            for (x in 0 until bitMatrixWidth) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) {
                    foregroundColor
                } else {
                    backgroundColor
                }
            }
        }
        val bitmap = createBitmap(
            bitMatrixWidth,
            bitMatrixHeight,
            Bitmap.Config.RGB_565
        )

        bitmap.setPixels(
            pixels,
            0,
            bitMatrixWidth,
            0,
            0,
            bitMatrixWidth,
            bitMatrixHeight,
        )

        return bitmap
    }
}
