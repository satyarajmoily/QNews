
package com.howaboutthis.satyaraj.qnews.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class NewsContract {


    private NewsContract() {}

    static final String CONTENT_AUTHORITY = "com.howaboutthis.satyaraj.qnews";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    static final String PATH_NEWS = "news";

    public static final class NewsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NEWS);


        static final String CONTENT_LIST_TYPE =


                                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEWS;


        static final String CONTENT_ITEM_TYPE =


                                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEWS;


        /** Name of database table for news */

        final static String TABLE_NAME = "news";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_NEWS_TITLE ="title";

        public final static String COLUMN_NEWS_DESCRIPTION = "description";

        public final static String COLUMN_NEWS_URL = "url" ;

        public final static String COLUMN_NEWS_TIME = "time";

    }

}

