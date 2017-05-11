package com.example.adamk_000.noteapp;


import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoteViewFragment extends Fragment {


    private Intent intent;
    private EditText message;
    TextWatcher textWatcher;

    public NoteViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentLayout = inflater.inflate(R.layout.fragment_note_view,
                container, false);

        TextView title = (TextView) fragmentLayout.findViewById(R.id.viewNoteTitle);
        message = (EditText) fragmentLayout.findViewById(R.id.viewNoteMessage);
        ImageView icon = (ImageView) fragmentLayout.findViewById(R.id.viewNoteIcon);

        intent = getActivity().getIntent();

        title.setText(intent.getExtras().getString(MainActivity.NOTE_TITLE_EXTRA));
        message.setText(readMessage(intent.getExtras().getString(MainActivity.NOTE_TITLE_EXTRA)));
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                writeMessage(message.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        message.addTextChangedListener(textWatcher);

        Note.Category noteCat = (Note.Category) intent.getSerializableExtra(MainActivity.NOTE_CATEGORY_EXTRA);
        //icon.setImageBitmap(Util.decodeSampledBitmapFromResource(getResources(),Note.categoryToDrawable(noteCat), 100, 100));
        File path = MainActivity.getPath();
        String sPath = path.getPath() + "/" + intent.getExtras().getString(MainActivity.NOTE_TITLE_EXTRA) + ".jpeg";
        icon.setImageBitmap(BitmapFactory.decodeFile(sPath));


        // Inflate the layout for this fragment
        return fragmentLayout;
    }

    public static String readMessage(String name) {
        String message = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(MainActivity.getPath() + "/" + name + ".txt"))) {
            reader.readLine();//name
            reader.readLine();//latitude
            reader.readLine();//longitude
            message = reader.readLine();

        } catch (IOException e) {
        }
        return message;
    }

    public void writeMessage(String text) {
        //write in memory informations, which we don't want to lose
        String name = null, latitude = null, longitude = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(MainActivity.getPath() + "/" + intent.getExtras().getString(MainActivity.NOTE_TITLE_EXTRA) + ".txt"))) {
            name = reader.readLine();
            latitude = reader.readLine();
            longitude = reader.readLine();

        } catch (IOException e) {
        }
        if (name != null && latitude != null && longitude != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(MainActivity.getPath() + "/" + intent.getExtras().getString(MainActivity.NOTE_TITLE_EXTRA) + ".txt"))) {
                writer.write(name);
                writer.newLine();
                writer.write(latitude);
                writer.newLine();
                writer.write(longitude);
                writer.newLine();
                writer.write(text);
            } catch (IOException e) {
            }
        }
    }



}
