package com.example.sample.plugin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PermissionHelper;

import android.widget.FrameLayout;
import android.hardware.Camera;
import android.content.pm.PackageManager;
import android.widget.RelativeLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup;
import android.view.SurfaceView;
import android.util.Log;

import static android.hardware.Camera.Parameters.FLASH_MODE_OFF;
import static android.hardware.Camera.Parameters.FOCUS_MODE_AUTO;
import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.sample.plugin.ScannerFinderView;

public class PluginName extends CordovaPlugin {
    private static final String TAG = "PluginName";
    public static Context sAppContext;
    private String[] permissions = { Manifest.permission.CAMERA };
    private Camera camera;
    private RelativeLayout layout;
    private FrameLayout cameraPreviewView;
    private ScannerFinderView mQrCodeFinderView;
    private CustomCameraPreview customCameraPreview;
    String packageName;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Context context = cordova.getActivity().getApplicationContext();
        sAppContext = cordova.getActivity().getApplicationContext();
        packageName = context.getPackageName();
        if (action.equals("new_activity")) {
            this.openNewActivity(context);
            return true;
        }
        if (action.equals("prepare")) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    cordova.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "prepare");
                            System.out.print("prepare");
                            prepare(callbackContext);
                        }
                    });
                }
            });
            return true;
        }
        return false;
    }

    private void openNewActivity(Context context) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.getView().setBackgroundColor(Color.argb(1, 0, 0, 0));
            }
        });
    }

    public boolean hasPermission() {
        for (String p : permissions) {
            if (!PermissionHelper.hasPermission(this, p)) {
                return false;
            }
        }
        return true;
    }

    private void requestPermission(int requestCode) {
        PermissionHelper.requestPermissions(this, requestCode, permissions);
    }

    private boolean hasCamera() {
        if (this.cordova.getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    private void prepare(final CallbackContext callbackContext) {
        if (hasCamera()) {
            if (!hasPermission()) {
                requestPermission(33);
            } else {
                setupCamera(callbackContext);
                // if (!scanning)
                // getStatus(callbackContext);
            }
        } else {
            // callbackContext.error(QRScannerError.BACK_CAMERA_UNAVAILABLE);
        }
    }

    private void setupCamera(CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                camera = Camera.open();
                Camera.Parameters cameraSettings = camera.getParameters();
                cameraSettings.setJpegQuality(100);
                List<String> supportedFocusModes = cameraSettings.getSupportedFocusModes();
                if (supportedFocusModes.contains(FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    cameraSettings.setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE);
                } else if (supportedFocusModes.contains(FOCUS_MODE_AUTO)) {
                    cameraSettings.setFocusMode(FOCUS_MODE_AUTO);
                }
                cameraSettings.setFlashMode(FLASH_MODE_OFF);
                camera.setParameters(cameraSettings);

                FrameLayout.LayoutParams cameraPreviewParams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                customCameraPreview = new CustomCameraPreview(cordova.getActivity().getApplicationContext(), camera);

                RelativeLayout.LayoutParams relLayoutParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                relLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                mQrCodeFinderView = new ScannerFinderView(cordova.getActivity().getApplicationContext());

                ((ViewGroup) webView.getView().getParent()).addView(customCameraPreview, cameraPreviewParams);
                ((ViewGroup) webView.getView().getParent()).addView(mQrCodeFinderView, relLayoutParams);
                webView.getView().bringToFront();
            }
        });
    }
}
