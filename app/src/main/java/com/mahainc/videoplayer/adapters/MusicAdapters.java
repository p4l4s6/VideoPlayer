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
import com.mahainc.videoplayer.activities.MusicPlayerActivity;
import com.mahainc.videoplayer.models.MusicModel;
import com.mahainc.videoplayer.utils.TempMusicList;

import java.util.List;

/**
 * Created by cr4ck3r
 * Date: 12/7/18
 * Owner: Raisul Islam
 * Copyright (c) 2018 . All rights reserved.
 */
public class MusicAdapters extends RecyclerView.Adapter<MusicAdapters.ViewHolder> {

    private Context mContext;
    private List<MusicModel> AudioList;

    public MusicAdapters(Context mContext, List<MusicModel> AudiolList) {
        this.mContext = mContext;
        this.AudioList = AudiolList;
    }

    @NonNull
    @Override
    public MusicAdapters.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.music_single_item, viewGroup, false);
        return new MusicAdapters.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapters.ViewHolder viewHolder, int i) {
        final MusicModel Audio = AudioList.get(i);
        final int subposition = i;

        viewHolder.title.setText(Audio.getTitle());
        viewHolder.duration.setText(Audio.getDuration());
        viewHolder.AudiooCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TempMusicList.getInstance().setData(AudioList);
                Intent intent = new Intent(mContext, MusicPlayerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("music_id", subposition);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return AudioList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, duration;
        CardView AudiooCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.audio_title);
            duration = itemView.findViewById(R.id.audio_duration);
            AudiooCard = itemView.findViewById(R.id.audio_card);
        }
    }
}
