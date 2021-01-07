package com.example.psyad9.mp3player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import java.io.IOException;

/**
 * Created by pszmdf on 06/11/16.
 */
public class MP3Player {

    protected MediaPlayer mediaPlayer;
    protected MP3PlayerState state;
    protected String filePath;

    public enum MP3PlayerState {
        ERROR,
        PLAYING,
        PAUSED,
        STOPPED
    }

    public MP3Player() {
        this.state = MP3PlayerState.STOPPED;
    }

    public MP3PlayerState getState() {
        return this.state;
    }

    public void load(String filePath) {
        this.filePath = filePath;
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
        } catch (IOException e) {
            Log.e("MP3Player", e.toString());
            e.printStackTrace();
            this.state = MP3PlayerState.ERROR;
            return;
        } catch (IllegalArgumentException e) {
            Log.e("MP3Player", e.toString());
            e.printStackTrace();
            this.state = MP3PlayerState.ERROR;
            return;
        }

        this.state = MP3PlayerState.PLAYING;
        mediaPlayer.start();
    }

    public String getFilePath() {
        return this.filePath;
    }

    public int getProgress() {
        if (mediaPlayer != null) {
            if (this.state == MP3PlayerState.PAUSED || this.state == MP3PlayerState.PLAYING)
                return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (mediaPlayer != null)
            if (this.state == MP3PlayerState.PAUSED || this.state == MP3PlayerState.PLAYING)
                return mediaPlayer.getDuration();
        return 0;
    }

    //simplified the following three methods, as I handled states in the main method primarily
    public void play() {
        if (this.state == MP3PlayerState.PAUSED) {
            mediaPlayer.start();
            this.state = MP3PlayerState.PLAYING;
        }
    }
    public void pause()
    {
        mediaPlayer.pause();
        this.state = MP3PlayerState.PAUSED;
    }
    public void stop()
    {
        mediaPlayer.stop();
        this.state = MP3PlayerState.STOPPED;
    }

}

