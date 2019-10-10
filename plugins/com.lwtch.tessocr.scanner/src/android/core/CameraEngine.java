package com.lwtch.tessocr.scanner;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.graphics.ImageFormat;

import java.util.List;
import java.io.IOException;

/**
 * Created by Fadi on 5/11/2014.
 */
public class CameraEngine {

    static final String TAG = "DBG_" + CameraUtils.class.getName();

    boolean on;
    Camera camera;
    SurfaceHolder surfaceHolder;

    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {

        }
    };

    public boolean isOn() {
        return on;
    }

    private CameraEngine(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
    }

    static public CameraEngine New(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "Creating camera engine");
        return new CameraEngine(surfaceHolder);
    }

    public void requestFocus() {
        if (camera == null)
            return;

        if (isOn()) {
            camera.autoFocus(autoFocusCallback);
        }
    }

    public void start() {

        Log.d(TAG, "Entered CameraEngine - start()");
        this.camera = CameraUtils.getCamera();

        if (this.camera == null)
            return;

        Log.d(TAG, "Got camera hardware");

        try {
            Camera.Parameters params = this.camera.getParameters();
            List<Camera.Size> sizes = params.getSupportedPictureSizes();
            Camera.Size size = sizes.get(0);
            for (int i = 0; i < sizes.size(); i++) {
                if (sizes.get(i).width > size.width)
                    size = sizes.get(i);
            }
            params.setPictureSize(size.width, size.height);
            params.setPictureFormat(ImageFormat.JPEG);
            params.setJpegQuality(100);
            this.camera.setParameters(params);
            this.camera.setPreviewDisplay(this.surfaceHolder);
            this.camera.setDisplayOrientation(90);
            this.camera.startPreview();

            on = true;

            Log.d(TAG, "CameraEngine preview started");

        } catch (IOException e) {
            Log.e(TAG, "Error in setPreviewDisplay");
        }
    }

    public void stop() {

        if (camera != null) {
            // this.autoFocusEngine.stop();
            camera.release();
            camera = null;
        }

        on = false;

        Log.d(TAG, "CameraEngine Stopped");
    }

    public void takeShot(Camera.ShutterCallback shutterCallback, Camera.PictureCallback rawPictureCallback,
            Camera.PictureCallback jpegPictureCallback) {
        if (isOn()) {
            camera.takePicture(shutterCallback, rawPictureCallback, jpegPictureCallback);
        }
    }

}
