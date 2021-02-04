package com.codemountain.notetaker.database;

import android.app.Application;
import android.content.Context;

import com.codemountain.notetaker.entity.Note;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/******
 * Created by  on 1/19/2021.
 * Copyright (c) 2021 codemountainInc. All rights reserved.
 * */
public class NoteRepository {
    private NoteDao noteDao;
    private Observable<List<Note>> notes;


    public NoteRepository(Context context) {
        noteDao = NoteDatabase.getInstance(context).noteDao();
        notes = noteDao.getAllNotes();
    }

    public Completable insertNote(Note note) {
        return noteDao.insertNote(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable updateNote(Note note) {
        return noteDao.updateNote(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteNote(Note note) {
        return noteDao.deleteNote(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteAllNote() {
        return noteDao.deleteAllNotes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

   /* public Single<Note> getNote(int id) {
        return noteDao.getNote(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }*/

    public Observable<List<Note>> getAllNotes() {
        return notes
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
