package me.dm7.barcodescanner.core;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "CameraPreview";
    private static final int ROTATION_DEGREES_0 = 0;
    private static final int ROTATION_DEGREES_90 = 90;
    private static final int ROTATION_DEGREES_180 = 180;
    private static final int ROTATION_DEGREES_270 = 270;
    private static final int FULL_ROTATION_DEGREES = 360;
    private static final int HALF_ROTATION_DEGREES = 180;
    private static final int NO_ROTATION_DEGREES = 0;
    private static final Long DELAY = 1000L;

    private CameraWrapper cameraWrapper;
    private Handler autoFocusHandler;
    private boolean previewing = true;
    private boolean autoFocus = true;
    private boolean surfaceCreated = false;
    private boolean shouldScaleToFill = true;
    private Camera.PreviewCallback previewCallback;
    private float aspectTolerance = 0.1f;
    private final Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (cameraWrapper != null && previewing && autoFocus && surfaceCreated) {
                safeAutoFocus();
            }
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = (success, camera) -> scheduleAutoFocus();

    public CameraPreview(Context context, CameraWrapper cameraWrapper, Camera.PreviewCallback previewCallback) {
        super(context);
        init(cameraWrapper, previewCallback);
    }

    public CameraPreview(Context context, AttributeSet attrs, CameraWrapper cameraWrapper, Camera.PreviewCallback previewCallback) {
        super(context, attrs);
        init(cameraWrapper, previewCallback);
    }

    public void init(CameraWrapper cameraWrapper, Camera.PreviewCallback previewCallback) {
        setCamera(cameraWrapper, previewCallback);
        autoFocusHandler = new Handler();
        getHolder().addCallback(this);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(CameraWrapper cameraWrapper, Camera.PreviewCallback previewCallback) {
        this.cameraWrapper = cameraWrapper;
        this.previewCallback = previewCallback;
    }

    public void setShouldScaleToFill(boolean scaleToFill) {
        shouldScaleToFill = scaleToFill;
    }

    public void setAspectTolerance(float aspectTolerance) {
        this.aspectTolerance = aspectTolerance;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        surfaceCreated = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        if (surfaceHolder.getSurface() == null) {
            return;
        }
        stopCameraPreview();
        showCameraPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        surfaceCreated = false;
        stopCameraPreview();
    }

    public void showCameraPreview() {
        if (cameraWrapper != null) {
            try {
                getHolder().addCallback(this);
                previewing = true;
                setupCameraParameters();
                cameraWrapper.camera.setPreviewDisplay(getHolder());
                cameraWrapper.camera.setDisplayOrientation(getDisplayOrientation());
                cameraWrapper.camera.setOneShotPreviewCallback(previewCallback);
                cameraWrapper.camera.startPreview();
                if (autoFocus) {
                    if (surfaceCreated) { // check if surface created before using autofocus
                        safeAutoFocus();
                    } else {
                        scheduleAutoFocus(); // wait 1 sec and then do check again
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
        }
    }

    public void safeAutoFocus() {
        try {
            cameraWrapper.camera.autoFocus(autoFocusCB);
        } catch (RuntimeException re) {
            // Horrible hack to deal with autofocus errors on Sony devices
            // See https://github.com/dm77/barcodescanner/issues/7 for example
            scheduleAutoFocus(); // wait 1 sec and then do check again
        }
    }

    public void stopCameraPreview() {
        if (cameraWrapper != null) {
            try {
                previewing = false;
                getHolder().removeCallback(this);
                cameraWrapper.camera.cancelAutoFocus();
                cameraWrapper.camera.setOneShotPreviewCallback(null);
                cameraWrapper.camera.stopPreview();
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
        }
    }

    public void setupCameraParameters() {
        Camera.Size optimalSize = getOptimalPreviewSize();
        Camera.Parameters parameters = cameraWrapper.camera.getParameters();
        parameters.setPreviewSize(optimalSize.width, optimalSize.height);
        cameraWrapper.camera.setParameters(parameters);
        adjustViewSize(optimalSize);
    }

    private void adjustViewSize(Camera.Size cameraSize) {
        Point previewSize = convertSizeToLandscapeOrientation(new Point(getWidth(), getHeight()));
        float cameraRatio = ((float) cameraSize.width) / cameraSize.height;
        float screenRatio = ((float) previewSize.x) / previewSize.y;

        if (screenRatio > cameraRatio) {
            setViewSize((int) (previewSize.y * cameraRatio), previewSize.y);
        } else {
            setViewSize(previewSize.x, (int) (previewSize.x / cameraRatio));
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private Point convertSizeToLandscapeOrientation(Point size) {
        if (getDisplayOrientation() % HALF_ROTATION_DEGREES == NO_ROTATION_DEGREES) {
            return size;
        } else {
            return new Point(size.y, size.x);
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void setViewSize(int width, int height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        int tmpWidth;
        int tmpHeight;
        if (getDisplayOrientation() % HALF_ROTATION_DEGREES == NO_ROTATION_DEGREES) {
            tmpWidth = width;
            tmpHeight = height;
        } else {
            tmpWidth = height;
            tmpHeight = width;
        }

        if (shouldScaleToFill) {
            int parentWidth = ((View) getParent()).getWidth();
            int parentHeight = ((View) getParent()).getHeight();
            float ratioWidth = (float) parentWidth / (float) tmpWidth;
            float ratioHeight = (float) parentHeight / (float) tmpHeight;

            float compensation = Math.max(ratioWidth, ratioHeight);

            tmpWidth = Math.round(tmpWidth * compensation);
            tmpHeight = Math.round(tmpHeight * compensation);
        }

        layoutParams.width = tmpWidth;
        layoutParams.height = tmpHeight;
        setLayoutParams(layoutParams);
    }

    public int getDisplayOrientation() {
        if (cameraWrapper == null) {
            // If we don't have a camera set there is no orientation so return dummy value
            return 0;
        }

        Camera.CameraInfo info = new Camera.CameraInfo();
        if (cameraWrapper.cameraId == -1) {
            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        } else {
            Camera.getCameraInfo(cameraWrapper.cameraId, info);
        }

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        return getDisplayOrientation(wm, info);
    }

    private int getDisplayOrientation(WindowManager wm, Camera.CameraInfo info) {
        Display display = wm.getDefaultDisplay();

        int rotation = display.getRotation();
        int degrees = switch (rotation) {
            case Surface.ROTATION_0 -> ROTATION_DEGREES_0;
            case Surface.ROTATION_90 -> ROTATION_DEGREES_90;
            case Surface.ROTATION_180 -> ROTATION_DEGREES_180;
            case Surface.ROTATION_270 -> ROTATION_DEGREES_270;
            default -> ROTATION_DEGREES_0;
        };

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % FULL_ROTATION_DEGREES;
            result = (FULL_ROTATION_DEGREES - result) % FULL_ROTATION_DEGREES;  // Compensate the mirror
        } else {  // Back-facing
            result = (info.orientation - degrees + FULL_ROTATION_DEGREES) % FULL_ROTATION_DEGREES;
        }
        return result;
    }

    private Camera.Size getOptimalPreviewSize() {
        if (cameraWrapper == null) {
            return null;
        }

        List<Camera.Size> sizes = cameraWrapper.camera.getParameters().getSupportedPreviewSizes();
        int width = getWidth();
        int height = getHeight();
        if (DisplayUtils.getScreenOrientation(getContext()) == Configuration.ORIENTATION_PORTRAIT) {
            int portraitWidth = height;
            height = width;
            width = portraitWidth;
        }

        double targetRatio = (double) width / height;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = height;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > aspectTolerance) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void setAutoFocus(boolean state) {
        if (cameraWrapper != null && previewing) {
            if (state == autoFocus) {
                return;
            }
            autoFocus = state;
            if (autoFocus) {
                if (surfaceCreated) { // Check if surface created before using autofocus
                    Log.v(TAG, "Starting autofocus");
                    safeAutoFocus();
                } else {
                    scheduleAutoFocus(); // Wait 1 sec and then do check again
                }
            } else {
                Log.v(TAG, "Cancelling autofocus");
                cameraWrapper.camera.cancelAutoFocus();
            }
        }
    }

    private void scheduleAutoFocus() {
        autoFocusHandler.postDelayed(doAutoFocus, DELAY);
    }
}
