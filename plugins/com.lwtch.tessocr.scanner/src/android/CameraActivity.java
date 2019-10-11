package com.lwtch.tessocr.scanner;

import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.lwtch.tessocr.scanner.TessocrScanner;

public class CameraActivity extends Activity
        implements SurfaceHolder.Callback, View.OnClickListener, Camera.PictureCallback, Camera.ShutterCallback {

    static final String TAG = "DBG_" + CameraActivity.class.getName();

    Button shutterButton;
    Button focusButton;
    FocusBoxView focusBox;
    SurfaceView cameraFrame;
    CameraEngine cameraEngine;
    String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageName = getApplication().getPackageName();
        int r_layout_activity_main = getApplication().getResources().getIdentifier("activity_main", "layout",
                packageName);
        setContentView(r_layout_activity_main);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Log.d(TAG, "Surface Created - starting camera");

        if (cameraEngine != null && !cameraEngine.isOn()) {
            cameraEngine.start();
        }

        if (cameraEngine != null && cameraEngine.isOn()) {
            Log.d(TAG, "Camera engine already on");
            return;
        }

        cameraEngine = CameraEngine.New(holder);
        cameraEngine.start();

        Log.d(TAG, "Camera engine started");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        int r_id_camera_frame = getApplication().getResources().getIdentifier("camera_frame", "id", packageName);
        int r_id_shutter_button = getApplication().getResources().getIdentifier("shutter_button", "id", packageName);
        int r_id_focus_box = getApplication().getResources().getIdentifier("focus_box", "id", packageName);
        int r_id_focus_button = getApplication().getResources().getIdentifier("focus_button", "id", packageName);

        cameraFrame = (SurfaceView) findViewById(r_id_camera_frame);
        focusBox = (FocusBoxView) findViewById(r_id_focus_box);

        shutterButton = (Button) findViewById(r_id_shutter_button);
        focusButton = (Button) findViewById(r_id_focus_button);

        shutterButton.setOnClickListener(this);
        focusButton.setOnClickListener(this);

        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        cameraFrame.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (cameraEngine != null && cameraEngine.isOn()) {
            cameraEngine.stop();
        }

        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
        surfaceHolder.removeCallback(this);
    }

    @Override
    public void onClick(View v) {
        if (v == shutterButton) {
            if (cameraEngine != null && cameraEngine.isOn()) {
                cameraEngine.takeShot(this, this, this);
            }
        }

        if (v == focusButton) {
            if (cameraEngine != null && cameraEngine.isOn()) {
                cameraEngine.requestFocus();
            }
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        Log.d(TAG, "Picture taken");

        if (data == null) {
            Log.d(TAG, "Got null data");
            return;
        }
        TessocrScanner._callbackContext.success(data);
        Bitmap bmp = Tools.getFocusedBitmap(this, camera, data, focusBox.getBox());

        Log.d(TAG, "Got bitmap");

        Log.d(TAG, "Initialization of TessBaseApi");

        new TessAsyncEngine().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, this, bmp);

    }

    @Override
    public void onShutter() {

    }

}