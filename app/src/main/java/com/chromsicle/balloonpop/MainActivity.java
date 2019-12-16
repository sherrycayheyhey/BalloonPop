package com.chromsicle.balloonpop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

//use Android Resizer to get all the image sizes needed
//create a folder to temporarily store them in then set the resource directory to that
//select the input density then click the boxes for the versions wanted and drag the images over
//versions will show up in the temporary folder
//once all images have been added, copy all the folders and paste them into Android Studio

public class MainActivity extends AppCompatActivity {

    private ViewGroup mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //display the background and the application starts up
        getWindow().setBackgroundDrawableResource(R.drawable.modern_background);

        //object that can be referenced to see things in full screen view
        mContentView = findViewById(R.id.activity_main);

        //allow the user to restore the full screen mode
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setToFullScreen();
            }
        });
    }

    private void setToFullScreen() {
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
