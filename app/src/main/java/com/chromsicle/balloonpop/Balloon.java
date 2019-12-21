package com.chromsicle.balloonpop;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.chromsicle.balloonpop.utils.PixelHelper;


@SuppressLint("AppCompatCustomView")
public class Balloon extends ImageView implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {

    private ValueAnimator mAnimator;

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

    public void releaseBalloon(int screenHeight, int duration) {
        mAnimator = new ValueAnimator();
        mAnimator.setDuration(duration);
        mAnimator.setFloatValues(screenHeight, 0f); //float for smooth animation
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setTarget(this);
        mAnimator.addListener(this);
        mAnimator.addUpdateListener(this);
        mAnimator.start();
    }

    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {

    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        //to make the balloon move, handle the event that occurs each time the animated value changes
        setY((Float) valueAnimator.getAnimatedValue());

    }
}
