package com.lwtch.camera;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.lwtch.camera.CameraAutoFocusCallBack;

public class CameraActivity extends Activity implements SurfaceHolder.Callback {
    /** Called when the activity is first created. */
    SurfaceHolder surfaceHolder;
    SurfaceView surfaceView1;
    Button button1;
    ImageView imageView1;
    Camera camera;
    String packageName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageName = getApplication().getPackageName();
        setContentView(getApplication().getResources().getIdentifier("camera_autofocus", "layout", packageName));
        button1 = (Button) findViewById(getApplication().getResources().getIdentifier("button1", "id", packageName));
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 设为横向显示。因为摄像头会自动翻转90度，所以如果不横向显示，看到的画面就是翻转的。
        // 本例中不横向

        surfaceView1 = (SurfaceView) findViewById(
                getApplication().getResources().getIdentifier("surfaceView1", "id", packageName));
        imageView1 = (ImageView) findViewById(
                getApplication().getResources().getIdentifier("imageView1", "id", packageName));
        surfaceHolder = surfaceView1.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.autoFocus(afcb);
                // 自动对焦
            }
        });
    }

    PictureCallback jpeg = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            CameraAutoFocusCallBack._callbackContext.success(data);
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            // byte数组转换成Bitmap
            imageView1.setImageBitmap(bmp);
            // 拍下图片显示在下面的ImageView里
            FileOutputStream fop;
            try {
                fop = new FileOutputStream("/sdcard/dd.jpg");
                // 实例化FileOutputStream，参数是生成路径
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fop);
                // 压缩bitmap写进outputStream 参数：输出格式 输出质量 目标OutputStream
                // 格式可以为jpg,png,jpg不能存储透明
                fop.close();
                System.out.println("拍成功");
                // 关闭流
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("FileNotFoundException");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("IOException");
            }
            camera.startPreview();
            // 需要手动重新startPreview，否则停在拍下的瞬间
        }

    };

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
        camera.setDisplayOrientation(90);
        try {
            Camera.Parameters parameters = camera.getParameters();
            // parameters.setPictureFormat(PixelFormat.JPEG);
            // 每台机器可用的PreviewSize不同，可以parameters.getSupportedPreviewSizes();获取支持的size
            // parameters.setPreviewSize(320, 220);
            camera.setParameters(parameters);
            // 设置参数
            camera.setPreviewDisplay(surfaceHolder);
            // 摄像头画面显示在Surface上
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        System.out.println("surfaceDestroyed");
        camera.stopPreview();
        // 关闭预览
        camera.release();
        // 释放资源
    }

    AutoFocusCallback afcb = new AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                // 对焦成功才拍照
                camera.takePicture(null, null, jpeg);
            }
        }
    };
}