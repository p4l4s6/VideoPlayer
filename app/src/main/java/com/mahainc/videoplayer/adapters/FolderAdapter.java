package com.mahainc.videoplayer.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mahainc.videoplayer.R;
import com.mahainc.videoplayer.activities.VideosList;
import com.mahainc.videoplayer.models.FolderModel;

import java.util.List;

/**
 * Created by cr4ck3r
 * Date: 12/7/18
 * Owner: Raisul Islam
 * Copyright (c) 2018 . All rights reserved.
 */
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {

    private Context mContext;
    private List<FolderModel> folderModelList;

    public FolderAdapter(Context mContext, List<FolderModel> folderModelList) {
        this.mContext = mContext;
        this.folderModelList = folderModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.folder_item_layout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final FolderModel folder = folderModelList.get(i);
        viewHolder.title.setText(folder.getName());
        viewHolder.folderCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, VideosList.class);
                intent.putExtra("folder_path", folder.getPath());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return folderModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView folderCard;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.folder_title);
            folderCard = itemView.findViewById(R.id.folder_card);
        }
    }
}
