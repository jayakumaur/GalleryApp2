package edu.cmu.m08723.jravisan.galleryapp2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
/**
 * Created by Jayakumaur on 14-07-2015.
 */
public class TweetActivity extends Activity implements View.OnClickListener {

    /* Shared preference keys */
    private static final String PREF_NAME = "sample_twitter_pref";
    private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    private static final String PREF_USER_NAME = "twitter_user_name";

    /* Any number for uniquely distinguish your request */
    public static final int WEBVIEW_REQUEST_CODE = 100;

    private ProgressDialog pDialog;

    private static Twitter twitter;
    private static RequestToken requestToken;

    private static SharedPreferences mSharedPreferences;

    private EditText mShareEditText;
    private TextView userName;
    private ImageView imageView;
    private View loginLayout;
    private View shareLayout;

    private String consumerKey = null;
    private String consumerSecret = null;
    private String callbackUrl = null;
    private String oAuthVerifier = null;

    private String filePath = "";

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		/* initializing twitter parameters from string.xml */
        initTwitterConfigs();

		/* Enabling strict mode */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

		/* Setting activity layout file */
        setContentView(R.layout.tweet);

        loginLayout = (RelativeLayout) findViewById(R.id.login_layout);
        shareLayout = (LinearLayout) findViewById(R.id.share_layout);
        mShareEditText = (EditText) findViewById(R.id.share_text);
        mShareEditText.setText(getDisplayText());
        imageView = (ImageView) findViewById(R.id.imageView);
        userName = (TextView) findViewById(R.id.user_name);


