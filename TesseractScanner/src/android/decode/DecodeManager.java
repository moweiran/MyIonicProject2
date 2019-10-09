package com.lwtch.tesseract.scanner.decode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * 二维码解析管理。
 */
public class DecodeManager {

    public void showCouldNotReadQrCodeFromScanner(Context context, final OnRefreshCameraListener listener) {
        new AlertDialog.Builder(context).setTitle(context.getResources().getIdentifier("notification", "string", context.getPackageName()))
                .setMessage(context.getResources().getIdentifier("could_not_read_qr_code_from_scanner", "string", context.getPackageName()))
                .setPositiveButton(context.getResources().getIdentifier("close", "string", context.getPackageName()), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (listener != null) {
                            listener.refresh();
                        }
                    }
                }).show();
    }

    public interface OnRefreshCameraListener {
        void refresh();
    }
}
