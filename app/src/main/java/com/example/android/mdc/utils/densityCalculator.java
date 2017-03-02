package com.example.android.mdc.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by Jandro on 1/22/2017.
 */

public class DensityCalculator {
    private static Resources resources;
    private static DisplayMetrics metrics;
    public DensityCalculator(Context context) {
        resources = context.getResources();
        metrics = resources.getDisplayMetrics();
    }


    public static float getDpFromPx(float px){
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static float getPxFromDp(float dp){
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static float getSpFromPx(float px){
        float sp = px / metrics.scaledDensity;
        return px;
    }

    public static float getPxFromSp(float sp){
        float px = sp * metrics.scaledDensity;
        return px;
    }

    public static float getScreenHeight(String dim){
        float height=0;
        if(dim.equals("px")){
            height = metrics.heightPixels;
        }else if(dim.equals("dp")){
            height = getDpFromPx(metrics.heightPixels);
        }
        return height;
    }
}
