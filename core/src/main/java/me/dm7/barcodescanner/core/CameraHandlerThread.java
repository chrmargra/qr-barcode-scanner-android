package me.dm7.barcodescanner.core;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

// This code is mostly based on the top answer here: http://stackoverflow.com/questions/18149964/best-use-of-handlerthread-over-other-similar-classes
public class CameraHandlerThread extends HandlerThread {

    private final BarcodeScannerView scannerView;

    public CameraHandlerThread(BarcodeScannerView scannerView) {
        super("CameraHandlerThread");
        this.scannerView = scannerView;
        start();
    }

    public void startCamera(final int cameraId) {
        Handler localHandler = new Handler(getLooper());
        localHandler.post(() -> {
            final Camera camera = CameraUtils.getCameraInstance(cameraId);
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> scannerView.setupCameraPreview(CameraWrapper.getWrapper(camera, cameraId)));
        });
    }
}
