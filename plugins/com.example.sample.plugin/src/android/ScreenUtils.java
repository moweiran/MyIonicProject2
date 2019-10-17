package com.example.sample.plugin;

import com.example.sample.plugin.PluginName;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.graphics.Point;

/**
 * ScreenUtils
 */
public class ScreenUtils {

    private ScreenUtils() {
        throw new AssertionError();
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth() {
        Context context = PluginName.sAppContext;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int getScreenHeight() {
        Context context = PluginName.sAppContext;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static Point getScreenResolution(Context context) {

        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();

        int width = display.getWidth();
        int height = display.getHeight();
        return new Point(width, height);
    }
}