		/* register button click listeners */
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_share).setOnClickListener(this);

		/* Check if required twitter keys are set */
        if (TextUtils.isEmpty(consumerKey) || TextUtils.isEmpty(consumerSecret)) {
            Toast.makeText(this, "Twitter key and secret not configured",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = getIntent();
        filePath = i.getStringExtra("mediaFilePath");

        //Setting image preview
        Bitmap bmp = decodeFile(new File(filePath));
        // Set the decoded bitmap into ImageView
        imageView.setImageBitmap(bmp);

		/* Initialize application preferences */
        mSharedPreferences = getSharedPreferences(PREF_NAME, 0);

        boolean isLoggedIn = mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);

		/*  if already logged in, then hide login layout and show share layout */
        if (isLoggedIn) {
            loginLayout.setVisibility(View.GONE);
            shareLayout.setVisibility(View.VISIBLE);

            String username = mSharedPreferences.getString(PREF_USER_NAME, "");
            userName.setText(getResources ().getString(R.string.hello)
                    + username);

        } else {
            loginLayout.setVisibility(View.VISIBLE);
            shareLayout.setVisibility(View.GONE);

            Uri uri = getIntent().getData();

            if (uri != null && uri.toString().startsWith(callbackUrl)) {

                String verifier = uri.getQueryParameter(oAuthVerifier);

                try {

					/* Getting oAuth authentication token */
                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

					/* Getting user id form access token */
                    long userID = accessToken.getUserId();
                    final User user = twitter.showUser(userID);
                    final String username = user.getName();

					/* save updated token */
                    saveTwitterInfo(accessToken);

                    loginLayout.setVisibility(View.GONE);
                    shareLayout.setVisibility(View.VISIBLE);
                    userName.setText(getString(R.string.hello) + username);

                } catch (Exception e) {
                    Log.e("Failed Login", e.getMessage());
                }
            }

        }
    }

    private Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE=540;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }
    /**
     * Saving user information, after user is authenticated for the first time.
     * You don't need to show user to login, until user has a valid access toen
     */
    private void saveTwitterInfo(AccessToken accessToken) {

        long userID = accessToken.getUserId();

        User user;
        try {
            user = twitter.showUser(userID);

            String username = user.getName();

			/* Storing oAuth tokens to shared preferences */
            SharedPreferences.Editor e = mSharedPreferences.edit();
            e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
            e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
            e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
            e.putString(PREF_USER_NAME, username);
            e.commit();

        } catch (TwitterException e1) {
            e1.printStackTrace();
        }
    }

    /* Reading twitter essential configuration parameters from strings.xml */
    private void initTwitterConfigs() {
        consumerKey = Constants.CONSUMER_KEY;
        consumerSecret = Constants.CONSUMER_SECRET;
        callbackUrl = Constants.CALLBACK_URL;
        oAuthVerifier = Constants.OAUTH_VERIFIER;
    }


    private void loginToTwitter() {
        boolean isLoggedIn = mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);

        if (!isLoggedIn) {
            final ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(consumerKey);
            builder.setOAuthConsumerSecret(consumerSecret);

            final Configuration configuration = builder.build();
            final TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();

            try {
                requestToken = twitter.getOAuthRequestToken(callbackUrl);

                /**
                 *  Loading twitter login page on webview for authorization
                 *  Once authorized, results are received at onActivityResult
                 *  */
                final Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.EXTRA_URL, requestToken.getAuthenticationURL());
                startActivityForResult(intent, WEBVIEW_REQUEST_CODE);

            } catch (TwitterException e) {
                e.printStackTrace();
            }
        } else {

            loginLayout.setVisibility(View.GONE);
            shareLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            String verifier = data.getExtras().getString(oAuthVerifier);
            try {
                AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

                long userID = accessToken.getUserId();
                final User user = twitter.showUser(userID);
                String username = user.getName();

                saveTwitterInfo(accessToken);

                loginLayout.setVisibility(View.GONE);
                shareLayout.setVisibility(View.VISIBLE);
                userName.setText(getResources().getString(
                        R.string.hello) + username);

            } catch (Exception e) {
                Log.e("Twitter Login Failed", e.getMessage());
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        Log.d("---------->>", "Inside Onclick()");
        switch (v.getId()) {
            case R.id.btn_login:
                loginToTwitter();
                break;
            case R.id.btn_share:
                final String status = mShareEditText.getText().toString();

                if (status.trim().length() > 0) {
                    new updateTwitterStatus().execute(status);
                } else {
                    Toast.makeText(this, "Empty Message", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //to get the custome display text to be set as the Tweet Message
    public String getDisplayText(){
        //Andrew ID
        String andrewID = getString(R.string.andrewID);
        //Timestamp
        String timestamp = new SimpleDateFormat(getString(R.string.datetimeFormat)).format(new Date());
        // Device model
        String PhoneModel = android.os.Build.MODEL;
        // Android version
        String AndroidVersion = android.os.Build.VERSION.RELEASE;
        //Android API Level
        int APILevel = (android.os.Build.VERSION.SDK_INT);
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();

        //Final text to be displayed
        String displayText = andrewID+" :"+PhoneModel+"-Android "+AndroidVersion+"-API Level "+APILevel+" : "+timestamp+" "+tz.getDisplayName(false, TimeZone.SHORT);
        return displayText;
    }

    class updateTwitterStatus extends AsyncTask<String, String, Void> {
        private String mediaFilePath = filePath;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(TweetActivity.this);
            pDialog.setMessage("Posting to twitter...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Void doInBackground(String... args) {

            String status = args[0];
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(consumerKey);
                builder.setOAuthConsumerSecret(consumerSecret);

                // Access Token
                String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
                // Access Token Secret
                String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");

                AccessToken accessToken = new AccessToken(access_token, access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

                // Update status
                StatusUpdate statusUpdate = new StatusUpdate(status);
                statusUpdate.setMedia(new File(mediaFilePath));
                twitter4j.Status response = twitter.updateStatus(statusUpdate);
                Log.d("Status", response.getText());

            } catch (TwitterException e) {
                Log.d("Failed to post!", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

			/* Dismiss the progress dialog after sharing */
            pDialog.dismiss();

            Toast.makeText(TweetActivity.this, "Posted to Twitter!", Toast.LENGTH_SHORT).show();

            // Clearing EditText field
            mShareEditText.setText("");
        }

    }
}