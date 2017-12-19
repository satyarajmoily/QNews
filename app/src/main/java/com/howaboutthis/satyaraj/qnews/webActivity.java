package com.howaboutthis.satyaraj.qnews;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class webActivity extends AppCompatActivity {
    private WebView mWebview ;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        mWebview  = new WebView(this);

        WebSettings webSettings = mWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        Bundle url = getIntent().getExtras();
        assert url != null;
        String mUrl = url.getString("URL");

        mWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

            }
        });

        mWebview.loadUrl(mUrl);
        setContentView(mWebview );
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebview.canGoBack()) {
            mWebview.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
