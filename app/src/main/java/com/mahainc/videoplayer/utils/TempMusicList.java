package com.mahainc.videoplayer.utils;

import com.mahainc.videoplayer.models.MusicModel;

import java.util.List;

/**
 * Created by cr4ck3r
 * Date: 12/8/18
 * Owner: Raisul Islam
 * Copyright (c) 2018 . All rights reserved.
 */
public class TempMusicList {
    private static TempMusicList instance;
    private List<MusicModel> data = null;

    protected TempMusicList() {

    }

    public static TempMusicList getInstance() {
        if (instance == null) {
            instance = new TempMusicList();
        }
        return instance;
    }

    public List<MusicModel> getData() {
        return data;
    }

    public void setData(List<MusicModel> data) {
        this.data = data;
    }
}
