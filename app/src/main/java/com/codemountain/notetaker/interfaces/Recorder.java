package com.codemountain.notetaker.interfaces;

//
// Created by  on 1/23/2021.
//
public interface Recorder {

    void onPlay(boolean start);

    void onRecord(boolean start);

    void onPause();

    void startPlaying();

    void stopPlaying();

    String startRecording();

    void stopRecording();

    void stop();
}
