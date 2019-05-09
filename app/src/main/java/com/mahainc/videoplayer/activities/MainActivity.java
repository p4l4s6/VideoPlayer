package com.mahainc.videoplayer.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.mahainc.videoplayer.R;
import com.mahainc.videoplayer.adapters.VideoAdapter;
import com.mahainc.videoplayer.fragments.AllVideos;
import com.mahainc.videoplayer.fragments.VideoFolders;
import com.mahainc.videoplayer.models.VideoModel;
import com.mahainc.videoplayer.utils.MyUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSION_REQUEST_CODE = 10;
    private ViewPager vp_pages;
    private AdView mAdView;
    private InterstitialAd interstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MobileAds.initialize(this, getString(R.string.admob_app_id));


        vp_pages = (ViewPager) findViewById(R.id.TabViewPager);
        PagerAdapter pagerAdapter = new FragmentAdapter(getSupportFragmentManager());
        vp_pages.setAdapter(pagerAdapter);

        TabLayout tbl_pages = (TabLayout) findViewById(R.id.TabButtonBar);
        tbl_pages.setupWithViewPager(vp_pages);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        adShow();
        permissionHandle();
    }

    public void adShow() {
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        interstitial.loadAd(adRequest);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_music) {
            Intent intent = new Intent(MainActivity.this, MusicActivity.class);
            showLargeAdBeforeLoadingActivity(intent);
        } else if (id == R.id.nav_more) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.publisher_account))));
        } else if (id == R.id.nav_feedback) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            showLargeAdBeforeLoadingActivity(intent);
        } else if (id == R.id.nav_share) {
            shareApp();
        } else if (id == R.id.nav_privacy_policy) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url)));
            startActivity(browserIntent);
        } else if (id == R.id.nav_rate) {
            showRating();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showLargeAdBeforeLoadingActivity(final Intent intent) {
        if (interstitial.isLoaded()) {
            interstitial.show();
            interstitial.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    AdRequest adRequest = new AdRequest.Builder()
                            .build();
                    interstitial.loadAd(adRequest);
                    startActivity(intent);
                }
            });
        } else {
            startActivity(intent);
        }
    }

    public void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out the App at: " + getString(R.string.app_link));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void showRating() {
        Uri uri = Uri.parse(getString(R.string.rate_app_url));
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.app_link))));
        }
    }

    public void feedback() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"maahainc@gmail.com"});
        i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Video Player");
        startActivity(Intent.createChooser(i, "Send email"));
    }

    private void permissionHandle() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                Log.e("value", "Permission Granted");
            } else {
                requestPermission();
            }
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Please allow storage permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted");
                    recreate();
                } else {
                    Toast.makeText(MainActivity.this, "Please allow storage permission in App Settings.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    class FragmentAdapter extends FragmentPagerAdapter {

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new AllVideos();
                case 1:
                    return new VideoFolders();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                //
                //Your tab titles
                //
                case 0:
                    return "All Videos";
                case 1:
                    return "Folders";
                default:
                    return null;
            }
        }
    }


}