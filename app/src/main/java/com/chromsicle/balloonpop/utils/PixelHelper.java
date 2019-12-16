package com.chromsicle.balloonpop.utils;

import android.content.Context;
import android.util.TypedValue;

public class PixelHelper {

    //takes pixels and returns the same value as device independent pixels for the current device
    public static int pixelsToDp(int px, Context context) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, px,
                context.getResources().getDisplayMetrics());
    }

}