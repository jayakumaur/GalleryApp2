package edu.cmu.m08723.jravisan.galleryapp2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Jayakumaur on 14-07-2015.
 */
public class Tab3Fragment extends Fragment {
    List<Status> statuses;
    String user;
    View v;
    private String[] statusMessageList;
    ListView listView = null;
    ListViewAdapter listViewAdapter = null;
    Activity activity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.timeline, container, false);
        new TwitterTimeline().execute();
        activity = getActivity();
        return v;
    }

    class TwitterTimeline extends AsyncTask<String,String,Void>{
        protected Void doInBackground(String... args) {
            try {
                ConfigurationBuilder cb = new ConfigurationBuilder();
                cb.setDebugEnabled(true)
                        .setOAuthConsumerKey(Constants.CONSUMER_KEY)
                        .setOAuthConsumerSecret(Constants.CONSUMER_SECRET)
                        .setOAuthAccessToken(Constants.ACCESS_TOKEN)
                        .setOAuthAccessTokenSecret(Constants.ACCESS_TOKEN_SECRET);
                TwitterFactory tf = new TwitterFactory(cb.build());
                Twitter twitter = tf.getInstance();
                user = twitter.verifyCredentials().getScreenName();
                statuses = twitter.getUserTimeline(user);
                int count = statuses.size();
                Log.d("---1----->", String.valueOf(count) + "....");
                for(twitter4j.Status s:statuses){
                    Log.d("--------->",s.getText()+":"+s.getId()+":"+s.getURLEntities());
                }
                listView = (ListView) v.findViewById(R.id.listView);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listViewAdapter = new ListViewAdapter(activity, statuses);
                        listView.setAdapter(listViewAdapter);
                    }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                        Intent intent;
                        //Create intent based on the file type.
                        intent = new Intent(getActivity(), DisplayTweetActivity.class);

                        //Addition of status details to the intent.
                        intent.putExtra("username", statuses.get(position).getUser().getScreenName());
                        intent.putExtra("tweetid", String.valueOf(statuses.get(position).getId()));

                        //Start the respective details activity.
                        startActivity(intent);
                    }
                });
            } catch (TwitterException te) {
                te.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
//			/* Dismiss the progress dialog after sharing */
//            pDialog.dismiss();

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
