package com.codemountain.notetaker.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import com.codemountain.notetaker.entity.Note;
import com.codemountain.notetaker.interfaces.Recorder;

import java.io.File;
import java.io.IOException;

//
// Created by  on 1/23/2021.
//
public class RecorderImpl implements Recorder {

    private static RecorderImpl instance;
    private MediaRecorder recorder;
    private MediaPlayer player;
    private Context context;
    private String filePath;

    public RecorderImpl(Context context) {
        this.context = context;
    }

    /*public static RecorderImpl getInstance() {
        if (instance  == null) {
            instance = new RecorderImpl();
        }
        return instance;
    }*/

    @Override
    public void onPlay(boolean start) {
        if (start) {
            startPlaying();
        }else {
            stopPlaying();
        }
    }

    @Override
    public void onRecord(boolean start) {
        if (start) {
            filePath = startRecording();
        }else {
            stopRecording();
        }
    }

    @Override
    public void onPause() {
        player.pause();
    }

    @Override
    public void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(startRecording());
            player.prepareAsync();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopPlaying() {
        player.release();
        player = null;
    }

    @Override
    public String startRecording() {
        String filePath = getFile();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(filePath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();

        return filePath;
    }

    @Override
    public void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    @Override
    public void stop() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }

    String getFile() {
        String filePath;
        File root = context.getExternalFilesDir(null);
        String fileName = "audio_" + NoteUtil.getInstance().generateTimeStamp() + ".amr";
        File file = new File(root, fileName);
        if (file.exists()) {
            file.delete();
        }
        filePath = file.getPath();
        return filePath;
    }

    public int getAudioSessionId() {
        if (player != null) {
            return player.getAudioSessionId();
        }
        return 0;
    }

    public int getDuration() {
        return player.getDuration();
    }

    public String getFilePath() {
        return filePath;
    }
}
