package com.mahainc.videoplayer.activities;

import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.mahainc.videoplayer.R;
import com.mahainc.videoplayer.adapters.MusicAdapters;
import com.mahainc.videoplayer.models.MusicModel;
import com.mahainc.videoplayer.utils.MyUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MusicActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private AdView mAdView;
    private InterstitialAd interstitial;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        MobileAds.initialize(this, getString(R.string.admob_app_id));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_music, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            shareApp();
        }

        return super.onOptionsItemSelected(item);
    }

    public void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out the App at: " + getString(R.string.app_link));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        List<MusicModel> musicModelList;
        RecyclerView recyclerView;
        MusicAdapters adapter;

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            View rootView = inflater.inflate(R.layout.fragment_music, container, false);
            int id = getArguments().getInt(ARG_SECTION_NUMBER, 0);
            musicModelList = new ArrayList<>();
            recyclerView = rootView.findViewById(R.id.musicRecycler);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            parseAllVideo();
            sortList(id);
            adapter = new MusicAdapters(getContext(), musicModelList);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            return rootView;
        }


        private void parseAllVideo() {
            try {
                String[] proj = {MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ARTIST,
                };
                Cursor audiocursor = getContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        proj, null, null, null);
                if (audiocursor != null) {
                    if (audiocursor.moveToFirst()) {
                        int name_index = audiocursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                        int duration_index = audiocursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
                        int path_index = audiocursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                        int album_index = audiocursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
                        int artist_index = audiocursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                        int music_id = 0;
                        do {
                            String name = audiocursor.getString(name_index);
                            String filepath = audiocursor.getString(path_index);
                            String duration = MyUtils.milisecondToHour(audiocursor.getLong(duration_index));
                            String album = audiocursor.getString(album_index);
                            String artist = audiocursor.getString(artist_index);
                            MusicModel audio = new MusicModel();
                            audio.setId(music_id);
                            audio.setTitle(name);
                            audio.setDuration(duration);
                            audio.setAlbum(album);
                            audio.setArtist(artist);
                            audio.setPath(filepath);
                            musicModelList.add(audio);
                            music_id++;
                        } while (audiocursor.moveToNext());
                    }
                    audiocursor.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void sortList(int n) {
            switch (n) {
                case 2:
                    Collections.sort(musicModelList, new Comparator<MusicModel>() {
                        @Override
                        public int compare(MusicModel m1, MusicModel m2) {
                            return m1.getAlbum().compareTo(m2.getAlbum());
                        }
                    });
                    break;
                case 3:
                    Collections.sort(musicModelList, new Comparator<MusicModel>() {
                        @Override
                        public int compare(MusicModel m1, MusicModel m2) {
                            return m1.getArtist().compareTo(m2.getArtist());
                        }
                    });
                    break;
                default:
                    return;
            }
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }


}
