package com.cmu.jayakumaur.galleryapp;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by Jayakumaur on 03-07-2015.
 */
public class GridViewAdapter extends BaseAdapter {
    private Context context;
    private int layoutResourceId;
    private String[] fileNameStrings;
    private String[] filePathStrings;
    private Integer[] fileNameExtensions;
    LayoutInflater inflater = null;

    public GridViewAdapter(Activity activity, String[]fname, String[] fpath, Integer[] fextension) {
//        super(activity, layoutResourceId, fpath);
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.fileNameStrings = fname;
        this.layoutResourceId = layoutResourceId;
        this.filePathStrings = fpath;
        this.fileNameExtensions = fextension;
    }
    public int getCount() {
        return filePathStrings.length;

    }
    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ImageView image;
        VideoView video;
        int layout_id = R.layout.gallery_image;
        int detail_layout_id = R.id.imageView;

        row = inflater.inflate(R.layout.gallery_image, parent, false);
        image = (ImageView) row.findViewById(R.id.imageView);
        row.setTag(image);
        Bitmap thumbImage;
        //If the file is a Video
        if(fileNameExtensions[position] == 3){
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inSampleSize = 1;
            thumbImage = ThumbnailUtils.createVideoThumbnail(filePathStrings[position],
                    MediaStore.Video.Thumbnails.MICRO_KIND);
        }

        //If the file is an Image
        else {
//        Bitmap bmp = BitmapFactory.decodeFile(filePathStrings[position]);
        Bitmap bmp = decodeFile(new File(filePathStrings[position]));
        thumbImage = ThumbnailUtils.extractThumbnail(bmp, 100, 100);
        }
        image.setImageBitmap(thumbImage);
        return row;
    }
    /*
        *Method - decodeFile
        * Method to decode the images and scale them down
        * to reduce memory consumption.
     */
    private Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE=40;

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
}
