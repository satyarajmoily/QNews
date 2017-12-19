package com.howaboutthis.satyaraj.qnews;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<ListItemEvent>> {

    private static final String LOG_TAG = NewsLoader.class.getName();

    private String mUrl;

    NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading(){
        forceLoad();
    }

    @Override
    public List<ListItemEvent> loadInBackground() {
        if(mUrl==null)
        return null;

        return Utils.fetchNewsData(mUrl);
    }
}
