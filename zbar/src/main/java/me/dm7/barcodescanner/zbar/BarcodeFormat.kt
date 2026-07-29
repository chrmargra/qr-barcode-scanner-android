package me.dm7.barcodescanner.zbar

import net.sourceforge.zbar.Symbol

open class BarcodeFormat(
    open val id: Int,
    open val name: String?
) {

    companion object {
        val NONE = BarcodeFormat(Symbol.NONE, "NONE")

        val PARTIAL = BarcodeFormat(Symbol.PARTIAL, "PARTIAL")

        val EAN8 = BarcodeFormat(Symbol.EAN8, "EAN8")

        val UPCE = BarcodeFormat(Symbol.UPCE, "UPCE")

        val ISBN10 = BarcodeFormat(Symbol.ISBN10, "ISBN10")

        val UPCA = BarcodeFormat(Symbol.UPCA, "UPCA")

        val EAN13 = BarcodeFormat(Symbol.EAN13, "EAN13")

        val ISBN13 = BarcodeFormat(Symbol.ISBN13, "ISBN13")

        val I25 = BarcodeFormat(Symbol.I25, "I25")

        val DATABAR = BarcodeFormat(Symbol.DATABAR, "DATABAR")

        val DATABAR_EXP = BarcodeFormat(Symbol.DATABAR_EXP, "DATABAR_EXP")

        val CODABAR = BarcodeFormat(Symbol.CODABAR, "CODABAR")

        val CODE39 = BarcodeFormat(Symbol.CODE39, "CODE39")

        val PDF417 = BarcodeFormat(Symbol.PDF417, "PDF417")

        val QRCODE = BarcodeFormat(Symbol.QRCODE, "QRCODE")

        val CODE93 = BarcodeFormat(Symbol.CODE93, "CODE93")

        val CODE128 = BarcodeFormat(Symbol.CODE128, "CODE128")

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
