package edu.cmu.m08723.jravisan.galleryapp2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import twitter4j.Status;

/**
 * Created by Jayakumaur on 15-07-2015.
 */
public class ListViewAdapter extends BaseAdapter{

    private Context context;
    private int layoutResourceId;
    private List<Status> statusList;
    private String statusText;
    private String username;
    private String datetime;
    LayoutInflater inflater = null;

    public ListViewAdapter(Activity activity, List<Status> pStatusList) {
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        statusList = pStatusList;
    }
    public int getCount() {
        return statusList.size();
    }
//    @Override
    public Object getItem(int position) {
        return statusList.get(position);
    }

//    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItems(List<Status> newList){
        statusList.clear();
        statusList.addAll(newList);
    }
//    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TextView statusView,usernameView;
        Status status = statusList.get(position);
        int layout_id = R.layout.timeline_item;
        int detail_layout_id = R.id.status;
        row = inflater.inflate(R.layout.timeline_item, parent, false);
        statusView = (TextView) row.findViewById(R.id.status);
        //usernameView = (TextView) row.findViewById(R.id.username);
        row.setTag(statusView);
        //usernameView.setText("@"+status.getUser().getScreenName()+status.getCreatedAt());
        statusView.setText(status.getText());
        return row;
    }
}
