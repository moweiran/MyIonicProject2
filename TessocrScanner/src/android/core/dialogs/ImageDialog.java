package com.lwtch.tessocr.scanner;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lwtch.tessocr.scanner.TessocrScanner;

/**
 * Created by Fadi on 5/11/2014.
 */
public class ImageDialog extends DialogFragment {

    private Bitmap bmp;

    private String title;

    public ImageDialog() {
    }

    public static ImageDialog New() {
        return new ImageDialog();
    }

    public ImageDialog addBitmap(Bitmap bmp) {
        if (bmp != null)
            this.bmp = bmp;
        return this;
    }

    public ImageDialog addTitle(String title) {
        if (title != null)
            this.title = title;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String packageName = TessocrScanner.appContext.getPackageName();
        int r_layout_image_dialog = TessocrScanner.appContext.getResources().getIdentifier("image_dialog", "layout",
                packageName);
        View view = inflater.inflate(r_layout_image_dialog, null);

        int r_id_image_dialog_imageView = TessocrScanner.appContext.getResources()
                .getIdentifier("image_dialog_imageView", "id", packageName);
        int r_id_image_dialog_textView = TessocrScanner.appContext.getResources().getIdentifier("image_dialog_textView",
                "id", packageName);
        ImageView imageView = (ImageView) view.findViewById(r_id_image_dialog_imageView);
        TextView textView = (TextView) view.findViewById(r_id_image_dialog_textView);

        if (bmp != null)
            imageView.setImageBitmap(bmp);

        if (title != null)
            textView.setText(this.title);

        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        bmp.recycle();
        bmp = null;
        System.gc();
        super.onDismiss(dialog);
    }
}