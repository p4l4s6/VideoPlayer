package com.mahainc.videoplayer.utils;

import com.mahainc.videoplayer.models.VideoModel;

import java.util.List;

/**
 * Created by cr4ck3r
 * Date: 12/8/18
 * Owner: Raisul Islam
 * Copyright (c) 2018 . All rights reserved.
 */
public class TempVideoList {
    private static TempVideoList instance;
    private List<VideoModel> data = null;

    protected TempVideoList() {

    }

    public static TempVideoList getInstance() {
        if (instance == null) {
            instance = new TempVideoList();
        }
        return instance;
    }

    public List<VideoModel> getData() {
        return data;
    }

    public void setData(List<VideoModel> data) {
        this.data = data;
    }
}
