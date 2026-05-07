package me.dm7.barcodescanner.zbar.sample.fullscanner

import android.os.Bundle
import me.dm7.barcodescanner.zbar.sample.base.BaseScannerActivity
import me.dm7.barcodescanner.zbar.sample.databinding.ActivityFullScannerFragmentBinding

class FullScannerFragmentActivity : BaseScannerActivity() {

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        val binding = ActivityFullScannerFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.toolbar)
    }
}
