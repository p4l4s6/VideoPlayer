package com.mahainc.videoplayer.activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.mahainc.videoplayer.models.MusicModel;
import com.mahainc.videoplayer.utils.TempMusicList;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayerActivity extends AppCompatActivity {
    List<MusicModel> musicList;

    PlayerView playerView;
    SimpleExoPlayer player;
    PlayerNotificationManager playerNotificationManager;
    int music_id;
    int play_count = 0;
    private AdView mAdView;
    private InterstitialAd interstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Music Player");
        setContentView(R.layout.activity_music_player);
        MobileAds.initialize(this, getString(R.string.admob_app_id));

        musicList = new ArrayList<>();
        musicList = TempMusicList.getInstance().getData();

        playerView = findViewById(R.id.exo_audio_player);
        PlayerControlView controls = findViewById(R.id.controls);

        music_id = getIntent().getIntExtra("music_id", 0);
        playerView.setControllerHideOnTouch(false);
        playerView.setShowShuffleButton(true);
        playerView.setDefaultArtwork(getApplicationContext().getResources().getDrawable(R.drawable.default_artwork));


        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
        controls.setPlayer(player);


        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(
                        this, Util.getUserAgent(this, "Music Player"), null);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        MediaSource[] mediaSources = new MediaSource[musicList.size()];
        for (int i = 0; i < mediaSources.length; i++) {
            String s = musicList.get(i).getPath();
            mediaSources[i] = new ExtractorMediaSource(Uri.parse(s), dataSourceFactory, extractorsFactory, null, null);
        }
        MediaSource mediaSource = mediaSources.length == 1 ? mediaSources[0]
                : new ConcatenatingMediaSource(mediaSources);

        player.prepare(mediaSource);
        player.seekTo(music_id, C.TIME_UNSET);
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

        /*final NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(this,"exo_test",
                R.string.noti_id,
                5000,
                new PlayerNotificationManager.MediaDescriptionAdapter() {
                    @Override
                    public String getCurrentContentTitle(Player player) {
                        int track_number=0;
                        for (int i=0;i<musicList.size();i++) {
                            if (musicList.get(i).getId()==music_id) {
                                track_number=i;
                            }
                        }
                        return null;
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player) {
                        Intent intent = new Intent(getApplicationContext(), MusicPlayerActivity.class);
                        intent.putExtra("music_id",player.getCurrentWindowIndex());
                        return PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    @Nullable
                    @Override
                    public String getCurrentContentText(Player player) {
                        int track_number=0;
                        for (int i=0;i<musicList.size();i++) {
                            if (musicList.get(i).getId()==music_id) {
                                track_number=i;
                            }
                        }
                        return null;
                    }

                    @Nullable
                    @Override
                    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                        return null;
                    }
                }
        );
        playerNotificationManager.setNotificationListener(new PlayerNotificationManager.NotificationListener() {
            @Override
            public void onNotificationStarted(int notificationId, Notification notification) {
                mNotificationManager.notify(notificationId,notification);
            }

            @Override
            public void onNotificationCancelled(int notificationId) {
                mNotificationManager.cancel(notificationId);
            }
        });
        playerNotificationManager.setPlayer(player);*/
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
