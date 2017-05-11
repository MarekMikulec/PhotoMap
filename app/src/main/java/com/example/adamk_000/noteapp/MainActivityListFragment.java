package com.example.adamk_000.noteapp;

import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.LruCache;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivityListFragment extends ListFragment {

    private ArrayList<Note> notes;
    private NoteAdapter noteAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstancesState){
        super.onActivityCreated(savedInstancesState);

        ArrayList<String> names = new ArrayList<>();
        MainActivity.readNames(MainActivity.getPath(), names);

        notes = new ArrayList<Note>();
        /*notes.add(new Note("Thids is a new note title", "This is the body of our note", Note.Category.PERSONAL));
        notes.add(new Note("Charmander says hello", "He is a fire type pokemon", Note.Category.TECHNICAL));
        notes.add(new Note("Squirtle says that you cant touch this", "He is a water type pokemon", Note.Category.QUETE));
        notes.add(new Note("Bulbasaur is tired, dont bother him", "He is nature type pokemon", Note.Category.FINANCE));*/

        for (String name:names){
            notes.add(new Note(name,NoteViewFragment.readMessage(name),Note.Category.PERSONAL));
        }


        noteAdapter = new NoteAdapter(getActivity(), notes);

        setListAdapter(noteAdapter);


    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        super.onListItemClick(l, v, position, id);

        launchNoteDetailActivity(position);
    }


    private void launchNoteDetailActivity(int position) {
        //Grab the note information associated with whatever note item we clicked on
        Note note = (Note) getListAdapter().getItem(position);

        //Create a new intent that launches our note activity
        Intent intent = new Intent(getActivity(), NoteDetailActivity.class);

        //pass along the info
        intent.putExtra(MainActivity.NOTE_TITLE_EXTRA, note.getTitle());
        intent.putExtra(MainActivity.NOTE_MESSAGE_EXTRA, note.getMessage());
        intent.putExtra(MainActivity.NOTE_CATEGORY_EXTRA, note.getCategory());
        intent.putExtra(MainActivity.NOTE_ID_EXTRA, note.getId());

        startActivity(intent);

    }



}
