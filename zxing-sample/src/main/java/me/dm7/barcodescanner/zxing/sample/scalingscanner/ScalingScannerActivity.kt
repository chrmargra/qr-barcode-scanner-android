package me.dm7.barcodescanner.zxing.sample.scalingscanner

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ResultHandler
import me.dm7.barcodescanner.zxing.ZXingScannerView
import me.dm7.barcodescanner.zxing.sample.base.BaseScannerActivity
import me.dm7.barcodescanner.zxing.sample.databinding.ActivityScalingScannerBinding

private const val FLASH_STATE = "FLASH_STATE"
private const val DELAY = 2000L

class ScalingScannerActivity : BaseScannerActivity(), ResultHandler {

    private var binding: ActivityScalingScannerBinding? = null
    private var scannerView: ZXingScannerView? = null
    private var flash = false

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        flash = state?.getBoolean(FLASH_STATE, false) ?: false

        binding = ActivityScalingScannerBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupToolbar(binding?.toolbar)
        initListeners()

        val newScannerView = ZXingScannerView(this)
        scannerView = newScannerView
        binding?.contentFrame?.addView(newScannerView)
    }

    private fun initListeners() {
        binding?.buttonFlash?.setOnClickListener {
            flash = !flash
            scannerView?.setFlash(flash)
        }
    }

    override fun onResume() {
        super.onResume()

        scannerView?.setResultHandler(this)

        // You can optionally set aspect ratio tolerance level
        // that is used in calculating the optimal Camera preview size
        scannerView?.setAspectTolerance(0.2f)

        scannerView?.startCamera()
        scannerView?.setFlash(flash)
    }

    override fun onPause() {
        super.onPause()

        scannerView?.stopCamera()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(FLASH_STATE, flash)
    }

    override fun handleResult(rawResult: Result) {
        Toast.makeText(
            this,
            "Contents = ${rawResult.text}, Format = ${rawResult.barcodeFormat}",
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
