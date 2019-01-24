package com.mahainc.videoplayer.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.mahainc.videoplayer.R;
import com.mahainc.videoplayer.models.VideoModel;
import com.mahainc.videoplayer.utils.TempVideoList;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cr4ck3r
 * Date: 12/6/18
 * Owner: Raisul Islam
 * Copyright (c) 2018 . All rights reserved.
 */
public class VideoPlayerActivity extends Activity {

    PlayerView playerView;
    SimpleExoPlayer player;
    List<VideoModel> video_list;
    int play_count = 0;

    private AdView mAdView;
    private InterstitialAd interstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_player_activity);

        MobileAds.initialize(this, getString(R.string.admob_app_id));

        Intent intent = getIntent();
        int video_id = intent.getIntExtra("video_id", 0);
        video_list = new ArrayList<>();
        video_list = TempVideoList.getInstance().getData();

        playerView = findViewById(R.id.player);
        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getPackageName()));
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();


        MediaSource[] mediaSources = new MediaSource[video_list.size()];
        for (int i = 0; i < mediaSources.length; i++) {
            String s = video_list.get(i).getPath();
            mediaSources[i] = new ExtractorMediaSource(Uri.parse(s), dataSourceFactory, extractorsFactory, null, null);
        }
        MediaSource mediaSource = mediaSources.length == 1 ? mediaSources[0]
                : new ConcatenatingMediaSource(mediaSources);

        player.prepare(mediaSource);
        player.seekTo(video_id, C.TIME_UNSET);
        player.setPlayWhenReady(true);
        player.addListener(new Player.EventListener() {
            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                play_count++;
                if (play_count % 5 == 0) {
                    if (interstitial.isLoaded()) {
                        interstitial.show();
                        interstitial.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                AdRequest adRequest = new AdRequest.Builder()
                                        .build();
                                interstitial.loadAd(adRequest);
                            }
                        });
                    }
                }

            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        adShow();
    }

    public void adShow() {
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        interstitial.loadAd(adRequest);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }


}
