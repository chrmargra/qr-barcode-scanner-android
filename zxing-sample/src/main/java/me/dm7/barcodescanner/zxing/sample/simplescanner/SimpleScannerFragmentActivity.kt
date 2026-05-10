package me.dm7.barcodescanner.zxing.sample.simplescanner

import android.os.Bundle
import me.dm7.barcodescanner.zxing.sample.base.BaseScannerActivity
import me.dm7.barcodescanner.zxing.sample.databinding.ActivitySimpleScannerFragmentBinding

class SimpleScannerFragmentActivity : BaseScannerActivity() {

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        val binding = ActivitySimpleScannerFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(toolbar = binding.toolbar)
    }
}
