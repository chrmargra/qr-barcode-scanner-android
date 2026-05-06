package me.dm7.barcodescanner.zxing.sample

import android.os.Bundle
import me.dm7.barcodescanner.zxing.sample.databinding.ActivityFullScannerFragmentBinding

class FullScannerFragmentActivity : BaseScannerActivity() {

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        val binding = ActivityFullScannerFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.toolbar)
    }
}
