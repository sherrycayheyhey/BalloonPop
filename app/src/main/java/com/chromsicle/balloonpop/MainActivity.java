package com.chromsicle.balloonpop;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

//use Android Resizer to get all the image sizes needed
//create a folder to temporarily store them in then set the resource directory to that
//select the input density then click the boxes for the versions wanted and drag the images over
//versions will show up in the temporary folder
//once all images have been added, copy all the folders and paste them into Android Studio

public class MainActivity extends AppCompatActivity {

    private ViewGroup mContentView;

    private int[] mBalloonColors = new int [3];
    private int mNextColor, mScreenWidth, mScreenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //balloon colors
        mBalloonColors[0] = Color.argb(255, 255, 0, 0);
        mBalloonColors[1] = Color.argb(255, 0, 255, 0);
        mBalloonColors[2] = Color.argb(255, 0, 0, 255);

        //display the background and the application starts up
        getWindow().setBackgroundDrawableResource(R.drawable.modern_background);

        //object that can be referenced to see things in full screen view
        mContentView = findViewById(R.id.activity_main);

        //only get the height and width after the layout is inflated and the screen is set to full screen dimensions
        ViewTreeObserver viewTreeObserver = mContentView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mScreenWidth = mContentView.getWidth();
                    mScreenHeight = mContentView.getHeight();
                }
            });
        }

        //allow the user to restore the full screen mode
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setToFullScreen();
            }
        });

        mContentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Balloon b = new Balloon(MainActivity.this, mBalloonColors[mNextColor], 100);
                    b.setX(motionEvent.getX());
                    b.setY(mScreenHeight); //balloons come from the bottom of the screen
                    mContentView.addView(b);
                    b.releaseBalloon(mScreenHeight, 3000);

                    if(mNextColor + 1 == mBalloonColors.length) {
                        mNextColor = 0;
                    } else {
                        mNextColor++;
                    }
                }

                return false;
            }
        });
    }

    private void setToFullScreen() {
        //look directly at the root element instead of other elements
        ViewGroup rootLayout = (ViewGroup) findViewById(R.id.activity_main);
        rootLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    //to enter full screen mode instantly when the app runs
    @Override
    protected void onResume() {
        super.onResume();
        setToFullScreen();
    }
}
