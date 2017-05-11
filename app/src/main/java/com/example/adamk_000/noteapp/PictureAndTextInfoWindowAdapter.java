package com.example.adamk_000.noteapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Acer on 10. 5. 2017.
 */

public class PictureAndTextInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    AppCompatActivity activityMaps;
    public PictureAndTextInfoWindowAdapter(AppCompatActivity activityMaps) {
        this.activityMaps = activityMaps;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;

    }

    @Override
    public View getInfoContents(Marker marker) {
        View popup = activityMaps.getLayoutInflater().inflate(R.layout.popup_map_info,null);
        TextView text = (TextView)popup.findViewById(R.id.text_map_detail);
        text.setText(marker.getTitle());
        ImageView image = (ImageView)popup.findViewById(R.id.image_map_detail);
        String thumbnailPath = MainActivity.getThumbnails().getPath() + "/" + marker.getTitle() + ".jpeg";
        Bitmap thumbnail;
        thumbnail = BitmapFactory.decodeFile(thumbnailPath);
        image.setImageBitmap(thumbnail);
        return popup;
    }
}
