package com.codemountain.notetaker.database;

//
// Created by Henry on 1/19/2021.
// Copyright (c) 2021 codemountainInc. All rights reserved.
//

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.codemountain.notetaker.entity.Note;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;


@Dao
public interface NoteDao {

    @Insert
    Completable insertNote(Note note);

    @Delete
    Completable deleteNote(Note note);

    @Update
    Completable updateNote(Note note);

    /*@Query("SELECT * FROM  notes WHERE id = :id")
    Single<Note> getNote(id);*/

    @Query("SELECT * FROM notes ORDER BY id DESC")
    Observable<List<Note>> getAllNotes();

    @Query("DELETE FROM notes")
    Completable deleteAllNotes();

}
