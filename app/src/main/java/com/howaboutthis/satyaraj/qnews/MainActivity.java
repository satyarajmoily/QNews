package com.howaboutthis.satyaraj.qnews;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.howaboutthis.satyaraj.qnews.data.NewsContract.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<List<ListItemEvent>> {

    ContentValues[] values = {null};
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    SharedPreferences shared_preferences;
    String firstOpen = null;
    List<ListItemEvent> news;
    int loader = 0;
    int headerImage;

    SharedPreferences.Editor shared_preferences_editor;
    private List<ListItemEvent> mListItem;
    private static String NEWS_REQUEST_URL = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView =  findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mListItem = new ArrayList<>();

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        shared_preferences = getSharedPreferences("shared_preferences", MODE_PRIVATE);
        firstOpen = shared_preferences.getString("firstOpen", null);

        if (firstOpen == null) {
            setUrl("bbc-news","top");
            headerImage = R.drawable.bbc;
            getLoaderManager().initLoader(0, null, this);
            loader = 0;
        }

        else {
            NEWS_REQUEST_URL = shared_preferences.getString("lastUrl",null);
            headerImage = shared_preferences.getInt("headerImage",0);

            String[] projection = {
                    NewsEntry._ID,
                    NewsEntry.COLUMN_NEWS_TITLE,
                    NewsEntry.COLUMN_NEWS_DESCRIPTION,
                    NewsEntry.COLUMN_NEWS_URL,
                    NewsEntry.COLUMN_NEWS_TIME
            };

            // This loader will execute the ContentProvider's query method on a background thread
            CursorLoader cursorLoader = new CursorLoader(this,   // Parent activity context
                    NewsEntry.CONTENT_URI,         // Query the content URI for the current pet
                    projection,             // Columns to include in the resulting Cursor
                    null,                   // No selection clause
                    null,                   // No selection arguments
                    null);

            Cursor cursor = cursorLoader.loadInBackground();

            ListItemEvent mlistItem;
            while (cursor.moveToNext()) {
                int titleColumnIndex = cursor.getColumnIndex(NewsEntry.COLUMN_NEWS_TITLE);
                int descriptionColumnIndex = cursor.getColumnIndex(NewsEntry.COLUMN_NEWS_DESCRIPTION);
                int urlColumnIndex = cursor.getColumnIndex(NewsEntry.COLUMN_NEWS_URL);
                int timeColumnIndex = cursor.getColumnIndex(NewsEntry.COLUMN_NEWS_TIME);

                // Extract out the value from the Cursor for the given column index
                String title = cursor.getString(titleColumnIndex);
                String description = cursor.getString(descriptionColumnIndex);
                String url = cursor.getString(urlColumnIndex);
                long time = cursor.getLong(timeColumnIndex);

                mlistItem = new ListItemEvent(title, description, url, time);

                mListItem.add(mlistItem);
            }

            mAdapter = new NewsAdapter(mListItem, this,headerImage);
            mRecyclerView.setAdapter(mAdapter);


            if (isConnected()) {
                getLoaderManager().initLoader(0, null, this);
                loader = 0;
            }
            else
                Snackbar.make(mRecyclerView,"OOPS, NO INTERNET CONNECTION", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            cursor.close();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
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

        if (id == R.id.action_about) {
            Intent intent = new Intent(MainActivity.this,aboutActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.bbc_news) {
            if (isConnected()) {
                setUrl("bbc-news", "top");
                getLoaderManager().destroyLoader(loader);
                headerImage = R.drawable.bbc;
                getLoaderManager().initLoader(1, null, this);
                loader = 1;
            }else
                showSnackBar();
        }
        else if (id == R.id.cnn){
            if (isConnected()) {
                setUrl("cnn", "top");
                getLoaderManager().destroyLoader(loader);
                headerImage = R.drawable.cnn;
                getLoaderManager().initLoader(2, null, this);
                loader = 2;
            }else
                showSnackBar();
        }
        else if (id == R.id.al_jazeera){
            if (isConnected()) {
                setUrl("al-jazeera-english", "top");
                getLoaderManager().destroyLoader(loader);
                headerImage = R.drawable.al_jazeera;
                getLoaderManager().initLoader(3, null, this);
                loader = 3;
            }else
                showSnackBar();

        }
        else if (id == R.id.google_news){
            if (isConnected()) {
                setUrl("google-news", "top");
                getLoaderManager().destroyLoader(loader);
                headerImage = R.drawable.google;
                getLoaderManager().initLoader(4, null, this);
                loader = 4;
            }else
                showSnackBar();
        }
        else if (id == R.id.the_hindu){
            if (isConnected()) {
                setUrl("the-hindu", "top");
                getLoaderManager().destroyLoader(loader);
                headerImage = R.drawable.thehindu;
                getLoaderManager().initLoader(5, null, this);
                loader = 5;
            }else
                showSnackBar();
        }
        else if (id == R.id.news_york){
            if (isConnected()) {
                setUrl("the-new-york-times", "top");
                getLoaderManager().destroyLoader(loader);
                headerImage = R.drawable.nytimes;
                getLoaderManager().initLoader(6, null, this);
                loader = 6;
            }else
                showSnackBar();
        }
        else if (id == R.id.times_of_india){
            if (isConnected()) {
                setUrl("the-times-of-india", "top");
                getLoaderManager().destroyLoader(loader);
                headerImage = R.drawable.toi;
                getLoaderManager().initLoader(7, null, this);
                loader = 7;
            }else
                showSnackBar();

        }else if (id == R.id.usa_today){
            if (isConnected()) {
                setUrl("usa-today", "top");
                getLoaderManager().destroyLoader(loader);
                headerImage = R.drawable.usatoday;
                getLoaderManager().initLoader(8, null, this);
                loader = 8;
            }else
                showSnackBar();

        }else if (id == R.id.bbc_sports){
            if (isConnected()) {
                setUrl("bbc-sport", "top");
                getLoaderManager().destroyLoader(loader);
                headerImage = R.drawable.bbb_sports;
                getLoaderManager().initLoader(9, null, this);
                loader = 9;
            }else
                showSnackBar();

        }else if (id == R.id.espn){
            if (isConnected()) {
                setUrl("espn", "top");
                getLoaderManager().destroyLoader(loader);
                headerImage = R.drawable.espn;
                getLoaderManager().initLoader(10, null, this);
                loader = 10;
            }else
                showSnackBar();

        }else if (id == R.id.football_italia){
            if (isConnected()) {
                setUrl("football-italia", "top");
                getLoaderManager().destroyLoader(loader);
                headerImage = R.drawable.football_italia;
                getLoaderManager().initLoader(11, null, this);
                loader = 11;
            }else
                showSnackBar();

        }else if (id == R.id.fox_sports){
            if (isConnected()) {
                setUrl("fox-sports", "top");
                getLoaderManager().destroyLoader(loader);
                headerImage = R.drawable.fox_sports;
                getLoaderManager().initLoader(12, null, this);
                loader = 12;
            }else
                showSnackBar();

        } else if (id == R.id.talksports){
            if (isConnected()) {
                setUrl("talksport", "top");
                getLoaderManager().destroyLoader(loader);
                headerImage = R.drawable.talksports;
                getLoaderManager().initLoader(13, null, this);
                loader = 13;
            }else
                showSnackBar();

        }else if (id == R.id.bloomberg){
            if (isConnected()) {
                setUrl("bloomberg", "top");
                getLoaderManager().destroyLoader(loader);
                headerImage = R.drawable.bloomberg;
                getLoaderManager().initLoader(14, null, this);
                loader = 14;
            }else
                showSnackBar();

        }else if (id == R.id.business_insider){
            if (isConnected()) {
                setUrl("business-insider", "top");
                getLoaderManager().destroyLoader(loader);
                headerImage = R.drawable.businessinsider;
                getLoaderManager().initLoader(15, null, this);
                loader = 15;
            }else
                showSnackBar();

        }else if (id == R.id.cnbc){
            if (isConnected()) {
                setUrl("cnbc", "top");
                getLoaderManager().destroyLoader(loader);
                headerImage = R.drawable.cnbc;
                getLoaderManager().initLoader(16, null, this);
                loader = 16;
            }else
                showSnackBar();

        }else if (id == R.id.financial_news){
            if (isConnected()) {
                setUrl("financial-times", "top");
                getLoaderManager().destroyLoader(loader);
                headerImage = R.drawable.financialtimes;
                getLoaderManager().initLoader(17, null, this);
                loader = 17;
            }else
                showSnackBar();

        }else if (id == R.id.wall_street){
            if (isConnected()) {
                setUrl("the-wall-street-journal", "top");
                getLoaderManager().destroyLoader(loader);
                headerImage = R.drawable.wall_street;
                getLoaderManager().initLoader(18, null, this);
                loader = 18;
            }else
                showSnackBar();

        }else if (id == R.id.ars_technica){
            if (isConnected()) {
                setUrl("ars-technica", "top");
                getLoaderManager().destroyLoader(loader);
                headerImage = R.drawable.ars_technica;
                getLoaderManager().initLoader(19, null, this);
                loader = 19;
            }else
                showSnackBar();

        }else if (id == R.id.engadget){
            if (isConnected()) {
                setUrl("engadget", "top");
                getLoaderManager().destroyLoader(loader);
                headerImage = R.drawable.engadget;
                getLoaderManager().initLoader(20, null, this);
                loader = 20;
            }else
                showSnackBar();

        }else if (id == R.id.recode){
            if (isConnected()) {
                setUrl("recode", "top");
                getLoaderManager().destroyLoader(loader);
                headerImage = R.drawable.recode;
                getLoaderManager().initLoader(21, null, this);
                loader = 21;
            }else
                showSnackBar();

        }else if (id == R.id.tech_radar){
            if (isConnected()) {
                setUrl("techradar", "top");
                getLoaderManager().destroyLoader(loader);
                headerImage = R.drawable.techradar;
                getLoaderManager().initLoader(22, null, this);
                loader = 22;
            }else
                showSnackBar();
        }
        else if (id == R.id.the_verge){
            if (isConnected()) {
                getLoaderManager().destroyLoader(loader);
                headerImage = R.drawable.verge;
                setUrl("the-verge", "top");
                getLoaderManager().initLoader(23, null, this);
                loader = 23;
            }else
                showSnackBar();
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public Loader<List<ListItemEvent>> onCreateLoader(int i, Bundle bundle) {
        return new NewsLoader(this, NEWS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<ListItemEvent>> loader, final List<ListItemEvent> news) {
        mListItem.clear();
        this.news = news;

        String firstNews ;

        if (news != null && !news.isEmpty()) {

            mListItem.addAll(news);
            mAdapter = new NewsAdapter(mListItem, this,headerImage);
            mRecyclerView.setAdapter(mAdapter);

            ListItemEvent firstNewsPosition = news.get(0);
            firstNews = firstNewsPosition.getHeading();

            shared_preferences = getSharedPreferences("shared_preferences", MODE_PRIVATE);
            String oldNews = shared_preferences.getString("oldNews", null);
            firstOpen = shared_preferences.getString("firstOpen", null);

            if (!Objects.equals(firstNews, oldNews) || Objects.equals(firstOpen, null)) {
                getContentResolver().delete(NewsEntry.CONTENT_URI, null, null);

                if(Objects.equals(firstOpen,null)) {
                    shared_preferences_editor = shared_preferences.edit();
                    shared_preferences_editor.putString("firstOpen", "false");
                    shared_preferences_editor.apply();
                }
                        for (int i = 0; i < news.size(); i++) {

                            ListItemEvent position = news.get(i);
                            values[0] = new ContentValues();
                            values[0].put(NewsEntry.COLUMN_NEWS_TITLE, position.getHeading());
                            values[0].put(NewsEntry.COLUMN_NEWS_DESCRIPTION, position.getDescription());
                            values[0].put(NewsEntry.COLUMN_NEWS_URL, position.getUrl());
                            values[0].put(NewsEntry.COLUMN_NEWS_TIME,position.getTime());
                            getContentResolver().insert(NewsEntry.CONTENT_URI, values[0]);
                        }
                values[0].clear();
                shared_preferences_editor = shared_preferences.edit();
                shared_preferences_editor.putString("oldNews", firstNews);
                shared_preferences_editor.putString("lastUrl",NEWS_REQUEST_URL);
                shared_preferences_editor.putInt("headerImage",headerImage);
                shared_preferences_editor.apply();
                }
        }

    }

    @Override
    public void onLoaderReset(Loader<List<ListItemEvent>> loader) {
               mListItem.clear();

    }

    public void setUrl(String channel,String sort){
        NEWS_REQUEST_URL = "https://newsapi.org/v1/articles?source="+channel+"&sortBy="+sort+"&apiKey=dc3681f5c27e4eab9756af49a1cecec1";
    }


    public boolean isConnected(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isWifi = false;
        boolean isMobile = false;
        if(activeNetworkInfo != null && activeNetworkInfo.isConnected())
        {
            isWifi = activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            isMobile = activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return isMobile || isWifi;
    }
    public void showSnackBar(){
        Snackbar.make(mRecyclerView,"OOPS, NO INTERNET CONNECTION", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }


}
