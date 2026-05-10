package me.dm7.barcodescanner.zxing.sample.fullscanner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.dm7.barcodescanner.zxing.sample.databinding.ActivityFullScreenScannerFragmentBinding

class FullScreenScannerFragmentActivity : AppCompatActivity() {

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        val binding = ActivityFullScreenScannerFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
