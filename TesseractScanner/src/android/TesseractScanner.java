package com.lwtch.tesseract.scanner;

import android.content.Context;
import android.content.Intent;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class TesseractScanner extends CordovaPlugin {
    public static Context sAppContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        sAppContext = cordova.getActivity().getApplicationContext();
        if (action.equals("tesseract_scanner")) {
            this.openScannerActivity(sAppContext);
            return true;
        }
        return false;
    }

    private void openScannerActivity(Context context) {
        Intent intent = new Intent(context, ScannerActivity.class);
        this.cordova.getActivity().startActivity(intent);
    }
}
