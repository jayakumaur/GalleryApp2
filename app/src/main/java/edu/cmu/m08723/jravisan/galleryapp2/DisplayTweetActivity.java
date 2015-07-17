package edu.cmu.m08723.jravisan.galleryapp2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * Created by Jayakumaur on 16-07-2015.
 */
public class DisplayTweetActivity extends Activity {
    private WebView webView;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tweet_webview);
        Intent i = getIntent();

        // Get the statusMessage
//        int position = i.getExtras().getInt("position");

        String id = i.getStringExtra("tweetid");
        String username = i.getStringExtra("username");
        String mediaURL = i.getStringExtra("mediaURL");
        String url = "https://twitter.com/"+username+"/status/"+id.toString();
        String htmlContent = "<html><body><img src=\""+mediaURL+"\" height=100 width=100></body></html>";
        // Locate the ImageView in view_image.xml
        webView = (WebView) findViewById(R.id.tweetWebView);

        // Set the Settings for loading the tweet
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
//        webView.loadUrl(mediaURL);
        if(mediaURL!=null) {
            webView.loadData(htmlContent, "text/HTML", "UTF-8");
        }
        webView.setWebViewClient(new WebViewClient());
    }
}
