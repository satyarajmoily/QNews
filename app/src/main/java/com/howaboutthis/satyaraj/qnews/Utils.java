package com.howaboutthis.satyaraj.qnews;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


class Utils {
    private static final String LOG_TAG = Utils.class.getSimpleName();

    static List<ListItemEvent> fetchNewsData(String requestUrl) {
        List<ListItemEvent> mListItem;
        URL url = createUrl(requestUrl);

        mListItem = new ArrayList<>();

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        }catch (IOException e){
            Log.e(LOG_TAG,"Error closing iput stream",e);
        }

        return extractFeatureFromJson(jsonResponse,mListItem);

    }

    private static List<ListItemEvent> extractFeatureFromJson(String jsonResponse, List<ListItemEvent> mListItem) {
        if (TextUtils.isEmpty(jsonResponse)){
            return null;
        }
        try{
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONArray articlesArray = baseJsonResponse.getJSONArray("articles");
            ListItemEvent mlistItem;
            JSONObject object;
            String title;
            String description;
            String url;
            String urlToImage;
            String time;
            long timeInMilliseconds = 0;
            for(int i=0; i<articlesArray.length();i++){
                object = articlesArray.getJSONObject(i);

                title = object.getString("title");
                description = object.getString("description");
                url = object.getString("url");
                urlToImage = object.getString("urlToImage");
                time = object.getString("publishedAt");

                if(time!=null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    try {
                        Date mDate = sdf.parse(time);
                        timeInMilliseconds = mDate.getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                mlistItem = new ListItemEvent(title, description, url, urlToImage,timeInMilliseconds);

                mListItem.add(mlistItem);

            }
            return mListItem;

        } catch (JSONException e) {
            Log.e(LOG_TAG,"Problem parsing the News JSON results",e);
        }
        return null;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse ="";

        if(url ==null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream= null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode()==200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else{
                Log.e(LOG_TAG,"Error response code: "+ urlConnection.getResponseCode());
            }
        } catch (IOException e) {
           Log.e(LOG_TAG,"Problem retrieving the News JSON results.",e);
        }finally {
            if (urlConnection!=null){
                urlConnection.disconnect();
            }
            if (inputStream!=null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if(inputStream!=null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line !=null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static URL createUrl(String requestUrl) {
        URL url = null;
        try{
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG,"Error with creating URL", e);
        }
        return url;
    }

}
