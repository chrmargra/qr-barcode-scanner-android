package me.dm7.barcodescanner.zxing.sample;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import me.dm7.barcodescanner.zxing.sample.databinding.ActivityFullScreenScannerFragmentBinding;

public class FullScreenScannerFragmentActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        ActivityFullScreenScannerFragmentBinding binding = ActivityFullScreenScannerFragmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
