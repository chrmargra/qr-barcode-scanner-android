package me.dm7.barcodescanner.zxing.sample.customviewfinder

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.zxing.Result
import me.dm7.barcodescanner.core.ViewFinder
import me.dm7.barcodescanner.zxing.ResultHandler
import me.dm7.barcodescanner.zxing.ZXingScannerView
import me.dm7.barcodescanner.zxing.sample.base.BaseScannerActivity
import me.dm7.barcodescanner.zxing.sample.databinding.ActivityCustomViewFinderScannerBinding

private const val DELAY = 2000L

class CustomViewFinderScannerActivity : BaseScannerActivity(), ResultHandler {

    private var scannerView: ZXingScannerView? = null

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        val binding = ActivityCustomViewFinderScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(toolbar = binding.toolbar)

        val newScannerView = object : ZXingScannerView(this) {
            override fun createViewFinderView(context: Context): ViewFinder =
                CustomViewFinderView(context)
        }

        scannerView = newScannerView
        binding.contentFrame.addView(newScannerView)
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
            "Contents = ${rawResult.text}, Format = ${rawResult.barcodeFormat}",
            Toast.LENGTH_SHORT
        ).show()

        // Note:
        // Wait 2 seconds to resume the preview.
        // On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // I don't know why this is the case but I don't have the time to figure out.
        Handler(Looper.getMainLooper()).postDelayed(
            {
                scannerView?.resumeCameraPreview(this)
            },
            DELAY
        )
    }
}
