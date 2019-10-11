package com.lwtch.tessocr.scanner;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;

import java.util.List;

/**
 * Created by Fadi on 5/11/2014.
 */
public class CameraUtils {

    static final String TAG = "DBG_ " + CameraUtils.class.getName();

    public static boolean deviceHasCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static Camera getCamera() {
        try {
            return Camera.open();
        } catch (Exception e) {
            Log.e(TAG, "Cannot getCamera()");
            return null;
        }
    }

    /**
     * Android相机的预览尺寸都是4:3或者16:9，这里遍历所有支持的预览尺寸，得到16:9的最大尺寸，保证成像清晰度
     *
     * @param sizes
     * @return 最佳尺寸
     */
    public static Camera.Size getBestSize(List<Camera.Size> sizes) {
        Camera.Size bestSize = null;
        for (Camera.Size size : sizes) {
            if ((float) size.width / (float) size.height == 16.0f / 9.0f) {
                if (bestSize == null) {
                    bestSize = size;
                } else {
                    if (size.width > bestSize.width) {
                        bestSize = size;
                    }
                }
            }
        }
        return bestSize;
    }
}
