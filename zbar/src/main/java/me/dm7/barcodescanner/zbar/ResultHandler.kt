package me.dm7.barcodescanner.zbar

interface ResultHandler {
    fun handleResult(rawResult: Result)
}
