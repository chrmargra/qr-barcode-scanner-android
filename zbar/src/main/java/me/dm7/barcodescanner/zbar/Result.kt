package me.dm7.barcodescanner.zbar

/**
 * Contains a barcode result decoded by [ZBarScannerView].
 *
 * The properties remain nullable because ZBar may report a detected symbol
 * without providing usable, non-empty barcode data.
 */
class Result {

    /**
     * Barcode contents decoded as UTF-8.
     *
     * Embedded null bytes are preserved as null characters.
     */
    var contents: String? = null

    /**
     * Detected barcode format.
     *
     * This is [BarcodeFormat.NONE] when ZBar returns an unregistered symbol type,
     * or `null` when no usable symbol data is available.
     */
    var barcodeFormat: BarcodeFormat? = null
}
