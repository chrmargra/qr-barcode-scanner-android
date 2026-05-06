package me.dm7.barcodescanner.zbar.sample

import android.os.Bundle
import me.dm7.barcodescanner.zbar.sample.databinding.ActivitySimpleScannerFragmentBinding

class SimpleScannerFragmentActivity : BaseScannerActivity() {

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        val binding = ActivitySimpleScannerFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.toolbar)
    }
}
