package com.howaboutthis.satyaraj.qnews.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class NewsProvider extends ContentProvider {

    public static final String LOG_TAG = NewsProvider.class.getSimpleName();
    private NewsDbHelper mDbHelper;
    private static final int NEWS = 100;
    private static final int NEWS_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(NewsContract.CONTENT_AUTHORITY,NewsContract.PATH_NEWS,NEWS);

        sUriMatcher.addURI(NewsContract.CONTENT_AUTHORITY,NewsContract.PATH_NEWS+"/#",NEWS_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new NewsDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match){
            case NEWS:
                cursor = database.query(NewsContract.NewsEntry.TABLE_NAME, projection,selection,selectionArgs,null,null,sortOrder);
                break;

            case NEWS_ID:
                selection = NewsContract.NewsEntry._ID+"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(NewsContract.NewsEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
        break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI "+uri);
        }
        //noinspection ConstantConditions
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case NEWS:
                return NewsContract.NewsEntry.CONTENT_LIST_TYPE;

            case NEWS_ID:
                return NewsContract.NewsEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(NewsContract.NewsEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        //noinspection ConstantConditions
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted = 0;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NEWS:
                rowsDeleted = database.delete(NewsContract.NewsEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case NEWS_ID:
                rowsDeleted = database.delete(NewsContract.NewsEntry.TABLE_NAME, selection, selectionArgs);
        }
        if (rowsDeleted != 0) //noinspection ConstantConditions
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(NewsContract.NewsEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) //noinspection ConstantConditions
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }
}
