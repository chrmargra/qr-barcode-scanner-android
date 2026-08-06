package me.dm7.barcodescanner.zbar

import net.sourceforge.zbar.Symbol

/**
 * Describes a barcode format supported by the ZBar scanner.
 *
 * @property id Numeric ZBar [Symbol] identifier used to configure the scanner.
 * @property name Optional human-readable name of the barcode format.
 */
open class BarcodeFormat(
    open val id: Int,
    open val name: String?
) {

    companion object {

        /** Fallback format used when no matching ZBar symbol type is registered. */
        val NONE = BarcodeFormat(id = Symbol.NONE, name = "NONE")

        val PARTIAL = BarcodeFormat(id = Symbol.PARTIAL, name = "PARTIAL")

        val EAN8 = BarcodeFormat(id = Symbol.EAN8, name = "EAN8")

        val UPCE = BarcodeFormat(id = Symbol.UPCE, name = "UPCE")

        val ISBN10 = BarcodeFormat(id = Symbol.ISBN10, name = "ISBN10")

        val UPCA = BarcodeFormat(id = Symbol.UPCA, name = "UPCA")

        val EAN13 = BarcodeFormat(id = Symbol.EAN13, name = "EAN13")

        val ISBN13 = BarcodeFormat(id = Symbol.ISBN13, name = "ISBN13")

        val I25 = BarcodeFormat(id = Symbol.I25, name = "I25")

        val DATABAR = BarcodeFormat(id = Symbol.DATABAR, name = "DATABAR")

        val DATABAR_EXP = BarcodeFormat(id = Symbol.DATABAR_EXP, name = "DATABAR_EXP")

        val CODABAR = BarcodeFormat(id = Symbol.CODABAR, name = "CODABAR")

        val CODE39 = BarcodeFormat(id = Symbol.CODE39, name = "CODE39")

        val PDF417 = BarcodeFormat(id = Symbol.PDF417, name = "PDF417")

        val QRCODE = BarcodeFormat(id = Symbol.QRCODE, name = "QRCODE")

        val CODE93 = BarcodeFormat(id = Symbol.CODE93, name = "CODE93")

        val CODE128 = BarcodeFormat(id = Symbol.CODE128, name = "CODE128")

        /**
         * Barcode formats enabled by default in [ZBarScannerView].
         *
         * [NONE] is not included because it represents an unknown or unsupported format.
         */
        val ALL_FORMATS: MutableList<BarcodeFormat> = arrayListOf(
            PARTIAL,
            EAN8,
            UPCE,
            ISBN10,
            UPCA,
            EAN13,
            ISBN13,
            I25,
            DATABAR,
            DATABAR_EXP,
            CODABAR,
            CODE39,
            PDF417,
            QRCODE,
            CODE93,
            CODE128
        )

        /**
         * Returns the format from [ALL_FORMATS] matching the specified ZBar symbol [id].
         *
         * @return The matching format, or [NONE] if no registered format has that identifier.
         */
        fun getFormatById(id: Int): BarcodeFormat {
            for (format in ALL_FORMATS) {
                if (format.id == id) {
                    return format
                }
            }

            return NONE
        }
    }
}
