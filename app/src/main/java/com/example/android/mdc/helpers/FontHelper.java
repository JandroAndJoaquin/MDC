package com.example.android.mdc.helpers;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;


public class FontHelper {
    Typeface tmpFont;
    Context context;

    public FontHelper(Context ctx){
        context = ctx;
    }

    public void setTextViewFont(TextView tv, String fnt){
        tmpFont = Typeface.createFromAsset(context.getAssets(), "font/"+fnt+".ttf");
        tv.setTypeface(tmpFont);
    }
}
