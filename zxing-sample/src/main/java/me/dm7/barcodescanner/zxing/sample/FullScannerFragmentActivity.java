package me.dm7.barcodescanner.zxing.sample;

import android.os.Bundle;

import me.dm7.barcodescanner.zxing.sample.databinding.ActivityFullScannerFragmentBinding;

public class FullScannerFragmentActivity extends BaseScannerActivity {
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        ActivityFullScannerFragmentBinding binding = ActivityFullScannerFragmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(binding.toolbar);
    }
}
