package com.chromsicle.balloonpop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chromsicle.balloonpop.utils.PixelHelper;


@SuppressLint("AppCompatCustomView")
public class Balloon extends ImageView {

    public Balloon(Context context) {
        super(context);
    }

    public Balloon(Context context, int color, int rawHeight) {
        super(context);

        //set the balloon's color
        this.setImageResource(R.drawable.balloon);
        //change the color of the balloon
        this.setColorFilter(color);

        //set the balloon's size
        //the image is twice as tall as it is wide
        int rawWidth = rawHeight / 2;

        int dpHeight = PixelHelper.pixelsToDp(rawHeight, context);
        int dpWidth = PixelHelper.pixelsToDp(rawWidth, context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(dpWidth, dpHeight);
        setLayoutParams(params);
    }
}
