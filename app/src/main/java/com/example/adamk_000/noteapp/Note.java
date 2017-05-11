package com.example.adamk_000.noteapp;

/**
 * Created by adamk_000 on 04.08.2016.
 */
public class Note {
    private String title, message;
    private long noteId, dateCreatedMilli;
    private Category category;



    public Note(String title, String message, Category category){
        this.title = title;
        this.message = message;
        this.category = category;
        this.noteId = 0;
        this.dateCreatedMilli = 0;
    }
    public enum Category{PERSONAL, TECHNICAL, QUETE, FINANCE, BUTTON}

    public Note(String title, String message, Category category, long noteId, long dateCreatedMilli){
        this.title = title;
        this.message = message;
        this.category = category;
        this.noteId = noteId;
        this.dateCreatedMilli = dateCreatedMilli;
    }

    public String getTitle(){
        return title;
    }

    public String getMessage(){
        return message;
    }

    public Category getCategory(){
        return category;
    }

    public long getDate(){return dateCreatedMilli;}

    public long getId(){return noteId;}

    public String toString(){
        return "ID: " + noteId + " Title: " + title + " Message: "
                + message + " IconId: " + category.name() + " Date: ";
    }

    public int getAssociatedDrawable(){
        return categoryToDrawable(category);
    }

    public static int categoryToDrawable(Category noteCategory){

        switch (noteCategory){
            case PERSONAL:
                return R.mipmap.pika;
            case TECHNICAL:
                return R.mipmap.charm;
            case FINANCE:
                return R.mipmap.bulb;
            case QUETE:
                return R.mipmap.squirt;
            case BUTTON:
                return R.mipmap.button;
            //case OWN:


        }
        return R.mipmap.pika;
    }

}
