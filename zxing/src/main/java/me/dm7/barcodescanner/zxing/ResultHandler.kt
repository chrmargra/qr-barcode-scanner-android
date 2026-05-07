package me.dm7.barcodescanner.zxing

import com.google.zxing.Result

interface ResultHandler {
    fun handleResult(rawResult: Result)
}
