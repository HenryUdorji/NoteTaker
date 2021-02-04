package com.codemountain.notetaker.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.codemountain.notetaker.entity.Note;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import pub.devrel.easypermissions.EasyPermissions;

//
// Created by Henry on 1/20/2021.
// Copyright (c) 2021 codemoutainInc. All rights reserved.
//
public class NoteUtil {
    private static final String TAG = "NoteUtil";
    private static NoteUtil instance;

    public static NoteUtil getInstance() {
        if (instance  == null) {
            instance = new NoteUtil();
        }
        return instance;
    }

    public int getRandomNoteColor(Context context) {
        int noteColor = Color.BLUE;
        int identifier = context.getResources()
                .getIdentifier("mdcolor_500", "array", context.getPackageName());

        if (identifier != 0) {
            TypedArray colors = context.getResources().obtainTypedArray(identifier);
            int index = (int) (Math.random() * colors.length());
            noteColor = colors.getColor(index, Color.BLUE);
            colors.recycle();
        }
        return noteColor;
    }

    public String saveImageToFile(Context context, Bitmap bitmap) {
        String filePath = null;
        File root = context.getExternalFilesDir(null);
        String fileName = "image_" + generateTimeStamp() + ".jpg";
        File file = new File(root, fileName);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            filePath = file.getPath();
            outputStream.flush();;
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePath;
    }

    public void deleteFileFromPath(String filePath) {
        File file = new File(filePath);
        file.delete();
    }

    public String generateTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
        return sdf.format(new Date());
    }

    public static String calculateDuration(long id) {
        String finalTimerString = "";
        String secondsString = "";
        String mp3Minutes = "";
        // Convert total duration into time

        int minutes = (int) (id % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((id % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        if (minutes < 10) {
            mp3Minutes = "0" + minutes;
        } else {
            mp3Minutes = "" + minutes;
        }
        finalTimerString = finalTimerString + mp3Minutes + ":" + secondsString;
        // return timer string
        return finalTimerString;
    }

}
