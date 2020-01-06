package com.chromsicle.balloonpop.utils;

import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.view.View;

import com.chromsicle.balloonpop.R;

public class SoundHelper {

    private MediaPlayer mMusicPlayer;

    private SoundPool mSoundPool;
    private int mSoundID;
    private boolean mLoaded;
    private float mVolume;

    //SoundPool is used for managing small audio files (1mb or smaller) like sound effects
    public SoundHelper(Activity activity) {

        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);

        //calculate the volumes
        float actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mVolume = actVolume / maxVolume;

        //connect the volume control and the activity
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //if at least lollipop, do it this way
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            mSoundPool = new SoundPool.Builder().setAudioAttributes(audioAttrib).setMaxStreams(6).build();
        //if older than lollipop, do it this way
        } else {
            //noinspection deprecation
            mSoundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }

        //load the sound
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mLoaded = true;
            }
        });
        mSoundID = mSoundPool.load(activity, R.raw.balloon_pop, 1);
    }

    //if the sound has been loaded, play it!
    public void playSound() {
        if (mLoaded) {
            mSoundPool.play(mSoundID, mVolume, mVolume, 1, 0, 1f);
        }
    }

    //instantiate the media player and then set some of its properties
    public void prepareMusicPlayer(Context context) {
        //instantiate
        mMusicPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.pleasant_music);
        //set the volume
        mMusicPlayer.setVolume(.5f, .5f);
        //loop the music
        mMusicPlayer.setLooping(true);
    }

    //start the music
    public void playMusic() {
        if (mMusicPlayer != null) {
            mMusicPlayer.start();
        }
    }

    public void pauseMusic() {
        if (mMusicPlayer != null && mMusicPlayer.isPlaying()) {
            mMusicPlayer.pause();
        }
    }
}

