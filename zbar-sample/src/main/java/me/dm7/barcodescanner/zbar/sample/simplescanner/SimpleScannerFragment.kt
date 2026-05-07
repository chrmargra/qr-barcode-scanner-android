package me.dm7.barcodescanner.zbar.sample.simplescanner

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView

private const val DIALOG_DELAY = 2000L

class SimpleScannerFragment : Fragment(), ZBarScannerView.ResultHandler {

    private var scannerView: ZBarScannerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val newScannerView = ZBarScannerView(requireActivity())
        scannerView = newScannerView
        return newScannerView
    }

    override fun onResume() {
        super.onResume()

        scannerView?.setResultHandler(this)
        scannerView?.startCamera()
    }

    override fun handleResult(rawResult: Result) {
        Toast.makeText(
            requireActivity(),
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
            DIALOG_DELAY
        )
    }

    override fun onPause() {
        super.onPause()

        scannerView?.stopCamera()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        scannerView = null
    }
}
