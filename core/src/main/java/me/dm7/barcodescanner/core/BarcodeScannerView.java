package me.dm7.barcodescanner.core;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;

import androidx.annotation.ColorInt;

import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public abstract class BarcodeScannerView extends FrameLayout implements Camera.PreviewCallback {

    private CameraWrapper cameraWrapper;
    private CameraPreview preview;
    private ViewFinder viewFinderView;
    private Rect framingRectInPreview;
    private CameraHandlerThread cameraHandlerThread;
    private Boolean flashState;
    private boolean autofocusState = true;
    private boolean shouldScaleToFill = true;

    private boolean isLaserEnabled = true;
    @ColorInt
    private int laserColor = getResources().getColor(R.color.viewfinder_laser);
    @ColorInt
    private int borderColor = getResources().getColor(R.color.viewfinder_border);
    private int maskColor = getResources().getColor(R.color.viewfinder_mask);
    private int borderWidth = getResources().getInteger(R.integer.viewfinder_border_width);
    private int borderLength = getResources().getInteger(R.integer.viewfinder_border_length);
    private boolean roundedCorner = false;
    private int cornerRadius = 0;
    private boolean squaredFinder = false;
    private float borderAlpha = 1.0f;
    private int viewFinderOffset = 0;
    private float aspectTolerance = 0.1f;

    public BarcodeScannerView(Context context) {
        super(context);
        init();
    }

    public BarcodeScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.BarcodeScannerView,
                0, 0);

        try {
            setShouldScaleToFill(a.getBoolean(R.styleable.BarcodeScannerView_shouldScaleToFill, true));
            isLaserEnabled = a.getBoolean(R.styleable.BarcodeScannerView_laserEnabled, isLaserEnabled);
            laserColor = a.getColor(R.styleable.BarcodeScannerView_laserColor, laserColor);
            borderColor = a.getColor(R.styleable.BarcodeScannerView_borderColor, borderColor);
            maskColor = a.getColor(R.styleable.BarcodeScannerView_maskColor, maskColor);
            borderWidth = a.getDimensionPixelSize(R.styleable.BarcodeScannerView_borderWidth, borderWidth);
            borderLength = a.getDimensionPixelSize(R.styleable.BarcodeScannerView_borderLength, borderLength);

            roundedCorner = a.getBoolean(R.styleable.BarcodeScannerView_roundedCorner, roundedCorner);
            cornerRadius = a.getDimensionPixelSize(R.styleable.BarcodeScannerView_cornerRadius, cornerRadius);
            squaredFinder = a.getBoolean(R.styleable.BarcodeScannerView_squaredFinder, squaredFinder);
            borderAlpha = a.getFloat(R.styleable.BarcodeScannerView_borderAlpha, borderAlpha);
            viewFinderOffset = a.getDimensionPixelSize(R.styleable.BarcodeScannerView_finderOffset, viewFinderOffset);
        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        viewFinderView = createViewFinderView(getContext());
    }

    public final void setupLayout(CameraWrapper cameraWrapper) {
        removeAllViews();

        preview = new CameraPreview(getContext(), cameraWrapper, this);
        preview.setAspectTolerance(aspectTolerance);
        preview.setShouldScaleToFill(shouldScaleToFill);
        if (!shouldScaleToFill) {
            RelativeLayout relativeLayout = new RelativeLayout(getContext());
            relativeLayout.setGravity(Gravity.CENTER);
            relativeLayout.setBackgroundColor(Color.BLACK);
            relativeLayout.addView(preview);
            addView(relativeLayout);
        } else {
            addView(preview);
        }

        if (viewFinderView instanceof View) {
            addView((View) viewFinderView);
        } else {
            throw new IllegalArgumentException("IViewFinder object returned by " +
                    "'createViewFinderView()' should be instance of android.view.View");
        }
    }

    /**
     * <p>Method that creates view that represents visual appearance of a barcode scanner</p>
     * <p>Override it to provide your own view for visual appearance of a barcode scanner</p>
     *
     * @param context {@link Context}
     * @return {@link android.view.View} that implements {@link ViewFinderView}
     */
    protected ViewFinder createViewFinderView(Context context) {
        ViewFinderView viewFinderView = new ViewFinderView(context);
        viewFinderView.setBorderColor(borderColor);
        viewFinderView.setLaserColor(laserColor);
        viewFinderView.setLaserEnabled(isLaserEnabled);
        viewFinderView.setBorderStrokeWidth(borderWidth);
        viewFinderView.setBorderLineLength(borderLength);
        viewFinderView.setMaskColor(maskColor);

        viewFinderView.setBorderCornerRounded(roundedCorner);
        viewFinderView.setBorderCornerRadius(cornerRadius);
        viewFinderView.setSquareViewFinder(squaredFinder);
        viewFinderView.setViewFinderOffset(viewFinderOffset);
        return viewFinderView;
    }

    public void setLaserColor(int laserColor) {
        this.laserColor = laserColor;
        viewFinderView.setLaserColor(this.laserColor);
        viewFinderView.setupViewFinder();
    }

    public void setMaskColor(int maskColor) {
        this.maskColor = maskColor;
        viewFinderView.setMaskColor(this.maskColor);
        viewFinderView.setupViewFinder();
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        viewFinderView.setBorderColor(this.borderColor);
        viewFinderView.setupViewFinder();
    }

    public void setBorderStrokeWidth(int borderStrokeWidth) {
        borderWidth = borderStrokeWidth;
        viewFinderView.setBorderStrokeWidth(borderWidth);
        viewFinderView.setupViewFinder();
    }

    public void setBorderLineLength(int borderLineLength) {
        borderLength = borderLineLength;
        viewFinderView.setBorderLineLength(borderLength);
        viewFinderView.setupViewFinder();
    }

    public void setLaserEnabled(boolean isLaserEnabled) {
        this.isLaserEnabled = isLaserEnabled;
        viewFinderView.setLaserEnabled(this.isLaserEnabled);
        viewFinderView.setupViewFinder();
    }

    public void setIsBorderCornerRounded(boolean isBorderCornerRounded) {
        roundedCorner = isBorderCornerRounded;
        viewFinderView.setBorderCornerRounded(roundedCorner);
        viewFinderView.setupViewFinder();
    }

    public void setBorderCornerRadius(int borderCornerRadius) {
        cornerRadius = borderCornerRadius;
        viewFinderView.setBorderCornerRadius(cornerRadius);
        viewFinderView.setupViewFinder();
    }

    public void setSquareViewFinder(boolean isSquareViewFinder) {
        squaredFinder = isSquareViewFinder;
        viewFinderView.setSquareViewFinder(squaredFinder);
        viewFinderView.setupViewFinder();
    }

    public void setBorderAlpha(float borderAlpha) {
        this.borderAlpha = borderAlpha;
        viewFinderView.setBorderAlpha(this.borderAlpha);
        viewFinderView.setupViewFinder();
    }

    public void startCamera(int cameraId) {
        if (cameraHandlerThread == null) {
            cameraHandlerThread = new CameraHandlerThread(this);
        }
        cameraHandlerThread.startCamera(cameraId);
    }

    public void setupCameraPreview(CameraWrapper cameraWrapper) {
        this.cameraWrapper = cameraWrapper;
        if (this.cameraWrapper != null) {
            setupLayout(this.cameraWrapper);
            viewFinderView.setupViewFinder();
            if (flashState != null) {
                setFlash(flashState);
            }
            setAutoFocus(autofocusState);
        }
    }

    public void startCamera() {
        startCamera(CameraUtils.getDefaultCameraId());
    }

    public void stopCamera() {
        if (cameraWrapper != null) {
            preview.stopCameraPreview();
            preview.setCamera(null, null);
            cameraWrapper.camera.release();
            cameraWrapper = null;
        }
        if (cameraHandlerThread != null) {
            cameraHandlerThread.quit();
            cameraHandlerThread = null;
        }
    }

    public void stopCameraPreview() {
        if (preview != null) {
            preview.stopCameraPreview();
        }
    }

    protected void resumeCameraPreview() {
        if (preview != null) {
            preview.showCameraPreview();
        }
    }

    public synchronized Rect getFramingRectInPreview(int previewWidth, int previewHeight) {
        if (framingRectInPreview == null) {
            Rect framingRect = viewFinderView.getFramingRect();
            int viewFinderViewWidth = viewFinderView.getWidth();
            int viewFinderViewHeight = viewFinderView.getHeight();
            if (framingRect == null || viewFinderViewWidth == 0 || viewFinderViewHeight == 0) {
                return null;
            }

            Rect rect = new Rect(framingRect);

            if (previewWidth < viewFinderViewWidth) {
                rect.left = rect.left * previewWidth / viewFinderViewWidth;
                rect.right = rect.right * previewWidth / viewFinderViewWidth;
            }

            if (previewHeight < viewFinderViewHeight) {
                rect.top = rect.top * previewHeight / viewFinderViewHeight;
                rect.bottom = rect.bottom * previewHeight / viewFinderViewHeight;
            }

            framingRectInPreview = rect;
        }
        return framingRectInPreview;
    }

    public void setFlash(boolean flag) {
        flashState = flag;
        if (cameraWrapper != null && CameraUtils.isFlashSupported(cameraWrapper.camera)) {

            Camera.Parameters parameters = cameraWrapper.camera.getParameters();
            if (flag) {
                if (parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                    return;
                }
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            } else {
                if (parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF)) {
                    return;
                }
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
            cameraWrapper.camera.setParameters(parameters);
        }
    }

    public boolean getFlash() {
        if (cameraWrapper != null && CameraUtils.isFlashSupported(cameraWrapper.camera)) {
            Camera.Parameters parameters = cameraWrapper.camera.getParameters();
            if (parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void toggleFlash() {
        if (cameraWrapper != null && CameraUtils.isFlashSupported(cameraWrapper.camera)) {
            Camera.Parameters parameters = cameraWrapper.camera.getParameters();
            if (parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            } else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            }
            cameraWrapper.camera.setParameters(parameters);
        }
    }

    public void setAutoFocus(boolean state) {
        autofocusState = state;
        if (preview != null) {
            preview.setAutoFocus(state);
        }
    }

    public void setShouldScaleToFill(boolean shouldScaleToFill) {
        this.shouldScaleToFill = shouldScaleToFill;
    }

    public void setAspectTolerance(float aspectTolerance) {
        this.aspectTolerance = aspectTolerance;
    }

    public byte[] getRotatedData(byte[] data, Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        int width = size.width;
        int height = size.height;

        int rotationCount = getRotationCount();

        if (rotationCount == 1 || rotationCount == 3) {
            for (int i = 0; i < rotationCount; i++) {
                byte[] rotatedData = new byte[data.length];
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++)
                        rotatedData[x * height + height - y - 1] = data[x + y * width];
                }
                data = rotatedData;
                int tmp = width;
                width = height;
                height = tmp;
            }
        }

        return data;
    }

    public int getRotationCount() {
        int displayOrientation = preview.getDisplayOrientation();
        return displayOrientation / 90;
    }
}
