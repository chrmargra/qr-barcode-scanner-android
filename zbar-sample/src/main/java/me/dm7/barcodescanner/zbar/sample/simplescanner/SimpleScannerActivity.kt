package me.dm7.barcodescanner.zbar.sample.simplescanner

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ResultHandler
import me.dm7.barcodescanner.zbar.ZBarScannerView
import me.dm7.barcodescanner.zbar.sample.base.BaseScannerActivity
import me.dm7.barcodescanner.zbar.sample.databinding.ActivitySimpleScannerBinding

private const val DELAY = 2000L

class SimpleScannerActivity : BaseScannerActivity(), ResultHandler {

    private var scannerView: ZBarScannerView? = null

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        val binding = ActivitySimpleScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.toolbar)

        scannerView = ZBarScannerView(this)
        binding.contentFrame.addView(scannerView)
    }

    override fun onResume() {
        super.onResume()
        scannerView?.setResultHandler(this)
        scannerView?.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scannerView?.stopCamera()
    }

    override fun handleResult(rawResult: Result) {
        Toast.makeText(
            this,
            "Contents = ${rawResult.contents}, Format = ${rawResult.barcodeFormat.name}",
            Toast.LENGTH_SHORT
        ).show()

        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler(Looper.getMainLooper()).postDelayed(
            {
                scannerView?.resumeCameraPreview(this)
            },
            DELAY
        )
    }
}
