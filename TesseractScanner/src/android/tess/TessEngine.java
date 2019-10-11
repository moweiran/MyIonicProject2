package com.lwtch.tessocr.scanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Created by Fadi on 6/11/2014.
 */
public class TessEngine {

    static final String TAG = "DBG_" + TessEngine.class.getName();

    private Context context;

    private TessEngine(Context context) {
        this.context = context;
    }

    public static TessEngine Generate(Context context) {
        return new TessEngine(context);
    }

    public String detectText(Bitmap bitmap) {
        Log.d(TAG, "Initialization of TessBaseApi");
        TessDataManager.initTessTrainedData(context);
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        String path = TessDataManager.getTesseractFolder();
        Log.d(TAG, "Tess folder: " + path);
        tessBaseAPI.setDebug(true);
        tessBaseAPI.init(path, "eng");
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "1234567890");
        // tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST,
        // "!@#$%^&*()_+=-qwertyuiop[]}{POIU" +
        // "YTREWQasdASDfghFGHjklJKLl;L:'\"\\|~`xcvXCVbnmBNM,./<>?");
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "!@#$%^&*()_+=-[]}{;:'\"\\|~`,./<>?");
        // tessBaseAPI.setPageSegMode(TessBaseAPI.OEM_TESSERACT_CUBE_COMBINED);
        tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD);
        // tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);//单行模式
        Log.d(TAG, "Ended initialization of TessEngine");
        Log.d(TAG, "Running inspection on bitmap");
        tessBaseAPI.setImage(bitmap);
        String inspection = tessBaseAPI.getUTF8Text();
        // String inspection = tessBaseAPI.getHOCRText(0);
        String inspection2 = tessBaseAPI.getHOCRText(0);

        // Log.d(TAG, "Confidence values: " + inspection2);

        Log.d(TAG, "Got data: " + inspection);
        tessBaseAPI.end();
        System.gc();
        String phone = getTelNum(inspection2);
        Log.d(TAG, "Got phone: " + phone);
        return inspection;
    }

    private static Pattern pattern = Pattern.compile("(1|861)\\d{10}$*");

    private static StringBuilder bf = new StringBuilder();

    public static String getTelNum(String sParam) {
        if (TextUtils.isEmpty(sParam)) {
            return "";
        }

        Matcher matcher = pattern.matcher(sParam.trim());
        bf.delete(0, bf.length());

        while (matcher.find()) {
            bf.append(matcher.group()).append("\n");
        }
        int len = bf.length();
        if (len > 0) {
            bf.deleteCharAt(len - 1);
        }
        return bf.toString();
    }

}
