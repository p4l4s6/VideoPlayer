package com.mahainc.videoplayer.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mahainc.videoplayer.R;
import com.mahainc.videoplayer.adapters.FolderAdapter;
import com.mahainc.videoplayer.models.FolderModel;
import com.mahainc.videoplayer.utils.MyUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFolders extends Fragment {

    private RecyclerView recyclerView;
    private FolderAdapter adapter;
    private List<FolderModel> foldersList;
    private List<String> folderpath;


    public VideoFolders() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.activity_videos_list, container, false);

        foldersList = new ArrayList<>();
        folderpath = new ArrayList<>();

        recyclerView = v.findViewById(R.id.videoRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        parseAllVideo();
        getUniqueFolders();
        adapter = new FolderAdapter(getContext(), foldersList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return v;
    }

    private void parseAllVideo() {
        try {
            String[] proj = {MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DATA};
            Cursor videocursor = getContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    proj, null, null, null);
            if (videocursor != null) {
                if (videocursor.moveToFirst()) {
                    int path_index = videocursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                    do {
                        String filepath = videocursor.getString(path_index);
                        String folder_path = MyUtils.getParentDirPath(filepath);
                        folderpath.add(folder_path);
                    } while (videocursor.moveToNext());
                }
                videocursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUniqueFolders() {
        List<String> newList = new ArrayList<>(new HashSet<>(folderpath));
        Collections.sort(newList);
        for (int i = 0; i < newList.size(); i++) {
            String folder_name = newList.get(i).substring(newList.get(i).lastIndexOf("/") + 1);
            FolderModel folder = new FolderModel(folder_name, newList.get(i));
            foldersList.add(folder);
        }

    }

}
