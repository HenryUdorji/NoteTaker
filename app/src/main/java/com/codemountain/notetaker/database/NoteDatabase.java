package com.codemountain.notetaker.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.codemountain.notetaker.entity.Note;

//
// Created by Henry on 1/19/2021.
// Copyright (c) 2021 codemountainInc. All rights reserved.
//

@Database(
        entities = {Note.class},
        version = 1,
        exportSchema = true
)
public abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase instance;
    public abstract NoteDao noteDao();

    public static synchronized NoteDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDatabase.class,
                    "notes")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}
