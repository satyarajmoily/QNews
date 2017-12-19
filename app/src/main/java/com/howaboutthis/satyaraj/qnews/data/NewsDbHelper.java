package com.howaboutthis.satyaraj.qnews.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.howaboutthis.satyaraj.qnews.data.NewsContract.*;

public class NewsDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "news.db";

    private static final int DATABASE_VERSION = 1;

    NewsDbHelper(Context context) {
       super(context,DATABASE_NAME,null,DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_NEWS_TABLE = "CREATE TABLE " + NewsEntry.TABLE_NAME + "("
                + NewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NewsEntry.COLUMN_NEWS_TITLE + " TEXT NOT NULL, "
                + NewsEntry.COLUMN_NEWS_DESCRIPTION + " TEXT, "
                + NewsEntry.COLUMN_NEWS_URL +" TEXT, "
                + NewsEntry.COLUMN_NEWS_TIME +" INTEGER " + ")";

        db.execSQL(SQL_CREATE_NEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
