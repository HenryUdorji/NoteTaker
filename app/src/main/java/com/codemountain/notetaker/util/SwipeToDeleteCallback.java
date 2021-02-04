package com.codemountain.notetaker.util;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

//
// Created by Henry on 1/22/2021.
// Copyright (c) 2021 codemountainInc. All rights reserved.
//
class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    public SwipeToDeleteCallback(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }
}
