package com.lwtch.tessocr.scanner;

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
public class TessocrScanner extends CordovaPlugin {
    public static CallbackContext _callbackContext;
    public static Context appContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        _callbackContext = callbackContext;
        appContext = cordova.getActivity().getApplicationContext();
        if (action.equals("tessocr_scanner")) {
            Intent intent = new Intent(appContext, CameraActivity.class);
            this.cordova.getActivity().startActivity(intent);
            return true;
        }
        return false;
    }
}
