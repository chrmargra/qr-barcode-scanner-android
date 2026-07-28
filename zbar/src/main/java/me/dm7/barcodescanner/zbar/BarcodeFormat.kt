package me.dm7.barcodescanner.zbar

import net.sourceforge.zbar.Symbol

open class BarcodeFormat(
    open val id: Int,
    open val name: String?
) {

    companion object {

        @JvmField
        val NONE = BarcodeFormat(Symbol.NONE, "NONE")

        @JvmField
        val PARTIAL = BarcodeFormat(Symbol.PARTIAL, "PARTIAL")

        @JvmField
        val EAN8 = BarcodeFormat(Symbol.EAN8, "EAN8")

        @JvmField
        val UPCE = BarcodeFormat(Symbol.UPCE, "UPCE")

        @JvmField
        val ISBN10 = BarcodeFormat(Symbol.ISBN10, "ISBN10")

        @JvmField
        val UPCA = BarcodeFormat(Symbol.UPCA, "UPCA")

        @JvmField
        val EAN13 = BarcodeFormat(Symbol.EAN13, "EAN13")

        @JvmField
        val ISBN13 = BarcodeFormat(Symbol.ISBN13, "ISBN13")

        @JvmField
        val I25 = BarcodeFormat(Symbol.I25, "I25")

        @JvmField
        val DATABAR = BarcodeFormat(Symbol.DATABAR, "DATABAR")

        @JvmField
        val DATABAR_EXP = BarcodeFormat(Symbol.DATABAR_EXP, "DATABAR_EXP")

        @JvmField
        val CODABAR = BarcodeFormat(Symbol.CODABAR, "CODABAR")

        @JvmField
        val CODE39 = BarcodeFormat(Symbol.CODE39, "CODE39")

        @JvmField
        val PDF417 = BarcodeFormat(Symbol.PDF417, "PDF417")

        @JvmField
        val QRCODE = BarcodeFormat(Symbol.QRCODE, "QRCODE")

        @JvmField
        val CODE93 = BarcodeFormat(Symbol.CODE93, "CODE93")

        @JvmField
        val CODE128 = BarcodeFormat(Symbol.CODE128, "CODE128")

        @JvmField
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

        @JvmStatic
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
