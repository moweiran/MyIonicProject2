package com.lwtch.tesseract.scanner.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

import com.lwtch.tesseract.scanner.TesseractScanner;

public class ImageDialog extends Dialog {

    private Bitmap bmp;

    private String title;

    public ImageDialog(@NonNull Context context) {
        super(context);
    }

    public ImageDialog addBitmap(Bitmap bmp) {
        if (bmp != null) {
            this.bmp = bmp;
        }
        return this;
    }

    public ImageDialog addTitle(String title) {
        if (title != null) {
            this.title = title;
        }
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = TesseractScanner.sAppContext;
        setContentView(context.getResources().getIdentifier("image_dialog", "layout", context.getPackageName()));

        ImageView imageView = (ImageView) findViewById(context.getResources().getIdentifier("image_dialog_imageView", "id", context.getPackageName()));
        TextView textView = (TextView) findViewById(context.getResources().getIdentifier("image_dialog_textView", "id", context.getPackageName()));

        if (bmp != null) {
            imageView.setImageBitmap(bmp);
        }

        if (title != null) {
            textView.setText(this.title);
        }
    }

    @Override
    public void dismiss() {
        bmp.recycle();
        bmp = null;
        System.gc();
        super.dismiss();
    }
}
