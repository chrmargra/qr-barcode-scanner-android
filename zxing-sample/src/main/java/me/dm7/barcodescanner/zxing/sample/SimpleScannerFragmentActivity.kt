package me.dm7.barcodescanner.zxing.sample;

import android.os.Bundle;

import me.dm7.barcodescanner.zxing.sample.databinding.ActivitySimpleScannerFragmentBinding;

public class SimpleScannerFragmentActivity extends BaseScannerActivity {
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        ActivitySimpleScannerFragmentBinding binding = ActivitySimpleScannerFragmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar(binding.toolbar);
    }
}
