package com.lwtch.tesseract.scanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.zxing.Result;
import com.lwtch.tesseract.scanner.camera.CameraManager;
import com.lwtch.tesseract.scanner.decode.CaptureActivityHandler;
import com.lwtch.tesseract.scanner.decode.DecodeManager;
import com.lwtch.tesseract.scanner.decode.InactivityTimer;
import com.lwtch.tesseract.scanner.tess.TesseractCallback;
import com.lwtch.tesseract.scanner.tess.TesseractThread;
import com.lwtch.tesseract.scanner.utils.Tools;
import com.lwtch.tesseract.scanner.view.ImageDialog;
import com.lwtch.tesseract.scanner.view.ScannerFinderView;

import java.io.IOException;

/**
 * 二维码扫描类。
 */
public class ScannerActivity extends Activity implements Callback, Camera.PictureCallback, Camera.ShutterCallback {

    private CaptureActivityHandler mCaptureActivityHandler;
    private boolean mHasSurface;
    private InactivityTimer mInactivityTimer;
    private ScannerFinderView mQrCodeFinderView;
    private SurfaceView mSurfaceView;
    private ViewStub mSurfaceViewStub;
    private DecodeManager mDecodeManager = new DecodeManager();
    private Switch switch1;
    private Button bt;

    private ProgressDialog progressDialog;
    private Bitmap bmp;
    private String packageName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        packageName = getApplication().getPackageName();
        setContentView(getApplication().getResources().getIdentifier("activity_scanner", "layout", packageName));
        initView();
        initData();
    }

    private void initView() {
        mQrCodeFinderView = (ScannerFinderView) findViewById(getApplication().getResources().getIdentifier("qr_code_view_finder", "id", packageName));
        mSurfaceViewStub = (ViewStub) findViewById(getApplication().getResources().getIdentifier("qr_code_view_stub", "id", packageName));
        switch1 = (Switch) findViewById(getApplication().getResources().getIdentifier("switch1", "id", packageName));
        mHasSurface = false;

        bt = (Button) findViewById(getApplication().getResources().getIdentifier("bt", "id", packageName));

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt.setEnabled(false);
                buildProgressDialog();
                CameraManager.get().takeShot(ScannerActivity.this, ScannerActivity.this, ScannerActivity.this);
            }
        });

        Switch switch2 = (Switch) findViewById(getApplication().getResources().getIdentifier("switch2", "id", packageName));
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CameraManager.get().setFlashLight(isChecked);
            }
        });
    }

    public Rect getCropRect() {
        return mQrCodeFinderView.getRect();
    }

    public boolean isQRCode() {
        return switch1.isChecked();
    }

    private void initData() {
        mInactivityTimer = new InactivityTimer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CameraManager.init();
        initCamera();
    }

    private void initCamera() {
        if (null == mSurfaceView) {
            mSurfaceViewStub.setLayoutResource(getApplication().getResources().getIdentifier("layout_surface_view", "layout", packageName));
            mSurfaceView = (SurfaceView) mSurfaceViewStub.inflate();
        }
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        if (mHasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            // 防止sdk8的设备初始化预览异常(可去除，本项目最小16)
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    @Override
    protected void onPause() {
        if (mCaptureActivityHandler != null) {
            try {
                mCaptureActivityHandler.quitSynchronously();
                mCaptureActivityHandler = null;
                if (null != mSurfaceView && !mHasSurface) {
                    mSurfaceView.getHolder().removeCallback(this);
                }
                CameraManager.get().closeDriver();
            } catch (Exception e) {
                // 关闭摄像头失败的情况下,最好退出该Activity,否则下次初始化的时候会显示摄像头已占用.
                finish();
            }
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (null != mInactivityTimer) {
            mInactivityTimer.shutdown();
        }
        super.onDestroy();
    }

    /**
     * Handler scan result
     *
     * @param result
     */
    public void handleDecode(Result result) {
        mInactivityTimer.onActivity();
        if (null == result) {
            mDecodeManager.showCouldNotReadQrCodeFromScanner(this, new DecodeManager.OnRefreshCameraListener() {
                @Override
                public void refresh() {
                    restartPreview();
                }
            });
        } else {
            handleResult(result);
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            if (!CameraManager.get().openDriver(surfaceHolder)) {
                return;
            }
        } catch (IOException e) {
            // 基本不会出现相机不存在的情况
            Toast.makeText(this, getString(getApplication().getResources().getIdentifier("camera_not_found", "string", packageName)), Toast.LENGTH_SHORT).show();
            finish();
            return;
        } catch (RuntimeException re) {
            re.printStackTrace();
            return;
        }
        mQrCodeFinderView.setVisibility(View.VISIBLE);
        findViewById(getApplication().getResources().getIdentifier("qr_code_view_background", "id", packageName)).setVisibility(View.GONE);
        if (mCaptureActivityHandler == null) {
            mCaptureActivityHandler = new CaptureActivityHandler(this);
        }
    }

    public void restartPreview() {
        if (null != mCaptureActivityHandler) {
            try {
                mCaptureActivityHandler.restartPreviewAndDecode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!mHasSurface) {
            mHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHasSurface = false;
    }

    public Handler getCaptureActivityHandler() {
        return mCaptureActivityHandler;
    }

    private void handleResult(Result result) {
        if (TextUtils.isEmpty(result.getText())) {
            mDecodeManager.showCouldNotReadQrCodeFromScanner(this, new DecodeManager.OnRefreshCameraListener() {
                @Override
                public void refresh() {
                    restartPreview();
                }
            });
        } else {
            Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
//            vibrator.vibrate(200L);
            if (switch1.isChecked()) {
                qrSucceed(result.getText());
            } else {
//                phoneSucceed(result.getText(), result.getBitmap());
            }
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        if (data == null) {
            return;
        }
        mCaptureActivityHandler.onPause();
        bmp = null;
        bmp = Tools.getFocusedBitmap(this, camera, data, getCropRect());

        TesseractThread mTesseractThread = new TesseractThread(bmp, new TesseractCallback() {

            @Override
            public void succeed(String result) {
                Message message = Message.obtain();
                message.what = 0;
                message.obj = result;
                mHandler.sendMessage(message);
            }

            @Override
            public void fail() {
                Message message = Message.obtain();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        });

        Thread thread = new Thread(mTesseractThread);
        thread.start();
    }

    @Override
    public void onShutter() {
    }

    private void qrSucceed(String result) {
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(getApplication().getResources().getIdentifier("notification", "string", packageName)).setMessage(result)
                .setPositiveButton(getApplication().getResources().getIdentifier("positive_button_confirm", "string", packageName), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        restartPreview();
                    }
                }).show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                restartPreview();
            }
        });
    }

    private void phoneSucceed(String result, Bitmap bitmap) {
        ImageDialog dialog = new ImageDialog(this);
        dialog.addBitmap(bitmap);
        dialog.addTitle(TextUtils.isEmpty(result) ? "未识别到手机号码" : result);
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                restartPreview();
            }
        });
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            bt.setEnabled(true);
            cancelProgressDialog();
            switch (msg.what) {
            case 0:
                phoneSucceed((String) msg.obj, bmp);
                break;
            case 1:
                Toast.makeText(ScannerActivity.this, "无法识别", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
            }
        }
    };

    public void buildProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        progressDialog.setMessage("识别中...");
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    public void cancelProgressDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
}
