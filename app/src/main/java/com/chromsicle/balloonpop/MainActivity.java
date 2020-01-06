package com.chromsicle.balloonpop;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chromsicle.balloonpop.utils.HighScoreHelper;
import com.chromsicle.balloonpop.utils.SimpleAlertDialog;
import com.chromsicle.balloonpop.utils.SoundHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

//use Android Resizer to get all the image sizes needed
//create a folder to temporarily store them in then set the resource directory to that
//select the input density then click the boxes for the versions wanted and drag the images over
//versions will show up in the temporary folder
//once all images have been added, copy all the folders and paste them into Android Studio

public class MainActivity extends AppCompatActivity
    implements Balloon.BalloonListener{

    //constants
    //delay is the amount of time between balloon launches
    private static final int MIN_ANIMATION_DELAY = 500;
    private static final int MAX_ANIMATION_DELAY = 1500;
    //duration is the length of each balloon animation
    //slowest is 1 second, fastest is 8 seconds
    private static final int MIN_ANIMATION_DURATION = 1000;
    private static final int MAX_ANIMATION_DURATION = 8000;
    private static final int NUMBER_OF_PINS = 5;
    private static final int BALLOONS_PER_LEVEL = 3;

    private ViewGroup mContentView;

    private int[] mBalloonColors = new int [3];
    private int mNextColor, mScreenWidth, mScreenHeight;
    private int mLevel, mScore, mPinsUsed;
    TextView mScoreDisplay, mLevelDisplay;
    private List<ImageView> mPinImages = new ArrayList<>();
    //this list will have references to all of the balloons that are visible on the screen
    // each time a new balloon is created it gets added to this list
    private List<Balloon> mBalloons = new ArrayList<>();

    private Button mGoButton;
    private boolean mPlaying;
    private boolean mGameStopped = true;
    private int mBalloonsPopped;

    //sounds
    private SoundHelper mSoundHelper;

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

        //references to visual objects, aka widget references
        mScoreDisplay = findViewById(R.id.score_display);
        mLevelDisplay = findViewById(R.id.level_display);
        mPinImages.add((ImageView) findViewById(R.id.pushpin1));
        mPinImages.add((ImageView) findViewById(R.id.pushpin2));
        mPinImages.add((ImageView) findViewById(R.id.pushpin3));
        mPinImages.add((ImageView) findViewById(R.id.pushpin4));
        mPinImages.add((ImageView) findViewById(R.id.pushpin5));
        mGoButton = findViewById(R.id.go_button);

        updateDisplay();

        //instantiate the sound object
        mSoundHelper = new SoundHelper();
        //prepare the music player object
        mSoundHelper.prepareMusicPlayer(this);
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

    private void startGame() {
        setToFullScreen();
        mScore = 0;
        mLevel = 0;
        mPinsUsed = 0;
        //for each loop
        for (ImageView pin:
                mPinImages) {
            pin.setImageResource(R.drawable.pin);
        }
        mGameStopped = false;
        startLevel();
        mSoundHelper.playMusic();
    }

    private void startLevel() {
        mLevel++;
        updateDisplay();
        BalloonLauncher launcher = new BalloonLauncher();
        launcher.execute(mLevel);
        mPlaying = true;
        mBalloonsPopped = 0;
        mGoButton.setText("Stop Game");
    }

    private void finishLevel() {
        //this is cool, use String format to pass in the level to the String the right way
        Toast.makeText(this, String.format("You finished level %d", mLevel), Toast.LENGTH_SHORT).show();
        mPlaying = false;
        mGoButton.setText(String.format("Start Level %d", mLevel + 1));
    }

    public void goButtonClickedHandler(View view) {
        if (mPlaying) {
            //user is playing so clicking the button means they want to stop the game completely
            gameOver(false);
        } else if (mGameStopped) {
            //the game has been stopped (or never started) so the user wants to start the game
            startGame();
        } else {
            //the game is going on but the user is between levels so they want to start the level
            startLevel();
        }
    }

    @Override
    public void popBalloon(Balloon balloon, boolean userTouch) {
        mBalloonsPopped++;

        //when the balloon pops, make it disappear
        mContentView.removeView(balloon);
        //also remove it from the list
        mBalloons.remove(balloon);

        if (userTouch) {
            mScore++;
        } else {
            //the balloon got to the top of the screen
            mPinsUsed++;
            if (mPinsUsed <= mPinImages.size()) {
                mPinImages.get(mPinsUsed - 1).setImageResource(R.drawable.pin_off);
            }
            if (mPinsUsed == NUMBER_OF_PINS) {
                gameOver(true);
                return;
            } else {
                Toast.makeText(this, "missed that one!", Toast.LENGTH_SHORT).show();
            }
        }
        updateDisplay();

        if (mBalloonsPopped == BALLOONS_PER_LEVEL) {
            finishLevel();
        }
    }

    private void gameOver(boolean allPinsUsed) {
        //when the game is over, remove all existing balloons from the screen
        Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT).show();
       mSoundHelper.pauseMusic();

        //this is a for each loop
        for (Balloon balloon:
                mBalloons) {
            mContentView.removeView(balloon);
            balloon.setPopped(true);
        }
        mBalloons.clear();
        mPlaying = false;
        mGameStopped = true;
        mGoButton.setText("Start Game");

        if (allPinsUsed) {
            //the game was lost
            if (HighScoreHelper.isTopScore(this, mScore)) {
                HighScoreHelper.setTopScore(this, mScore);
                SimpleAlertDialog dialog = SimpleAlertDialog.newInstance("New High Score!",
                        String.format("Your new high score is %d", mScore));
                        dialog.show(getSupportFragmentManager(), null);
            }
        }
    }

    private void updateDisplay() {
        mScoreDisplay.setText(String.valueOf(mScore));
        mLevelDisplay.setText(String.valueOf(mLevel));
    }

    private class BalloonLauncher extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected Void doInBackground(Integer... params) {

            if (params.length != 1) {
                throw new AssertionError(
                        "Expected 1 param for current level");
            }

            int level = params[0];
            int maxDelay = Math.max(MIN_ANIMATION_DELAY,
                    (MAX_ANIMATION_DELAY - ((level - 1) * 500)));
            int minDelay = maxDelay / 2;

            int balloonsLaunched = 0;
            while (mPlaying && balloonsLaunched < BALLOONS_PER_LEVEL) {

//              Get a random horizontal position for the next balloon
                Random random = new Random(new Date().getTime());
                int xPosition = random.nextInt(mScreenWidth - 200);
                publishProgress(xPosition);
                balloonsLaunched++;

//              Wait a random number of milliseconds before looping
                int delay = random.nextInt(minDelay) + minDelay;
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int xPosition = values[0];
            launchBalloon(xPosition);
        }

    }

    private void launchBalloon(int x) {

        Balloon balloon = new Balloon(this, mBalloonColors[mNextColor], 150);
        mBalloons.add(balloon); //adds to the list

        if (mNextColor + 1 == mBalloonColors.length) {
            mNextColor = 0;
        } else {
            mNextColor++;
        }

//      Set balloon vertical position and dimensions, add to container
        balloon.setX(x);
        balloon.setY(mScreenHeight + balloon.getHeight());
        mContentView.addView(balloon);

//      Let 'er fly
        int duration = Math.max(MIN_ANIMATION_DURATION, MAX_ANIMATION_DURATION - (mLevel * 1000));
        balloon.releaseBalloon(mScreenHeight, duration);

    }
}
