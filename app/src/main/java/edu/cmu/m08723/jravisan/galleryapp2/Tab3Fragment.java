package edu.cmu.m08723.jravisan.galleryapp2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Jayakumaur on 14-07-2015.
 */
public class Tab3Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    List<Status> statuses;
    String user;
    View v;
    TwitterFactory tf;
    Twitter twitter;
    private String[] statusMessageList;
    ListView listView = null;
    ListViewAdapter listViewAdapter = null;
    SwipeRefreshLayout swipeRefreshLayout;
    Activity activity;
    SharedPreferences mSharedPreferences;

    private static final String PREF_NAME = "sample_twitter_pref";
    private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    private static final String PREF_USER_NAME = "twitter_user_name";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.timeline, container, false);

        //Initialise and render the ListView
        listView = (ListView) v.findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);

        new TwitterTimeline().execute();

        swipeRefreshLayout.setOnRefreshListener(Tab3Fragment.this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                new TwitterUpdate().execute();
            }
        });
        activity = getActivity();
        return v;
    }
    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        listViewAdapter.notifyDataSetChanged();
        statuses.clear();
        new TwitterUpdate().execute();
    }

    class TwitterUpdate extends AsyncTask<String,String,Void> {
        protected Void doInBackground(String... args) {
            // showing refresh animation before making http call
            swipeRefreshLayout.setRefreshing(true);
            try {

                statuses = twitter.getUserTimeline(user);
                for (twitter4j.Status s : statuses) {
                    Log.d("--------->", s.getText() + ":" + s.getId());
                }
            } catch (TwitterException te) {
                te.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("---EXEC-->", "postexecute");
            listViewAdapter.addItems(statuses);
            listViewAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    class TwitterTimeline extends AsyncTask<String,String,Void>{
        protected Void doInBackground(String... args) {

                //Perform the initial OAuth configurations
                ConfigurationBuilder cb = new ConfigurationBuilder();
                cb.setDebugEnabled(true)
                        .setOAuthConsumerKey(Constants.CONSUMER_KEY)
                        .setOAuthConsumerSecret(Constants.CONSUMER_SECRET)
                        .setOAuthAccessToken(Constants.ACCESS_TOKEN)
                        .setOAuthAccessTokenSecret(Constants.ACCESS_TOKEN_SECRET);
                tf = new TwitterFactory(cb.build());
                twitter = tf.getInstance();
                try {
                    mSharedPreferences = getActivity().getSharedPreferences(PREF_NAME, 0);
//                    boolean isLoggedIn = mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
//                    user = twitter.verifyCredentials().getScreenName();
                    user = mSharedPreferences.getString("twitter_user_name",getString(R.string.twitter_handle));

                    statuses = twitter.getUserTimeline(user);
                }catch (TwitterException e){
                    e.printStackTrace();
                }

                if (getActivity()==null)
                    return null;
                else
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listViewAdapter = new ListViewAdapter(activity, statuses);
                            listView.setAdapter(listViewAdapter);
                        }
                    });

                //Implementing the OnItemClickListener to open up
                // the web view when a tweet is clicked upon
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        Intent intent;
                        //Create intent based on the file type.
                        intent = new Intent(getActivity(), TimelineActivity.class);
                        //Addition of status details to the intent.
                        intent.putExtra("username", statuses.get(position).getUser().getScreenName());
                        intent.putExtra("tweetid", String.valueOf(statuses.get(position).getId()));
                        try {
                            if (statuses.get(position).getMediaEntities().length > 0) {
                                intent.putExtra("mediaURL", statuses.get(position).getMediaEntities()[0].getMediaURL());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //Start the respective details activity.
                        startActivity(intent);
                    }
                });
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
