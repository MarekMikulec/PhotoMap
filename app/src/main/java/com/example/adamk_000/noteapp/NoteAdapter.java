package com.example.adamk_000.noteapp;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.provider.MediaStore;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by adamk_000 on 04.08.2016.
 */
public class NoteAdapter extends ArrayAdapter<Note> {
    //Constructor
    public NoteAdapter(Context context, ArrayList<Note> notes) {
        super(context, 0, notes);
        contentResolver = new ContentResolver(context) {
        };
    }

    static ContentResolver contentResolver;

    public static class ViewHolder {
        TextView title;
        TextView note;
        ImageView noteIcon;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get the data item for this position
        Note note = getItem(position);

        //creating new holder
        ViewHolder viewHolder;

        //Check if an existing view is being reused, otherwise inflate a new view from custom row layout
        if (convertView == null) {

            //if we dont have a view holder make sure you create the new one, save our view references too
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row, parent, false);

            // Grab references of views so we can populate them with specific note row data
            viewHolder.title = (TextView) convertView.findViewById(R.id.listItemNoteTitle);
            viewHolder.note = (TextView) convertView.findViewById(R.id.listItemNoteBody);
            viewHolder.noteIcon = (ImageView) convertView.findViewById(R.id.listItemNoteImg);

            convertView.setTag(viewHolder);

        } else {
            //we already have a view so jusz go to the viewholder and grab our widgets
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Fill each new referenced view with data associated with note its refering
        viewHolder.title.setText(note.getTitle());
        viewHolder.note.setText(note.getMessage());
        //viewHolder.noteIcon.setImageBitmap(Util.decodeSampledBitmapFromResource(getContext().getResources(),note.getAssociatedDrawable(), 100, 100));
        Bitmap thumbnail = this.makeThumbnail(note.getTitle());
        viewHolder.noteIcon.setImageBitmap(thumbnail);
        /*Drawable drawable = viewHolder.noteIcon.getDrawable();

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable != null) {
                Bitmap bitmap = bitmapDrawable.getBitmap();

                if (bitmap != null && !bitmap.isRecycled())
                    bitmap.recycle();
            }
        }*/

/*

        // Grab references of views so we can populate them with specific note row data
        TextView noteTitle = (TextView) convertView.findViewById(R.id.listItemNoteTitle);
        TextView noteText = (TextView) convertView.findViewById(R.id.listItemNoteBody);
        ImageView noteIcon = (ImageView) convertView.findViewById(R.id.listItemNoteImg);
*/
/*

        //Fill each new referenced view with data associated with note its refering
        noteTitle.setText(note.getTitle());
        noteText.setText(note.getMessage());
        noteIcon.setImageResource(note.getAssociatedDrawable());
*/

        //return so it will be displayed
        return convertView;
    }

    public static Bitmap makeThumbnail(String name) {
        File path = MainActivity.getPath();
        String sPath = path.getPath() + "/" + name + ".jpeg";

        String thumbnailPath = MainActivity.getThumbnails().getPath() + "/" + name + ".jpeg";

        Bitmap thumbnail;
        thumbnail = BitmapFactory.decodeFile(thumbnailPath);
        if (thumbnail == null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 50;
            thumbnail = BitmapFactory.decodeFile(sPath, options);
            if (thumbnail != null) {
                try (FileOutputStream out = new FileOutputStream(thumbnailPath)) {
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 50, out);
                    thumbnail.recycle();
                    thumbnail = BitmapFactory.decodeFile(thumbnailPath);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return thumbnail;
    }

}
