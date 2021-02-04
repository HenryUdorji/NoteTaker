package com.codemountain.notetaker.entity;

//
// Created by  on 1/19/2021.
//

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Date;


@Entity(tableName = "notes")
public class Note {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    private String title;

    private String content;

    private String date;

    @ColumnInfo(name = "image_path")
    private String imagePath;

    @ColumnInfo(name = "audio_path")
    private String audioPath;

    public Note() {
    }

    public Note(String title, String content, String date, String imagePath, String audioPath) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.imagePath = imagePath;
        this.audioPath = audioPath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }
}
