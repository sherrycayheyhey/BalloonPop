package com.chromsicle.balloonpop.utils;

import android.content.Context;
import android.media.MediaPlayer;

import com.chromsicle.balloonpop.R;

public class SoundHelper {

    private MediaPlayer mMusicPlayer;

    public SoundHelper() {
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

