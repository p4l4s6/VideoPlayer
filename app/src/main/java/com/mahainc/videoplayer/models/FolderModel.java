package com.mahainc.videoplayer.models;

/**
 * Created by cr4ck3r
 * Date: 12/7/18
 * Owner: Raisul Islam
 * Copyright (c) 2018 . All rights reserved.
 */
public class FolderModel {
    private String name;
    private String path;

    public FolderModel(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
