package me.dm7.barcodescanner.zxing.encoder

import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

private const val DEFAULT_QR_CODE_RESOLUTION = 500
private const val MIN_QR_CODE_RESOLUTION = 1

object QRCodeEncoder {

    /**
     * Encodes [value] as a QR code and returns the generated image as a square [Bitmap].
     *
     * ZXing's default QR code configuration is used. The requested [resolution] is
     * supplied as both the width and height. The resulting bitmap uses
     * [Bitmap.Config.RGB_565], so alpha transparency is not preserved.
     *
     * @param value Text to encode. It must contain at least one character.
     * @param resolution Requested width and height in pixels. It must be greater than zero.
     * Defaults to 500 pixels.
     * @param foregroundColor Color used for the QR code modules.
     * @param backgroundColor Color used for the background and quiet zone.
     *
     * @return The generated QR code bitmap.
     *
     * @throws IllegalArgumentException if [value] is empty or [resolution] is not greater than zero.
     * @throws com.google.zxing.WriterException if ZXing cannot encode [value] as a QR code.
     */
    fun encodeQRCode(
        value: String,
        resolution: Int = DEFAULT_QR_CODE_RESOLUTION,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE
    ): Bitmap {
        require(value = value.isNotEmpty()) {
            "The value to encode cannot be empty."
        }
        require(value = resolution >= MIN_QR_CODE_RESOLUTION) {
            "The resolution must be greater than zero."
        }

        val bitMatrix = MultiFormatWriter().encode(
            value,
            BarcodeFormat.QR_CODE,
            resolution,
            resolution,
            null
        )

        val bitMatrixWidth = bitMatrix.width
        val bitMatrixHeight = bitMatrix.height
        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)

        for (row in 0 until bitMatrixHeight) {
            val rowOffset = row * bitMatrixWidth
            for (column in 0 until bitMatrixWidth) {
                pixels[rowOffset + column] = if (bitMatrix.get(column, row)) {
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
            bitMatrixHeight
        )

        return bitmap
    }
}
