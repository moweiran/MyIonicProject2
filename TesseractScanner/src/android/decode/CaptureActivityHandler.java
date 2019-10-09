/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.lwtch.tesseract.scanner.decode;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.zxing.Result;
import com.lwtch.tesseract.scanner.ScannerActivity;
import com.lwtch.tesseract.scanner.camera.CameraManager;
import com.lwtch.tesseract.scanner.TesseractScanner;

/**
 * This class handles all the messaging which comprises the state machine for
 * capture.
 */
public final class CaptureActivityHandler extends Handler {
    private static final String TAG = CaptureActivityHandler.class.getName();

    private final ScannerActivity mActivity;
    private final DecodeThread mDecodeThread;
    private State mState;

    public CaptureActivityHandler(ScannerActivity activity) {
        this.mActivity = activity;
        mDecodeThread = new DecodeThread(activity);
        mDecodeThread.start();
        mState = State.SUCCESS;
        // Start ourselves capturing previews and decoding.
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        Context context = TesseractScanner.sAppContext;
        packageName = context.getPackageName();
        switch (message.what) {
        case context.getResources().getIdentifier("auto_focus", "id", packageName):
            // Log.d(TAG, "Got auto-focus message");
            // When one auto focus pass finishes, start another. This is the closest thing
            // to
            // continuous AF. It does seem to hunt a bit, but I'm not sure what else to do.
            if (mState == State.PREVIEW) {
                CameraManager.get().requestAutoFocus(this, context.getResources().getIdentifier("auto_focus", "id", packageName));
            }
            break;
        case context.getResources().getIdentifier("decode_succeeded", "id", packageName):
            Log.e(TAG, "Got decode succeeded message");
            mState = State.SUCCESS;
            mActivity.handleDecode((Result) message.obj);
            break;
        case context.getResources().getIdentifier("decode_failed", "id", packageName):
            // We're decoding as fast as possible, so when one decode fails, start another.
            mState = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(mDecodeThread.getHandler(), context.getResources().getIdentifier("decode", "id", packageName));
            break;
        }
    }

    public void quitSynchronously() {
        Context context = TesseractScanner.sAppContext;
        packageName = context.getPackageName();
        mState = State.DONE;
        CameraManager.get().stopPreview();
        Message quit = Message.obtain(mDecodeThread.getHandler(), context.getResources().getIdentifier("quit", "id", packageName));
        quit.sendToTarget();
        try {
            mDecodeThread.join();
        } catch (InterruptedException e) {
            // continue
        }
        // Be absolutely sure we don't send any queued up messages
        removeMessages(context.getResources().getIdentifier("decode_succeeded", "id", packageName));
        removeMessages(context.getResources().getIdentifier("decode_failed", "id", packageName));
    }

    public void restartPreviewAndDecode() {
        if (mState != State.PREVIEW) {
            Context context = TesseractScanner.sAppContext;
            packageName = context.getPackageName();
            CameraManager.get().startPreview();
            mState = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(mDecodeThread.getHandler(), context.getResources().getIdentifier("decode", "id", packageName));
            CameraManager.get().requestAutoFocus(this, context.getResources().getIdentifier("auto_focus", "id", packageName));
        }
    }

    private enum State {
        PREVIEW, SUCCESS, DONE
    }

    public void onPause() {
        mState = State.DONE;
        CameraManager.get().stopPreview();
    }
}
