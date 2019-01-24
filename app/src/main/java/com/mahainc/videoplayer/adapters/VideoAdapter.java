package com.mahainc.videoplayer.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mahainc.videoplayer.R;
import com.mahainc.videoplayer.activities.VideoPlayerActivity;
import com.mahainc.videoplayer.models.VideoModel;
import com.mahainc.videoplayer.utils.TempVideoList;

import java.io.File;
import java.util.List;

/**
 * Created by cr4ck3r
 * Date: 12/6/18
 * Owner: Raisul Islam
 * Copyright (c) 2018 . All rights reserved.
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private Context mContext;
    private List<VideoModel> videoList;

    public VideoAdapter(Context mContext, List<VideoModel> videolList) {
        this.mContext = mContext;
        this.videoList = videolList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.video_single_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final VideoModel video = videoList.get(i);
        Glide.with(mContext)
                .load(Uri.fromFile(new File(video.getPath())))
                .into(viewHolder.thumb);


        viewHolder.title.setText(video.getName());
        viewHolder.duration.setText(video.getDuration());
        viewHolder.VideoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TempVideoList.getInstance().setData(videoList);
                Intent intent = new Intent(mContext, VideoPlayerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("video_id", video.getId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumb;
        TextView title, duration;
        CardView VideoCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumb = itemView.findViewById(R.id.video_thumb);
            title = itemView.findViewById(R.id.video_title);
            duration = itemView.findViewById(R.id.video_duration);
            VideoCard = itemView.findViewById(R.id.video_card);
        }
    }
}
